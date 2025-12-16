package com.maquinaria_agricola.hateoas;

import com.maquinaria_agricola.model.usuario.Area;
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

class AreaModelAssemblerTest {

    // Instancia real, no necesitamos Mockito para la clase que testeamos
    private final AreaModelAssembler assembler = new AreaModelAssembler();

    @BeforeEach
    void setUp() {
        // --- CONFIGURACIÓN CRÍTICA ---
        // Simulamos un contexto HTTP. Sin esto, 'linkTo' y 'methodOn' fallan
        // porque no saben cómo construir la URL base (http://localhost).
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("toModel: Debería convertir Area a EntityModel con link Self")
    void toModel_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area area = new Area();
        area.setId(id);
        area.setNombre("Cultivo");

        // WHEN
        EntityModel<Area> model = assembler.toModel(area);

        // THEN
        // 1. Verificar contenido
        assertNotNull(model);
        assertEquals(area, model.getContent());
        assertEquals("Cultivo", model.getContent().getNombre());

        // 2. Verificar existencia del Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(),
                "El modelo debe tener un link 'self'");

        // 3. Verificar URL del Link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // MANEJO DE TEMPLATES:
        // A veces HATEOAS genera el link como /api/areas/{id} (template)
        // y otras como /api/areas/123-456 (expandido).
        // Esta lógica maneja ambos casos para que el test sea robusto.
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID del área (" + id + ")");
    }

    @Test
    @DisplayName("toModel: Debería fallar si el Área no tiene ID (IllegalArgumentException)")
    void toModel_SinId_Falla() {
        // GIVEN
        Area areaSinId = new Area();
        areaSinId.setNombre("Nueva Area");
        // ID es null

        // WHEN & THEN
        // El método padre 'createModelWithId' valida que el ID no sea nulo
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(areaSinId);
        });
    }

    @Test
    @DisplayName("toModel: Debería fallar si el objeto Área es nulo (NullPointerException)")
    void toModel_Nulo_Falla() {
        // GIVEN
        Area areaNula = null;

        // WHEN & THEN
        // Tu código hace area.getId() inmediatamente
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(areaNula);
        });
    }
}