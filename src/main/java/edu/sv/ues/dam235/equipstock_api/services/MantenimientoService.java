package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.Mantenimiento;
import java.util.List;
import java.util.Optional;

public interface MantenimientoService {
    List<Mantenimiento> listarTodos();
    Optional<Mantenimiento> buscarPorId(Long id);
    List<Mantenimiento> buscarPorEquipo(Long equipoId);
    List<Mantenimiento> buscarPorTecnico(Long tecnicoId);
    List<Mantenimiento> buscarPorEstado(Mantenimiento.Estado estado);
    long contarPorEstado(Mantenimiento.Estado estado);
    Mantenimiento guardar(Mantenimiento mantenimiento);
    Mantenimiento actualizar(Long id, Mantenimiento mantenimiento);
}
