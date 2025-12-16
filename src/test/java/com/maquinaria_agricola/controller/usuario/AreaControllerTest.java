package com.maquinaria_agricola.controller.usuario;

import com.maquinaria_agricola.DTO.usuario.AreaDTO;
import com.maquinaria_agricola.hateoas.AreaModelAssembler;
import com.maquinaria_agricola.mapper.AreaMapper;
import com.maquinaria_agricola.model.ResponseWrapper;
import com.maquinaria_agricola.model.usuario.Area;
import com.maquinaria_agricola.service.usuario.AreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AreaControllerTest {

    private AreaService areaService;
    private AreaMapper areaMapper;
    private AreaModelAssembler areaModelAssembler;
    private AreaController areaController;

    @BeforeEach
    void setUp() {
        areaService = mock(AreaService.class);
        areaMapper = mock(AreaMapper.class);
        areaModelAssembler = mock(AreaModelAssembler.class);
        areaController = new AreaController(areaService, areaMapper, areaModelAssembler);
    }

    @Test
    void getAllAreas_ReturnsAreas() {
        Area area1 = new Area();
        area1.setId(UUID.randomUUID());
        area1.setNombre("Área 1");
        Area area2 = new Area();
        area2.setId(UUID.randomUUID());
        area2.setNombre("Área 2");

        when(areaService.getAllAreas()).thenReturn(List.of(area1, area2));
        when(areaModelAssembler.toModel(area1)).thenReturn(EntityModel.of(area1));
        when(areaModelAssembler.toModel(area2)).thenReturn(EntityModel.of(area2));

        ResponseEntity<CollectionModel<EntityModel<Area>>> response = areaController.getAllAreas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
        verify(areaService).getAllAreas();
    }

    @Test
    void getAllAreas_NoAreas_ReturnsNoContent() {
        when(areaService.getAllAreas()).thenReturn(List.of());

        ResponseEntity<CollectionModel<EntityModel<Area>>> response = areaController.getAllAreas();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAreaById_ReturnsArea() {
        UUID id = UUID.randomUUID();
        Area area = new Area();
        area.setId(id);
        area.setNombre("Área Test");

        when(areaService.getAreaById(id)).thenReturn(area);
        when(areaModelAssembler.toModel(area)).thenReturn(EntityModel.of(area));

        ResponseEntity<EntityModel<Area>> response = areaController.getAreaById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(area.getId(), response.getBody().getContent().getId());
        verify(areaService).getAreaById(id);
    }

    @Test
    void createArea_ReturnsCreatedArea() {
        AreaDTO areaDTO = new AreaDTO();
        Area area = new Area();
        area.setNombre("Nueva Área");

        when(areaMapper.toEntity(areaDTO)).thenReturn(area);
        when(areaService.createArea(area)).thenReturn(area);
        when(areaModelAssembler.toModel(area)).thenReturn(EntityModel.of(area));

        ResponseEntity<EntityModel<Area>> response = areaController.createArea(areaDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(area.getNombre(), response.getBody().getContent().getNombre());
        verify(areaService).createArea(area);
    }

    @Test
    void createArea_NullDto_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> areaController.createArea(null));
    }

    @Test
    void deleteArea_ReturnsResponseWrapper() {
        UUID id = UUID.randomUUID();

        ResponseEntity<?> response = areaController.deleteArea(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseWrapper<?> body = (ResponseWrapper<?>) response.getBody();
        verify(areaService).deleteArea(id);
    }

    @Test
    void deleteArea_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> areaController.deleteArea(null));
    }
}
