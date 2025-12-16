package com.maquinaria_agricola.mapper;

import com.maquinaria_agricola.DTO.usuario.RolDTO;
import com.maquinaria_agricola.model.usuario.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RolMapperTest {

    // Instancia directa (sin @Autowired ni @Mock)
    private final RolMapper rolMapper = new RolMapper();

    // --- TEST: Entidad -> DTO ---
    @Test
    @DisplayName("toDTO: Debería mapear correctamente de Entidad a DTO")
    void toDTO_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol entity = new Rol();
        entity.setId(id);
        entity.setNombre("ROLE_ADMIN");

        // WHEN
        RolDTO dto = rolMapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId(), "El ID debe ser idéntico");
        assertEquals("ROLE_ADMIN", dto.getNombre(), "El nombre debe ser idéntico");
    }

    @Test
    @DisplayName("toDTO: Debería lanzar NullPointerException si la entidad es nula")
    void toDTO_InputNull_Falla() {
        // GIVEN
        Rol inputNulo = null;

        // WHEN & THEN
        // Tu código accede a input.getId() sin validar, así que esperamos NPE
        assertThrows(NullPointerException.class, () -> {
            rolMapper.toDTO(inputNulo);
        });
    }

    // --- TEST: DTO -> Entidad ---
    @Test
    @DisplayName("toEntity: Debería mapear correctamente de DTO a Entidad")
    void toEntity_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        RolDTO dto = new RolDTO();
        dto.setId(id);
        dto.setNombre("ROLE_USER");

        // WHEN
        Rol entity = rolMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId(), "El ID debe ser idéntico");
        assertEquals("ROLE_USER", entity.getNombre(), "El nombre debe ser idéntico");
    }

    @Test
    @DisplayName("toEntity: Debería lanzar NullPointerException si el DTO es nulo")
    void toEntity_InputNull_Falla() {
        // GIVEN
        RolDTO inputNulo = null;

        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> {
            rolMapper.toEntity(inputNulo);
        });
    }
}