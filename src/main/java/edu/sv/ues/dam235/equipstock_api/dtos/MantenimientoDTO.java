package edu.sv.ues.dam235.equipstock_api.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MantenimientoDTO {
    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private Long tecnicoId;
    private String tecnicoNombre;
    private String tipo;
    private String estado;
    private LocalDate fechaProgramada;
    private String descripcion;
    private String observaciones;
}
