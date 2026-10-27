package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.Ubicacion;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    List<Ubicacion> listarTodas();
    Optional<Ubicacion> buscarPorId(Long id);
    List<Ubicacion> buscarPorNombre(String nombre);
    Ubicacion guardar(Ubicacion ubicacion);
    Ubicacion actualizar(Long id, Ubicacion ubicacion);
    void eliminar(Long id);
}
