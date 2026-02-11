package br.com.fiap.postech.meu_postinho.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
    }

    @Test
    @DisplayName("Deve criar LoginDTO com construtor padrão")
    void shouldCreateLoginDTOWithDefaultConstructor() {
        LoginDTO newLoginDTO = new LoginDTO();
        
        assertNull(newLoginDTO.getEmail());
        assertNull(newLoginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve criar LoginDTO com construtor com parâmetros")
    void shouldCreateLoginDTOWithParameterizedConstructor() {
        LoginDTO newLoginDTO = new LoginDTO("test@example.com", "password123");
        
        assertEquals("test@example.com", newLoginDTO.getEmail());
        assertEquals("password123", newLoginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve definir e obter email")
    void shouldSetAndGetEmail() {
        String email = "test@example.com";
        loginDTO.setEmail(email);
        
        assertEquals(email, loginDTO.getEmail());
    }

    @Test
    @DisplayName("Deve definir e obter senha")
    void shouldSetAndGetSenha() {
        String senha = "password123";
        loginDTO.setSenha(senha);
        
        assertEquals(senha, loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve aceitar email nulo")
    void shouldAcceptNullEmail() {
        loginDTO.setEmail(null);
        
        assertNull(loginDTO.getEmail());
    }

    @Test
    @DisplayName("Deve aceitar senha nula")
    void shouldAcceptNullSenha() {
        loginDTO.setSenha(null);
        
        assertNull(loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve aceitar email vazio")
    void shouldAcceptEmptyEmail() {
        loginDTO.setEmail("");
        
        assertEquals("", loginDTO.getEmail());
    }

    @Test
    @DisplayName("Deve aceitar senha vazia")
    void shouldAcceptEmptySenha() {
        loginDTO.setSenha("");
        
        assertEquals("", loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve aceitar email com formato válido")
    void shouldAcceptValidEmailFormat() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "user123@test-domain.com",
            "test.email.with+symbol@example.com"
        };

        for (String email : validEmails) {
            loginDTO.setEmail(email);
            assertEquals(email, loginDTO.getEmail());
        }
    }

    @Test
    @DisplayName("Deve aceitar senha com tamanho mínimo")
    void shouldAcceptMinimumPasswordLength() {
        String senha = "12345678"; // 8 caracteres
        loginDTO.setSenha(senha);
        
        assertEquals(senha, loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve aceitar senha longa")
    void shouldAcceptLongPassword() {
        String senha = "this-is-a-very-long-password-with-many-characters-123456789";
        loginDTO.setSenha(senha);
        
        assertEquals(senha, loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve aceitar senha com caracteres especiais")
    void shouldAcceptPasswordWithSpecialCharacters() {
        String senha = "P@$$w0rd!123#";
        loginDTO.setSenha(senha);
        
        assertEquals(senha, loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve manter consistência após múltiplas atribuições")
    void shouldMaintainConsistencyAfterMultipleAssignments() {
        loginDTO.setEmail("first@example.com");
        loginDTO.setSenha("firstPassword");
        
        assertEquals("first@example.com", loginDTO.getEmail());
        assertEquals("firstPassword", loginDTO.getSenha());
        
        loginDTO.setEmail("second@example.com");
        loginDTO.setSenha("secondPassword");
        
        assertEquals("second@example.com", loginDTO.getEmail());
        assertEquals("secondPassword", loginDTO.getSenha());
    }

    @Test
    @DisplayName("Deve permitir uso como CPF no campo email")
    void shouldAllowCPFInEmailField() {
        String cpf = "12345678901";
        loginDTO.setEmail(cpf);
        
        assertEquals(cpf, loginDTO.getEmail());
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void shouldTestEqualsAndHashCode() {
        // Testa reflexividade
        assertEquals(loginDTO, loginDTO, "Objeto deve ser igual a si mesmo");
        
        // Testa nulo
        assertNotEquals(loginDTO, null, "Objeto não deve ser igual a null");
        
        // Testa classe diferente
        assertNotEquals(loginDTO, new Object(), "Objeto não deve ser igual a objeto de outra classe");
        
        // Testa consistência do hashCode
        int hashCode1 = loginDTO.hashCode();
        int hashCode2 = loginDTO.hashCode();
        assertEquals(hashCode1, hashCode2, "HashCode deve ser consistente");
        
        // Testa que objetos diferentes podem ter hashCodes diferentes
        LoginDTO differentDTO = new LoginDTO();
        differentDTO.setEmail("different@example.com");
        differentDTO.setSenha("different");
        assertNotEquals(loginDTO.hashCode(), differentDTO.hashCode(), "HashCodes de objetos diferentes podem ser diferentes");
    }

    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        loginDTO.setEmail("test@example.com");
        loginDTO.setSenha("password123");
        
        String toString = loginDTO.toString();
        
        assertNotNull(toString, "toString não deve ser nulo");
        // Lombok gera toString com todos os campos
        assertTrue(toString.length() > 0, "toString não deve ser vazio");
    }
}
