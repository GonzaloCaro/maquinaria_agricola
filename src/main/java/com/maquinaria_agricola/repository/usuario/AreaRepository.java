package com.maquinaria_agricola.repository.usuario;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maquinaria_agricola.model.usuario.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, UUID> {

}
