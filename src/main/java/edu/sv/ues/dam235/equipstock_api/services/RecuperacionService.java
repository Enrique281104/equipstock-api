package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.CodigoRecuperacion;
import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import edu.sv.ues.dam235.equipstock_api.repositories.CodigoRecuperacionRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecuperacionService {

    private static final int MINUTOS_VIGENCIA = 15;
    private static final int MAX_INTENTOS = 5;
    private static final int SEGUNDOS_ENTRE_ENVIOS = 60;
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final String ERROR_CODIGO = "Código incorrecto o vencido";

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.username:}")
    private String remitente;

    /** Genera y envía el código. No avisa si el correo no existe. */
    @Transactional
    public void solicitarCodigo(String email) {
        if (email == null || email.isBlank()) return;
        email = email.trim();

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty() || !Boolean.TRUE.equals(usuario.get().getActivo())) return;

        LocalDateTime ahora = LocalDateTime.now(ZONA);
        Optional<CodigoRecuperacion> previo = codigoRepository.findByEmail(email);
        if (previo.isPresent()
                && previo.get().getCreadoEn().isAfter(ahora.minusSeconds(SEGUNDOS_ENTRE_ENVIOS))) {
            return;   // pidió otro hace menos de un minuto
        }

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        CodigoRecuperacion c = previo.orElseGet(CodigoRecuperacion::new);
        c.setEmail(email);
        c.setCodigoHash(passwordEncoder.encode(codigo));
        c.setExpira(ahora.plusMinutes(MINUTOS_VIGENCIA));
        c.setIntentos(0);
        c.setCreadoEn(ahora);
        codigoRepository.save(c);

        try {
            enviarCorreo(email, codigo);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de recuperación", e);   // el usuario no se entera
        }
    }

    /** Devuelve null si todo salió bien, o el mensaje de error. */
    @Transactional
    public String restablecer(String email, String codigo, String nueva) {
        if (nueva == null || nueva.length() < 8 || nueva.length() > 72) {
            return "La contraseña debe tener entre 8 y 72 caracteres";
        }
        if (email == null || codigo == null) return ERROR_CODIGO;
        email = email.trim();

        Optional<CodigoRecuperacion> opt = codigoRepository.findByEmail(email);
        if (opt.isEmpty()) return ERROR_CODIGO;
        CodigoRecuperacion c = opt.get();

        if (c.getExpira().isBefore(LocalDateTime.now(ZONA)) || c.getIntentos() >= MAX_INTENTOS) {
            codigoRepository.delete(c);
            return ERROR_CODIGO;
        }

        if (!passwordEncoder.matches(codigo.trim(), c.getCodigoHash())) {
            c.setIntentos(c.getIntentos() + 1);
            if (c.getIntentos() >= MAX_INTENTOS) codigoRepository.delete(c);
            else codigoRepository.save(c);
            return ERROR_CODIGO;
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            codigoRepository.delete(c);
            return ERROR_CODIGO;
        }
        usuario.get().setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario.get());
        codigoRepository.delete(c);   // el código sirve una sola vez
        return null;
    }

    private void enviarCorreo(String destino, String codigo) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(remitente);
        m.setTo(destino);
        m.setSubject("EquipStock: código para recuperar tu contraseña");
        m.setText("Tu código de verificación es: " + codigo
                + "\n\nVence en " + MINUTOS_VIGENCIA + " minutos."
                + "\nSi no solicitaste este cambio, ignora este mensaje.");
        mailSender.send(m);
    }
}