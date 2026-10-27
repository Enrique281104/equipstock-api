package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.UsuarioDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import edu.sv.ues.dam235.equipstock_api.services.UsuarioService;
import edu.sv.ues.dam235.equipstock_api.utilities.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrar(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtUtil.generateToken(nuevo));
            response.put("usuario", toDTO(nuevo));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        return usuarioService.buscarPorEmail(email)
                .filter(u -> u.getActivo())
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", jwtUtil.generateToken(u));
                    response.put("usuario", toDTO(u));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).build());
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