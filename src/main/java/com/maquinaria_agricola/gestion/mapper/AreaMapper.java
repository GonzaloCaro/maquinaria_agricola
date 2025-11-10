package com.maquinaria_agricola.gestion.mapper;

import org.springframework.stereotype.Component;

import com.maquinaria_agricola.gestion.DTO.usuario.AreaDTO;
import com.maquinaria_agricola.gestion.model.usuario.Area;

@Component
public class AreaMapper {

    public Area toEntity(AreaDTO areaDTO) {
        Area area = new Area();
        area.setId(areaDTO.getId());
        area.setNombre(areaDTO.getNombre());
        return area;
    }

    public AreaDTO toDTO(Area area) {
        AreaDTO areaDTO = new AreaDTO();
        areaDTO.setId(area.getId());
        areaDTO.setNombre(area.getNombre());
        return areaDTO;
    }

}
