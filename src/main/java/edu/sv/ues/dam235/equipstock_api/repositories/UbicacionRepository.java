package edu.sv.ues.dam235.equipstock_api.repositories;

import edu.sv.ues.dam235.equipstock_api.entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    List<Ubicacion> findByNombreContainingIgnoreCase(String nombre);
}