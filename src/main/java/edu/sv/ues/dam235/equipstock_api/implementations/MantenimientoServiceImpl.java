package edu.sv.ues.dam235.equipstock_api.implementations;

import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import edu.sv.ues.dam235.equipstock_api.entities.Mantenimiento;
import edu.sv.ues.dam235.equipstock_api.repositories.EquipoRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.MantenimientoRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.UsuarioRepository;
import edu.sv.ues.dam235.equipstock_api.services.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Mantenimiento> listarTodos() {
        return mantenimientoRepository.findAll();
    }

    @Override
    public Optional<Mantenimiento> buscarPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }

    @Override
    public List<Mantenimiento> buscarPorEquipo(Long equipoId) {
        return mantenimientoRepository.findByEquipoId(equipoId);
    }

    @Override
    public List<Mantenimiento> buscarPorTecnico(Long tecnicoId) {
        return mantenimientoRepository.findByTecnicoId(tecnicoId);
    }

    @Override
    public List<Mantenimiento> buscarPorEstado(Mantenimiento.Estado estado) {
        return mantenimientoRepository.findByEstado(estado);
    }

    @Override
    public long contarPorEstado(Mantenimiento.Estado estado) {
        return mantenimientoRepository.countByEstado(estado);
    }

    @Override
    public Mantenimiento guardar(Mantenimiento mantenimiento) {
        Equipo equipo = equipoRepository.findById(mantenimiento.getEquipo().getId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        mantenimiento.setEquipo(equipo);
        if (mantenimiento.getTecnico() != null) {
            usuarioRepository.findById(mantenimiento.getTecnico().getId())
                    .ifPresent(mantenimiento::setTecnico);
        }
        return mantenimientoRepository.save(mantenimiento);
    }

    @Override
    public Mantenimiento actualizar(Long id, Mantenimiento mantenimiento) {
        Mantenimiento existente = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        existente.setTipo(mantenimiento.getTipo());
        existente.setEstado(mantenimiento.getEstado());
        existente.setFechaProgramada(mantenimiento.getFechaProgramada());
        existente.setDescripcion(mantenimiento.getDescripcion());
        existente.setObservaciones(mantenimiento.getObservaciones());
        if (mantenimiento.getTecnico() != null) {
            usuarioRepository.findById(mantenimiento.getTecnico().getId())
                    .ifPresent(existente::setTecnico);
        }
        return mantenimientoRepository.save(existente);
    }
}