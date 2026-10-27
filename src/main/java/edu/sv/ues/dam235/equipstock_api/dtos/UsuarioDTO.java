package edu.sv.ues.dam235.equipstock_api.dtos;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private String fotoUrl;
    private Boolean activo;
}