package com.maquinaria_agricola.gestion.controller.usuario;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maquinaria_agricola.gestion.DTO.usuario.LoginRequest;
import com.maquinaria_agricola.gestion.DTO.usuario.RegisterRequest;
import com.maquinaria_agricola.gestion.DTO.usuario.response.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.maquinaria_agricola.gestion.model.usuario.Usuario;
import com.maquinaria_agricola.gestion.repository.usuario.UsuarioRepository;
import com.maquinaria_agricola.gestion.utils.JwtUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

        Usuario usuario = usuarioRepository.findByUserName(loginRequest.getUserName()).orElse(null);

        String roleNombre = null;
        if (usuario != null && usuario.getRole() != null) {
            roleNombre = usuario.getRole().getRol().getNombre();
        }

        Cookie cookie = new Cookie("jwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hora
        response.addCookie(cookie);

        LoginResponse resp = new LoginResponse(
                jwt,
                usuario != null ? usuario.getId() : null,
                loginRequest.getUserName(),
                roleNombre);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByUserName(request.getUserName()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setApellido(request.getApellido());
        nuevo.setUserName(request.getUserName());
        nuevo.setEmail(request.getEmail());
        nuevo.setContrasena(passwordEncoder.encode(request.getContrasena()));

        usuarioRepository.save(nuevo);

        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no encontrado");
        }

        // Enviar correo o generar token de recuperación (por implementar)
        log.info("Simulando envío de correo de recuperación a {}", email);
        return ResponseEntity.ok("Correo de recuperación enviado");
    }
}
