package edu.sv.ues.dam235.equipstock_api.controllers;

import edu.sv.ues.dam235.equipstock_api.dtos.UbicacionDTO;
import edu.sv.ues.dam235.equipstock_api.entities.Ubicacion;
import edu.sv.ues.dam235.equipstock_api.services.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public List<UbicacionDTO> listar(@RequestParam(required = false) String nombre) {
        List<Ubicacion> ubicaciones = nombre != null
                ? ubicacionService.buscarPorNombre(nombre)
                : ubicacionService.listarTodas();
        return ubicaciones.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> buscarPorId(@PathVariable Long id) {
        return ubicacionService.buscarPorId(id)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UbicacionDTO> crear(@RequestBody UbicacionDTO dto) {
        Ubicacion ubicacion = toEntity(dto);
        return ResponseEntity.ok(toDTO(ubicacionService.guardar(ubicacion)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDTO> actualizar(@PathVariable Long id,
                                                   @RequestBody UbicacionDTO dto) {
        try {
            Ubicacion actualizada = ubicacionService.actualizar(id, toEntity(dto));
            return ResponseEntity.ok(toDTO(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private UbicacionDTO toDTO(Ubicacion u) {
        UbicacionDTO dto = new UbicacionDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setDescripcion(u.getDescripcion());
        return dto;
    }

    private Ubicacion toEntity(UbicacionDTO dto) {
        Ubicacion u = new Ubicacion();
        u.setNombre(dto.getNombre());
        u.setDescripcion(dto.getDescripcion());
        return u;
    }
}