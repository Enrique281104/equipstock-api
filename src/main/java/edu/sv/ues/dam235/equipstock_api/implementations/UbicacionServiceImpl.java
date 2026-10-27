package edu.sv.ues.dam235.equipstock_api.implementations;

import edu.sv.ues.dam235.equipstock_api.entities.Ubicacion;
import edu.sv.ues.dam235.equipstock_api.repositories.UbicacionRepository;
import edu.sv.ues.dam235.equipstock_api.services.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    @Override
    public List<Ubicacion> listarTodas() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Optional<Ubicacion> buscarPorId(Long id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    public List<Ubicacion> buscarPorNombre(String nombre) {
        return ubicacionRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    public Ubicacion actualizar(Long id, Ubicacion ubicacion) {
        Ubicacion existente = ubicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
        existente.setNombre(ubicacion.getNombre());
        existente.setDescripcion(ubicacion.getDescripcion());
        return ubicacionRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        ubicacionRepository.deleteById(id);
    }
}
