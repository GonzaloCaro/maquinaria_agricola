package com.maquinaria_agricola.hateoas;

import com.maquinaria_agricola.model.usuario.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RolModelAssemblerTest {

    // Instancia directa, prueba unitaria pura
    private final RolModelAssembler assembler = new RolModelAssembler();

    @BeforeEach
    void setUp() {
        // --- SIMULACIÓN DE CONTEXTO HTTP ---
        // Vital para que WebMvcLinkBuilder sepa construir la URL (http://localhost)
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("toModel: Convierte Rol a EntityModel y agrega link self")
    void toModel_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ROLE_ADMIN");

        // WHEN
        EntityModel<Rol> model = assembler.toModel(rol);

        // THEN
        // 1. Verificamos contenido
        assertNotNull(model);
        assertEquals(rol, model.getContent());
        assertEquals("ROLE_ADMIN", model.getContent().getNombre());

        // 2. Verificamos el Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(),
                "El modelo debe tener un link 'self'");

        // 3. Verificamos la URL del Link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // Si el link es un template (/api/roles/{id}), lo expandimos con el ID real
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link generado '" + href + "' debería contener el ID del rol (" + id + ")");
    }

    @Test
    @DisplayName("toModel: Falla si el Rol no tiene ID (IllegalArgumentException)")
    void toModel_SinId_Falla() {
        // GIVEN
        Rol rolSinId = new Rol();
        rolSinId.setNombre("ROLE_TEST");
        // ID es null

        // WHEN & THEN
        // createModelWithId lanza IllegalArgumentException si ID es null
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(rolSinId);
        });
    }

    @Test
    @DisplayName("toModel: Falla si el objeto Rol es nulo (NullPointerException)")
    void toModel_Nulo_Falla() {
        // GIVEN
        Rol rolNulo = null;

        // WHEN & THEN
        // El código intenta acceder a rol.getId() inmediatamente
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(rolNulo);
        });
    }
}