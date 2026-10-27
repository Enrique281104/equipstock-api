package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.EquipoDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Categoria;
import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import edu.sv.ues.dam235.equipstock_api.entities.Ubicacion;
import edu.sv.ues.dam235.equipstock_api.services.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    public List<EquipoDTO> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String estado) {
        List<Equipo> equipos;
        if (nombre != null) {
            equipos = equipoService.buscarPorNombre(nombre);
        } else if (categoriaId != null) {
            equipos = equipoService.buscarPorCategoria(categoriaId);
        } else if (estado != null) {
            equipos = equipoService.buscarPorEstado(Equipo.Estado.valueOf(estado));
        } else {
            equipos = equipoService.listarTodos();
        }
        return equipos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDTO> buscarPorId(@PathVariable Long id) {
        return equipoService.buscarPorId(id)
                .map(e -> ResponseEntity.ok(toDTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EquipoDTO> crear(@RequestBody EquipoDTO dto) {
        Equipo equipo = toEntity(dto);
        return ResponseEntity.ok(toDTO(equipoService.guardar(equipo)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> actualizar(@PathVariable Long id,
                                                @RequestBody EquipoDTO dto) {
        try {
            Equipo actualizado = equipoService.actualizar(id, toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private EquipoDTO toDTO(Equipo e) {
        EquipoDTO dto = new EquipoDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setNumeroSerie(e.getNumeroSerie());
        dto.setCodigoActivo(e.getCodigoActivo());
        dto.setEstado(e.getEstado().name());
        dto.setImagenUrl(e.getImagenUrl());
        if (e.getCategoria() != null) {
            dto.setCategoriaId(e.getCategoria().getId());
            dto.setCategoriaNombre(e.getCategoria().getNombre());
        }
        if (e.getUbicacion() != null) {
            dto.setUbicacionId(e.getUbicacion().getId());
            dto.setUbicacionNombre(e.getUbicacion().getNombre());
        }
        return dto;
    }

    private Equipo toEntity(EquipoDTO dto) {
        Equipo e = new Equipo();
        e.setNombre(dto.getNombre());
        e.setNumeroSerie(dto.getNumeroSerie());
        e.setCodigoActivo(dto.getCodigoActivo());
        e.setImagenUrl(dto.getImagenUrl());
        if (dto.getEstado() != null) {
            e.setEstado(Equipo.Estado.valueOf(dto.getEstado()));
        }
        if (dto.getCategoriaId() != null) {
            Categoria cat = new Categoria();
            cat.setId(dto.getCategoriaId());
            e.setCategoria(cat);
        }
        if (dto.getUbicacionId() != null) {
            Ubicacion ub = new Ubicacion();
            ub.setId(dto.getUbicacionId());
            e.setUbicacion(ub);
        }
        return e;
    }
}
