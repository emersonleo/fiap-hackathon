package br.com.fiap.postech.meu_postinho.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User userDetails;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "ChaveSecretaMuitoComplexaParaOTolkenJWTDoProjeto1234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000);

        userDetails = new User("test@example.com", "password", 
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve gerar token JWT com sucesso")
    void generateTokenSuccess() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Deve gerar token JWT com parâmetros")
    void generateTokenWithParamsSuccess() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ROLE_USER", 1L, "12345678901", "Test User", 1L);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Deve extrair username do token com sucesso")
    void getUsernameFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("test@example.com", username);
    }

    @Test
    @DisplayName("Deve extrair ID do usuário do token com sucesso")
    void getUserIdFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ROLE_USER", 1L, "12345678901", "Test User", 1L);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Deve extrair role do token com sucesso")
    void getRoleFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ROLE_USER", 1L, "12345678901", "Test User", 1L);
        String role = jwtTokenProvider.getRoleFromToken(token);

        assertEquals("ROLE_USER", role);
    }

    @Test
    @DisplayName("Deve extrair ID da UBS do token com sucesso")
    void getUbsIdFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ROLE_USER", 1L, "12345678901", "Test User", 1L);
        Long ubsId = jwtTokenProvider.getUbsIdFromToken(token);

        assertEquals(1L, ubsId);
    }

    @Test
    @DisplayName("Deve extrair data de expiração do token com sucesso")
    void getExpirationDateFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken(userDetails);
        Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Deve validar token com sucesso")
    void validateTokenSuccess() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Deve invalidar token nulo")
    void validateTokenNull() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Deve invalidar token vazio")
    void validateTokenEmpty() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    @DisplayName("Deve invalidar token malformado")
    void validateTokenMalformed() {
        assertFalse(jwtTokenProvider.validateToken("token.invalido"));
    }

    @Test
    @DisplayName("Deve extrair claim do token com sucesso")
    void getClaimFromTokenSuccess() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ROLE_USER", 1L, "12345678901", "Test User", 1L);
        String role = jwtTokenProvider.getClaimFromToken(token, claims -> (String) claims.get("role"));

        assertEquals("ROLE_USER", role);
    }
}
