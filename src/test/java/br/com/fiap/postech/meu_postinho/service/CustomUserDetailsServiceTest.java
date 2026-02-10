package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.config.CustomUserDetailsService;
import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setSenha("password");
        usuario.setAtivo(true);
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        usuario.setRoles(roles);
    }

    @Test
    @DisplayName("Deve carregar usuário por email com sucesso")
    void loadUserByUsernameSuccess() {
        String email = "test@example.com";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        
        UserDetails result = customUserDetailsService.loadUserByUsername(email);
        
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado")
    void loadUserByUsernameNotFound() {
        String email = "nonexistent@example.com";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
                () -> customUserDetailsService.loadUserByUsername(email));
        
        assertTrue(exception.getMessage().contains("Usuário não encontrado com email"));
    }

    @Test
    @DisplayName("Deve carregar usuário inativo como desabilitado")
    void loadInactiveUserAsDisabled() {
        String email = "inactive@example.com";
        Usuario inactiveUsuario = new Usuario();
        inactiveUsuario.setEmail(email);
        inactiveUsuario.setSenha("password");
        inactiveUsuario.setAtivo(false);
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        inactiveUsuario.setRoles(roles);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(inactiveUsuario));
        
        UserDetails result = customUserDetailsService.loadUserByUsername(email);
        
        assertNotNull(result);
        assertFalse(result.isEnabled());
    }

    @Test
    @DisplayName("Deve carregar usuário sem roles")
    void loadUserWithoutRoles() {
        String email = "noroles@example.com";
        Usuario usuarioSemRoles = new Usuario();
        usuarioSemRoles.setEmail(email);
        usuarioSemRoles.setSenha("password");
        usuarioSemRoles.setAtivo(true);
        usuarioSemRoles.setRoles(null);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioSemRoles));
        
        UserDetails result = customUserDetailsService.loadUserByUsername(email);
        
        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
    }
}
