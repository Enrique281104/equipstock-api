package edu.sv.ues.dam235.equipstock_api.repositories;

import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByNombreContainingIgnoreCase(String nombre);
    List<Equipo> findByCategoriaId(Long categoriaId);
    List<Equipo> findByUbicacionId(Long ubicacionId);
    List<Equipo> findByEstado(Equipo.Estado estado);
    long countByEstado(Equipo.Estado estado);
}