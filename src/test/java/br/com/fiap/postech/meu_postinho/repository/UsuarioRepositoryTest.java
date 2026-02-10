package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UBS ubs;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCpf("12345678901");
        usuario.setNome("Test User");
        usuario.setTelefone("11999999999");
        usuario.setEmail("test@example.com");
        usuario.setSenha("encodedPassword");
        usuario.setDataNascimento("01/01/1990");
        usuario.setEndereco("Rua Teste, 123");
        usuario.setCep("01234567");
        usuario.setUbs(ubs);
        usuario.setAtivo(true);
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void shouldFindUserByEmail() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> found = usuarioRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals(usuario.getId(), found.get().getId());
        assertEquals(usuario.getEmail(), found.get().getEmail());
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Deve não encontrar usuário por email inexistente")
    void shouldNotFindUserByNonExistentEmail() {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<Usuario> found = usuarioRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
        verify(usuarioRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Deve encontrar usuário por CPF")
    void shouldFindUserByCpf() {
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        Optional<Usuario> found = usuarioRepository.findByCpf("12345678901");

        assertTrue(found.isPresent());
        assertEquals(usuario.getId(), found.get().getId());
        assertEquals(usuario.getCpf(), found.get().getCpf());
        verify(usuarioRepository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve não encontrar usuário por CPF inexistente")
    void shouldNotFindUserByNonExistentCpf() {
        when(usuarioRepository.findByCpf("98765432100")).thenReturn(Optional.empty());

        Optional<Usuario> found = usuarioRepository.findByCpf("98765432100");

        assertFalse(found.isPresent());
        verify(usuarioRepository).findByCpf("98765432100");
    }

    @Test
    @DisplayName("Deve encontrar usuário por telefone")
    void shouldFindUserByTelefone() {
        when(usuarioRepository.findByTelefone("11999999999")).thenReturn(Optional.of(usuario));

        Optional<Usuario> found = usuarioRepository.findByTelefone("11999999999");

        assertTrue(found.isPresent());
        assertEquals(usuario.getId(), found.get().getId());
        assertEquals(usuario.getTelefone(), found.get().getTelefone());
        verify(usuarioRepository).findByTelefone("11999999999");
    }

    @Test
    @DisplayName("Deve não encontrar usuário por telefone inexistente")
    void shouldNotFindUserByNonExistentTelefone() {
        when(usuarioRepository.findByTelefone("11888888888")).thenReturn(Optional.empty());

        Optional<Usuario> found = usuarioRepository.findByTelefone("11888888888");

        assertFalse(found.isPresent());
        verify(usuarioRepository).findByTelefone("11888888888");
    }

    @Test
    @DisplayName("Deve listar usuários por UBS ID")
    void shouldListUsersByUbsId() {
        List<Usuario> usuarios = List.of(usuario);
        when(usuarioRepository.findByUbsId(ubs.getId())).thenReturn(usuarios);

        List<Usuario> result = usuarioRepository.findByUbsId(ubs.getId());

        assertEquals(1, result.size());
        assertEquals(usuario.getId(), result.get(0).getId());
        assertEquals(ubs.getId(), result.get(0).getUbs().getId());
        verify(usuarioRepository).findByUbsId(ubs.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver usuários na UBS")
    void shouldReturnEmptyListWhenNoUsersInUbs() {
        when(usuarioRepository.findByUbsId(999L)).thenReturn(List.of());

        List<Usuario> usuarios = usuarioRepository.findByUbsId(999L);

        assertTrue(usuarios.isEmpty());
        verify(usuarioRepository).findByUbsId(999L);
    }

    @Test
    @DisplayName("Deve verificar se CPF existe")
    void shouldCheckIfCpfExists() {
        when(usuarioRepository.existsByCpf("12345678901")).thenReturn(true);
        when(usuarioRepository.existsByCpf("98765432100")).thenReturn(false);

        assertTrue(usuarioRepository.existsByCpf("12345678901"));
        assertFalse(usuarioRepository.existsByCpf("98765432100"));
        verify(usuarioRepository).existsByCpf("12345678901");
        verify(usuarioRepository).existsByCpf("98765432100");
    }

    @Test
    @DisplayName("Deve verificar se telefone existe")
    void shouldCheckIfTelefoneExists() {
        when(usuarioRepository.existsByTelefone("11999999999")).thenReturn(true);
        when(usuarioRepository.existsByTelefone("11888888888")).thenReturn(false);

        assertTrue(usuarioRepository.existsByTelefone("11999999999"));
        assertFalse(usuarioRepository.existsByTelefone("11888888888"));
        verify(usuarioRepository).existsByTelefone("11999999999");
        verify(usuarioRepository).existsByTelefone("11888888888");
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void shouldCheckIfEmailExists() {
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(usuarioRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertTrue(usuarioRepository.existsByEmail("test@example.com"));
        assertFalse(usuarioRepository.existsByEmail("nonexistent@example.com"));
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(usuarioRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Deve encontrar múltiplos usuários na mesma UBS")
    void shouldFindMultipleUsersInSameUbs() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setCpf("98765432100");
        usuario2.setNome("Test User 2");
        usuario2.setTelefone("11888888888");
        usuario2.setEmail("test2@example.com");
        usuario2.setSenha("encodedPassword2");
        usuario2.setDataNascimento("02/02/1990");
        usuario2.setEndereco("Rua Teste, 456");
        usuario2.setCep("01234568");
        usuario2.setUbs(ubs);
        usuario2.setAtivo(true);

        List<Usuario> usuarios = List.of(usuario, usuario2);
        when(usuarioRepository.findByUbsId(ubs.getId())).thenReturn(usuarios);

        List<Usuario> result = usuarioRepository.findByUbsId(ubs.getId());

        assertEquals(2, result.size());
        verify(usuarioRepository).findByUbsId(ubs.getId());
    }
}
