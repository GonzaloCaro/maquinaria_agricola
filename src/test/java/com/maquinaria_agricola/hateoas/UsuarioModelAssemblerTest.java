package com.maquinaria_agricola.hateoas;

import com.maquinaria_agricola.model.usuario.Usuario;
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

class UsuarioModelAssemblerTest {

    // Test unitario puro: instanciamos la clase directamente
    private final UsuarioModelAssembler assembler = new UsuarioModelAssembler();

    @BeforeEach
    void setUp() {
        // --- CONTEXTO HTTP SIMULADO ---
        // Necesario para que 'linkTo' y 'methodOn' funcionen y puedan construir la URL
        // base.
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("toModel: Crea EntityModel con el usuario y el link self")
    void toModel_Exito() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUserName("agricultor_juan");
        usuario.setEmail("juan@campo.cl");

        // WHEN
        EntityModel<Usuario> model = assembler.toModel(usuario);

        // THEN
        // 1. Validar Contenido
        assertNotNull(model);
        assertEquals(usuario, model.getContent());
        assertEquals("agricultor_juan", model.getContent().getUserName());

        // 2. Validar Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(),
                "El modelo debe contener un link 'self'");

        // 3. Validar URL
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // Manejo de Templates: HATEOAS puede generar '/usuarios/{id}' o
        // '/usuarios/123-...'
        // Expandimos el template si es necesario para validar el ID.
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID del usuario (" + id + ")");
    }

    @Test
    @DisplayName("toModel: Falla si el usuario no tiene ID (IllegalArgumentException)")
    void toModel_SinId_Falla() {
        // GIVEN
        Usuario usuarioSinId = new Usuario();
        usuarioSinId.setUserName("sin_id");
        // ID es null

        // WHEN & THEN
        // createModelWithId lanza IllegalArgumentException si el ID es nulo
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(usuarioSinId);
        });
    }

    @Test
    @DisplayName("toModel: Falla si el usuario es nulo (NullPointerException)")
    void toModel_Nulo_Falla() {
        // GIVEN
        Usuario usuarioNulo = null;

        // WHEN & THEN
        // Tu código llama a usuario.getId() inmediatamente
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(usuarioNulo);
        });
    }
}