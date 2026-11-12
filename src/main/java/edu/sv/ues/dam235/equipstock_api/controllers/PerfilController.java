package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.CambioPasswordDTO;
import edu.sv.ues.dam235.equipstock_api.dtos.PerfilDTO;
import edu.sv.ues.dam235.equipstock_api.dtos.UsuarioDTO;
import edu.sv.ues.dam235.equipstock_api.entities.FotoPerfil;
import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import edu.sv.ues.dam235.equipstock_api.repositories.FotoPerfilRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private static final int MAX_FOTO_BYTES = 512 * 1024;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FotoPerfilRepository fotoRepository;

    /** Obtiene el perfil del usuario autenticado. */
    @GetMapping
    public ResponseEntity<UsuarioDTO> obtener(@AuthenticationPrincipal Usuario actual) {
        return usuarioRepository.findById(actual.getId())
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Actualiza nombre y teléfono del usuario autenticado. El correo no se puede cambiar. */
    @PutMapping
    public ResponseEntity<UsuarioDTO> actualizar(@AuthenticationPrincipal Usuario actual,
                                                 @Valid @RequestBody PerfilDTO dto) {
        return usuarioRepository.findById(actual.getId())
                .map(u -> {
                    u.setNombre(dto.getNombre().trim());
                    String telefono = dto.getTelefono() == null ? "" : dto.getTelefono().trim();
                    u.setTelefono(telefono.isEmpty() ? null : telefono);
                    return ResponseEntity.ok(toDTO(usuarioRepository.save(u)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** Cambia la contraseña del usuario autenticado, comprobando la actual. */
    @PutMapping("/password")
    public ResponseEntity<?> cambiarPassword(@AuthenticationPrincipal Usuario actual,
                                             @Valid @RequestBody CambioPasswordDTO dto) {
        Usuario u = usuarioRepository.findById(actual.getId()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();

        if (!passwordEncoder.matches(dto.getActual(), u.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña actual es incorrecta"));
        }
        if (passwordEncoder.matches(dto.getNueva(), u.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña debe ser distinta de la actual"));
        }
        u.setPassword(passwordEncoder.encode(dto.getNueva()));
        usuarioRepository.save(u);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/foto", consumes = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> subirFoto(@AuthenticationPrincipal Usuario actual,
                                       @RequestBody byte[] datos) {
        if (datos == null || datos.length < 4 || datos.length > MAX_FOTO_BYTES) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La imagen debe pesar menos de 512 KB"));
        }
        // Un JPEG empieza siempre con los bytes FF D8 FF
        if ((datos[0] & 0xFF) != 0xFF || (datos[1] & 0xFF) != 0xD8 || (datos[2] & 0xFF) != 0xFF) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo no es una imagen JPEG válida"));
        }
        Usuario u = usuarioRepository.findById(actual.getId()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();

        FotoPerfil foto = fotoRepository.findById(u.getId()).orElseGet(FotoPerfil::new);
        foto.setUsuarioId(u.getId());
        foto.setDatos(datos);
        fotoRepository.save(foto);

        u.setFotoActualizada(System.currentTimeMillis());
        return ResponseEntity.ok(toDTO(usuarioRepository.save(u)));
    }

    @GetMapping("/foto")
    public ResponseEntity<byte[]> bajarFoto(@AuthenticationPrincipal Usuario actual) {
        return fotoRepository.findById(actual.getId())
                .map(f -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(f.getDatos()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/foto")
    public ResponseEntity<UsuarioDTO> eliminarFoto(@AuthenticationPrincipal Usuario actual) {
        Usuario u = usuarioRepository.findById(actual.getId()).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();

        fotoRepository.deleteById(u.getId());
        u.setFotoActualizada(null);
        return ResponseEntity.ok(toDTO(usuarioRepository.save(u)));
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol().name());
        dto.setFotoUrl(u.getFotoUrl());
        dto.setActivo(u.getActivo());
        dto.setFotoActualizada(u.getFotoActualizada());
        return dto;
    }
}