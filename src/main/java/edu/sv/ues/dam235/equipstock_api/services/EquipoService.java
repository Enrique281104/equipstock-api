package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import java.util.List;
import java.util.Optional;

public interface EquipoService {
    List<Equipo> listarTodos();
    Optional<Equipo> buscarPorId(Long id);
    List<Equipo> buscarPorNombre(String nombre);
    List<Equipo> buscarPorCategoria(Long categoriaId);
    List<Equipo> buscarPorEstado(Equipo.Estado estado);
    Equipo guardar(Equipo equipo);
    Equipo actualizar(Long id, Equipo equipo);
    void eliminar(Long id);
}