package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> listarTodas();
    Optional<Categoria> buscarPorId(Long id);
    List<Categoria> buscarPorNombre(String nombre);
    Categoria guardar(Categoria categoria);
    Categoria actualizar(Long id, Categoria categoria);
    void eliminar(Long id);
}