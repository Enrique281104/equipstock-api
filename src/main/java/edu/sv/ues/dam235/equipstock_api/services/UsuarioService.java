package edu.sv.ues.dam235.equipstock_api.services;

import edu.sv.ues.dam235.equipstock_api.entities.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
    Usuario actualizar(Long id, Usuario usuario);
    void desactivar(Long id);
}