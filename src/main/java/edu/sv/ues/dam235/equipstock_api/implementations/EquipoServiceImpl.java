package edu.sv.ues.dam235.equipstock_api.implementations;

import edu.sv.ues.dam235.equipstock_api.entities.Equipo;
import edu.sv.ues.dam235.equipstock_api.repositories.CategoriaRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.EquipoRepository;
import edu.sv.ues.dam235.equipstock_api.repositories.UbicacionRepository;
import edu.sv.ues.dam235.equipstock_api.services.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UbicacionRepository ubicacionRepository;

    @Override
    public List<Equipo> listarTodos() {
        return equipoRepository.findAll();
    }

    @Override
    public Optional<Equipo> buscarPorId(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    public List<Equipo> buscarPorNombre(String nombre) {
        return equipoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Equipo> buscarPorCategoria(Long categoriaId) {
        return equipoRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public List<Equipo> buscarPorEstado(Equipo.Estado estado) {
        return equipoRepository.findByEstado(estado);
    }

    @Override
    public Equipo guardar(Equipo equipo) {
        if (equipo.getCategoria() != null) {
            categoriaRepository.findById(equipo.getCategoria().getId())
                    .ifPresent(equipo::setCategoria);
        }
        if (equipo.getUbicacion() != null) {
            ubicacionRepository.findById(equipo.getUbicacion().getId())
                    .ifPresent(equipo::setUbicacion);
        }
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo actualizar(Long id, Equipo equipo) {
        Equipo existente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        existente.setNombre(equipo.getNombre());
        existente.setNumeroSerie(equipo.getNumeroSerie());
        existente.setCodigoActivo(equipo.getCodigoActivo());
        existente.setEstado(equipo.getEstado());
        existente.setImagenUrl(equipo.getImagenUrl());
        if (equipo.getCategoria() != null) {
            categoriaRepository.findById(equipo.getCategoria().getId())
                    .ifPresent(existente::setCategoria);
        }
        if (equipo.getUbicacion() != null) {
            ubicacionRepository.findById(equipo.getUbicacion().getId())
                    .ifPresent(existente::setUbicacion);
        }
        return equipoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        equipoRepository.deleteById(id);
    }
}