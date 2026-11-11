package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.DashboardDTO;
import edu.sv.ues.dam235.equipstock_api.dtos.MantenimientoDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import edu.sv.ues.dam235.equipstock_api.entities.Mantenimiento;
import edu.sv.ues.dam235.equipstock_api.repositories.EquipoRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.MantenimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EquipoRepository equipoRepository;
    private final MantenimientoRepository mantenimientoRepository;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // Métricas de equipos
        dto.setTotalEquipos(equipoRepository.count());
        dto.setEquiposOperativos(
                equipoRepository.countByEstado(Equipo.Estado.OPERATIVO));
        dto.setEquiposEnMantenimiento(
                equipoRepository.countByEstado(Equipo.Estado.EN_MANTENIMIENTO));
        dto.setEquiposInactivos(
                equipoRepository.countByEstado(Equipo.Estado.INACTIVO));

        // Métricas de mantenimientos
        dto.setMantenimientosCompletados(
                mantenimientoRepository.countByEstado(Mantenimiento.Estado.COMPLETADO));

        // Fecha actual de El Salvador
        LocalDate hoy = LocalDate.now(ZoneId.of("America/El_Salvador"));

    // Mantenimientos en proceso cuya fecha aún no ha vencido
        dto.setMantenimientosEnProceso(
                mantenimientoRepository.countByEstadoAndFechaProgramadaGreaterThanEqual(
                        Mantenimiento.Estado.EN_PROCESO, hoy));

    // Mantenimientos en proceso cuya fecha ya venció
        dto.setMantenimientosVencidos(
                mantenimientoRepository.countByEstadoAndFechaProgramadaBefore(
                        Mantenimiento.Estado.EN_PROCESO, hoy));

        // Actividad reciente
        List<Mantenimiento> recientes = mantenimientoRepository
                .findTop5ByOrderByCreatedAtDesc();
        dto.setActividadReciente(recientes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(dto);
    }

    private MantenimientoDTO toDTO(Mantenimiento m) {
        MantenimientoDTO dto = new MantenimientoDTO();
        dto.setId(m.getId());
        dto.setTipo(m.getTipo().name());
        dto.setEstado(m.getEstado().name());
        dto.setFechaProgramada(m.getFechaProgramada());
        dto.setDescripcion(m.getDescripcion());
        if (m.getEquipo() != null) {
            dto.setEquipoId(m.getEquipo().getId());
            dto.setEquipoNombre(m.getEquipo().getNombre());
        }
        if (m.getTecnico() != null) {
            dto.setTecnicoId(m.getTecnico().getId());
            dto.setTecnicoNombre(m.getTecnico().getNombre());
        }
        return dto;
    }
}