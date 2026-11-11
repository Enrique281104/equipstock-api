package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.CambioPasswordDTO;
import edu.sv.ues.dam235.equipstock_api.dtos.PerfilDTO;
import edu.sv.ues.dam235.equipstock_api.dtos.UsuarioDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import edu.sv.ues.dam235.equipstock_api.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol().name());
        dto.setFotoUrl(u.getFotoUrl());
        dto.setActivo(u.getActivo());
        return dto;
    }
}