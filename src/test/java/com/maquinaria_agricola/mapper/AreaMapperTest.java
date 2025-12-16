package com.maquinaria_agricola.mapper;

import com.maquinaria_agricola.DTO.usuario.AreaDTO;
import com.maquinaria_agricola.model.usuario.Area;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AreaMapperTest {

    // Instanciamos el mapper directamente (POJO).
    // No hace falta @Autowired ni @Mock porque no tiene dependencias.
    private final AreaMapper areaMapper = new AreaMapper();

    // --- TEST: DTO -> Entity ---
    @Test
    @DisplayName("toEntity: Debería convertir correctamente de DTO a Entidad")
    void toEntity_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        AreaDTO dto = new AreaDTO();
        dto.setId(id);
        dto.setNombre("Mantenimiento");

        // WHEN
        Area entity = areaMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId(), "El ID debe coincidir");
        assertEquals("Mantenimiento", entity.getNombre(), "El nombre debe coincidir");
    }

    @Test
    @DisplayName("toEntity: Debería lanzar NullPointerException si el input es null")
    void toEntity_InputNull_LanzaExcepcion() {
        // GIVEN
        AreaDTO dtoNulo = null;

        // WHEN & THEN
        // Tu código actual accede a areaDTO.getId() sin verificar nulos,
        // así que esperamos un NPE.
        assertThrows(NullPointerException.class, () -> {
            areaMapper.toEntity(dtoNulo);
        });
    }

    // --- TEST: Entity -> DTO ---
    @Test
    @DisplayName("toDTO: Debería convertir correctamente de Entidad a DTO")
    void toDTO_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area entity = new Area();
        entity.setId(id);
        entity.setNombre("Logística");

        // WHEN
        AreaDTO dto = areaMapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId(), "El ID debe coincidir");
        assertEquals("Logística", dto.getNombre(), "El nombre debe coincidir");
    }

    @Test
    @DisplayName("toDTO: Debería lanzar NullPointerException si el input es null")
    void toDTO_InputNull_LanzaExcepcion() {
        // GIVEN
        Area entityNula = null;

        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> {
            areaMapper.toDTO(entityNula);
        });
    }
}