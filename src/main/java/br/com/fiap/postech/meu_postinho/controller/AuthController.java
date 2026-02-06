package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.LoginDTO;
import br.com.fiap.postech.meu_postinho.dto.AuthResponse;
import br.com.fiap.postech.meu_postinho.dto.UsuarioDTO;
import br.com.fiap.postech.meu_postinho.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação e registro")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica um usuário e retorna token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponse response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "Registrar novo morador", description = "Cria um novo usuário morador")
    public ResponseEntity<AuthResponse> registrarMorador(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        AuthResponse response = authService.registrarMorador(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
