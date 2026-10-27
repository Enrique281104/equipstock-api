package edu.sv.ues.dam235.equipstock_api.dtos;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDTO {
    // Métricas generales
    private long totalEquipos;
    private long equiposOperativos;
    private long equiposEnMantenimiento;
    private long equiposInactivos;

    // Métricas de mantenimiento
    private long mantenimientosCompletados;
    private long mantenimientosEnProceso;
    private long mantenimientosVencidos;

    // Actividad reciente (últimos mantenimientos)
    private List<MantenimientoDTO> actividadReciente;
}