package com.maquinaria_agricola.gestion.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maquinaria_agricola.gestion.DTO.usuario.UsuarioDTO;
import com.maquinaria_agricola.gestion.hateoas.UsuarioModelAssembler;
import com.maquinaria_agricola.gestion.mapper.UsuarioMapper;
import com.maquinaria_agricola.gestion.model.usuario.Usuario;
import com.maquinaria_agricola.gestion.service.usuario.CustomUserDetailsService;
import com.maquinaria_agricola.gestion.service.usuario.UsuarioService;
import com.maquinaria_agricola.gestion.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService; // Mockeado aquí por ser dependencia del controller
    @MockBean
    private UsuarioMapper usuarioMapper;
    @MockBean
    private UsuarioModelAssembler usuarioModelAssembler;

    // Mocks de Seguridad (aunque UsuarioService ya está arriba, los otros son
    // necesarios)
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUsuario_ok() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUserName("nuevoUser");
        dto.setContrasena("1234");
        dto.setEmail("nuevo@test.com"); // Requerido por @Valid si hay anotaciones, o por lógica

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setUserName("nuevoUser");

        when(usuarioMapper.toEntity(any(UsuarioDTO.class))).thenReturn(usuario);
        when(usuarioService.createUsuario(any(Usuario.class))).thenReturn(usuario);
        when(usuarioModelAssembler.toModel(any(Usuario.class))).thenReturn(EntityModel.of(usuario));

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}