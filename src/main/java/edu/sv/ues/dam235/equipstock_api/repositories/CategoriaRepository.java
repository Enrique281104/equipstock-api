package edu.sv.ues.dam235.equipstock_api.repositories;

import edu.sv.ues.dam235.equipstock_api.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}