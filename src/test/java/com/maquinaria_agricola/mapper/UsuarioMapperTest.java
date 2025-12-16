package com.maquinaria_agricola.mapper;

import com.maquinaria_agricola.DTO.usuario.UsuarioDTO;
import com.maquinaria_agricola.model.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    // Instancia directa (Unit Test puro)
    private final UsuarioMapper mapper = new UsuarioMapper();

    // --- TESTS: toEntity ---

    @Test
    @DisplayName("toEntity: Mapeo completo (con Rol y Area) crea estructura RoleUser")
    void toEntity_Completo() {
        // GIVEN
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(userId);
        dto.setUserName("jdoe");
        dto.setNombre("John");
        dto.setApellido("Doe");
        dto.setEmail("jdoe@mail.com");
        dto.setContrasena("123456");
        // Datos para la relación
        dto.setRoleId(roleId);
        dto.setAreaId(areaId);

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        // 1. Datos básicos
        assertEquals(userId, entity.getId());
        assertEquals("jdoe", entity.getUserName());

        // 2. Lógica compleja (RoleUser)
        assertNotNull(entity.getRole(), "Debería haberse creado el objeto RoleUser");
        assertNotNull(entity.getRole().getRol(), "Debería tener un Rol asignado");
        assertNotNull(entity.getRole().getArea(), "Debería tener un Area asignada");

        // 3. Verificamos los IDs anidados
        assertEquals(roleId, entity.getRole().getRol().getId());
        assertEquals(areaId, entity.getRole().getArea().getId());

        // 4. Verificamos la relación bidireccional (roleUser.setUsuario(usuario))
        assertEquals(entity, entity.getRole().getUsuario(), "La relación RoleUser -> Usuario debe estar vinculada");
    }

    @Test
    @DisplayName("toEntity: Mapeo básico (sin Rol/Area) deja RoleUser en null")
    void toEntity_Basico_SinIds() {
        // GIVEN
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUserName("guest");
        dto.setRoleId(null);
        dto.setAreaId(null);

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        assertEquals("guest", entity.getUserName());
        assertNull(entity.getRole(), "No debería crear RoleUser si faltan IDs");
    }

    @Test
    @DisplayName("toEntity: Mapeo parcial (solo un ID) no debe crear RoleUser")
    void toEntity_Parcial_SoloRoleId() {
        // GIVEN
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRoleId(UUID.randomUUID());
        dto.setAreaId(null); // Falta el Area

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        // La condición es (roleId != null && areaId != null), por tanto debe fallar la
        // condición
        assertNull(entity.getRole(), "La condición es estricta (AND), no debe crear RoleUser parcial");
    }

    @Test
    @DisplayName("toEntity: Input nulo lanza NPE")
    void toEntity_Null_Falla() {
        assertThrows(NullPointerException.class, () -> mapper.toEntity(null));
    }

    // --- TESTS: toDTO ---

    @Test
    @DisplayName("toDTO: Mapea datos básicos de Entidad a DTO")
    void toDTO_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Usuario entity = new Usuario();
        entity.setId(id);
        entity.setNombre("Jane");
        entity.setApellido("Doe");
        entity.setUserName("janedoe");
        entity.setEmail("jane@mail.com");
        entity.setContrasena("secret");
        // Nota: Tu método toDTO actual NO mapea el Rol ni el Area de vuelta al DTO,
        // así que no hace falta setearlos en el entity para esta prueba.

        // WHEN
        UsuarioDTO dto = mapper.toDTO(entity);

        // THEN
        assertEquals(id, dto.getId());
        assertEquals("Jane", dto.getNombre());
        assertEquals("janedoe", dto.getUserName());
        assertEquals("secret", dto.getContrasena());

        // Verificamos que los campos de relación estén nulos (según tu implementación
        // actual)
        assertNull(dto.getRoleId());
        assertNull(dto.getAreaId());
    }
}