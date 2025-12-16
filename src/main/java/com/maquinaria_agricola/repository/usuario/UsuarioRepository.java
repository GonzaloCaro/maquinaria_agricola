package com.maquinaria_agricola.repository.usuario;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.model.usuario.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUserName(String userName);
    Optional<Usuario> findByEmail(String email);

}
