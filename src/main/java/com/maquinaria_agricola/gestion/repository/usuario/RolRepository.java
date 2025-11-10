package com.maquinaria_agricola.gestion.repository.usuario;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.gestion.model.usuario.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {

}
