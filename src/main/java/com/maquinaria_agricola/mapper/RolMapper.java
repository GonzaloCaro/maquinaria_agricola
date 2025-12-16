package com.maquinaria_agricola.mapper;

import org.springframework.stereotype.Component;

import com.maquinaria_agricola.DTO.usuario.RolDTO;
import com.maquinaria_agricola.model.usuario.Rol;

@Component
public class RolMapper {
    public RolDTO toDTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setId(rol.getId());
        rolDTO.setNombre(rol.getNombre());
        return rolDTO;
    }

    public Rol toEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setId(rolDTO.getId());
        rol.setNombre(rolDTO.getNombre());
        return rol;
    }
}
