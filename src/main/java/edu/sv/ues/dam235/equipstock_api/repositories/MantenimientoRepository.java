package edu.sv.ues.dam235.equipstock_api.repositories;

import edu.sv.ues.dam235.equipstock_api.entities.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByEquipoId(Long equipoId);
    List<Mantenimiento> findByTecnicoId(Long tecnicoId);
    List<Mantenimiento> findByEstado(Mantenimiento.Estado estado);
    List<Mantenimiento> findTop5ByOrderByCreatedAtDesc();
    long countByEstado(Mantenimiento.Estado estado);
}