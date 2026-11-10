package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.MantenimientoDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import edu.sv.ues.dam235.equipstock_api.entities.Mantenimiento;
import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import edu.sv.ues.dam235.equipstock_api.services.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @GetMapping
    public List<MantenimientoDTO> listar(
            @RequestParam(required = false) Long equipoId,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) String estado) {
        List<Mantenimiento> mantenimientos;
        if (equipoId != null) {
            mantenimientos = mantenimientoService.buscarPorEquipo(equipoId);
        } else if (tecnicoId != null) {
            mantenimientos = mantenimientoService.buscarPorTecnico(tecnicoId);
        } else if (estado != null) {
            mantenimientos = mantenimientoService.buscarPorEstado(
                    Mantenimiento.Estado.valueOf(estado));
        } else {
            mantenimientos = mantenimientoService.listarTodos();
        }
        return mantenimientos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> buscarPorId(@PathVariable Long id) {
        return mantenimientoService.buscarPorId(id)
                .map(m -> ResponseEntity.ok(toDTO(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MantenimientoDTO> crear(@RequestBody MantenimientoDTO dto) {
        Mantenimiento m = toEntity(dto);
        return ResponseEntity.ok(toDTO(mantenimientoService.guardar(m)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> actualizar(@PathVariable Long id,
                                                       @RequestBody MantenimientoDTO dto) {
        try {
            Mantenimiento actualizado = mantenimientoService.actualizar(id, toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MantenimientoDTO> crear(@AuthenticationPrincipal Usuario actual,
                                                  @RequestBody MantenimientoDTO dto) {
        if (actual.getRol() == Usuario.Rol.TECNICO) {
            dto.setTecnicoId(actual.getId());          // un técnico solo se asigna a sí mismo
        }
        return ResponseEntity.ok(toDTO(mantenimientoService.guardar(toEntity(dto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> actualizar(@AuthenticationPrincipal Usuario actual,
                                                       @PathVariable Long id,
                                                       @RequestBody MantenimientoDTO dto) {
        Optional<Mantenimiento> existente = mantenimientoService.buscarPorId(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        if (actual.getRol() == Usuario.Rol.TECNICO) {
            Usuario asignado = existente.get().getTecnico();
            if (asignado == null || !asignado.getId().equals(actual.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // no es suyo
            }
            dto.setTecnicoId(actual.getId());          // no puede reasignar
        }
        try {
            return ResponseEntity.ok(toDTO(mantenimientoService.actualizar(id, toEntity(dto))));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private MantenimientoDTO toDTO(Mantenimiento m) {
        MantenimientoDTO dto = new MantenimientoDTO();
        dto.setId(m.getId());
        dto.setTipo(m.getTipo().name());
        dto.setEstado(m.getEstado().name());
        dto.setFechaProgramada(m.getFechaProgramada());
        dto.setDescripcion(m.getDescripcion());
        dto.setObservaciones(m.getObservaciones());
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

    private Mantenimiento toEntity(MantenimientoDTO dto) {
        Mantenimiento m = new Mantenimiento();
        if (dto.getTipo() != null) {
            m.setTipo(Mantenimiento.Tipo.valueOf(dto.getTipo()));
        }
        if (dto.getEstado() != null) {
            m.setEstado(Mantenimiento.Estado.valueOf(dto.getEstado()));
        }
        m.setFechaProgramada(dto.getFechaProgramada());
        m.setDescripcion(dto.getDescripcion());
        m.setObservaciones(dto.getObservaciones());
        if (dto.getEquipoId() != null) {
            Equipo e = new Equipo();
            e.setId(dto.getEquipoId());
            m.setEquipo(e);
        }
        if (dto.getTecnicoId() != null) {
            Usuario u = new Usuario();
            u.setId(dto.getTecnicoId());
            m.setTecnico(u);
        }
        return m;
    }
}