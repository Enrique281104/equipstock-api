package edu.sv.ues.dam235.equipstock_api.dtos;

import lombok.Data;

@Data
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
}