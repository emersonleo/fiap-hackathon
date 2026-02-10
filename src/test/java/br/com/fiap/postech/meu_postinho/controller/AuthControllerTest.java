package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.AuthResponse;
import br.com.fiap.postech.meu_postinho.dto.LoginDTO;
import br.com.fiap.postech.meu_postinho.dto.UsuarioDTO;
import br.com.fiap.postech.meu_postinho.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginDTO loginDTO;
    private UsuarioDTO usuarioDTO;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setSenha("password123");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Test User");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setSenha("password123");
        usuarioDTO.setCpf("52998224725");
        usuarioDTO.setTelefone("11999999999");
        usuarioDTO.setDataNascimento("01/01/1990");
        usuarioDTO.setEndereco("Rua Teste, 123");
        usuarioDTO.setCep("01234567");
        usuarioDTO.setUbsId(1L);

        authResponse = new AuthResponse(
            "jwt-token",
            1L,
            "52998224725",
            "Test User",
            "test@example.com",
            "ROLE_MORADOR",
            1L
        );
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void loginSuccess() throws Exception {
        when(authService.login(any(LoginDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.nome").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_MORADOR"));
    }

    @Test
    @DisplayName("Deve registrar novo morador com sucesso")
    void registerSuccess() throws Exception {
        when(authService.registrarMorador(any(UsuarioDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.nome").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_MORADOR"));
    }

    @Test
    @DisplayName("Deve retornar erro quando loginDTO for inválido")
    void loginWithInvalidData() throws Exception {
        LoginDTO invalidLoginDTO = new LoginDTO();
        invalidLoginDTO.setEmail("");
        invalidLoginDTO.setSenha("123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro quando usuarioDTO for inválido")
    void registerWithInvalidData() throws Exception {
        UsuarioDTO invalidUsuarioDTO = new UsuarioDTO();
        invalidUsuarioDTO.setNome("");
        invalidUsuarioDTO.setEmail("invalid-email");
        invalidUsuarioDTO.setSenha("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsuarioDTO)))
                .andExpect(status().isBadRequest());
    }
}
