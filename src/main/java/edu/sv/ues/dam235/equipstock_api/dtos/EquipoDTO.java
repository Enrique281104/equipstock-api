package edu.sv.ues.dam235.equipstock_api.dtos;

import lombok.Data;

@Data
public class EquipoDTO {
    private Long id;
    private String nombre;
    private String numeroSerie;
    private String codigoActivo;
    private String estado;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private Long ubicacionId;
    private String ubicacionNombre;
}
