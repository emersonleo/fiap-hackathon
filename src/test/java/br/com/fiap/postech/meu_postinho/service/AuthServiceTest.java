package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.AuthResponse;
import br.com.fiap.postech.meu_postinho.dto.LoginDTO;
import br.com.fiap.postech.meu_postinho.dto.UsuarioDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.UsuarioRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import br.com.fiap.postech.meu_postinho.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UBSRepository ubsRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private UBS ubs;
    private LoginDTO loginDTO;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCpf("52998224725");
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setSenha("encodedPassword");
        usuario.setUbs(ubs);
        usuario.setRoles(new HashSet<>());
        usuario.getRoles().add("ROLE_MORADOR");

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setSenha("password123");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Test User");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setSenha("password123");
        usuarioDTO.setCpf("52998224725"); // CPF válido
        usuarioDTO.setTelefone("11999999999");
        usuarioDTO.setDataNascimento("01/01/1990");
        usuarioDTO.setEndereco("Rua Teste, 123");
        usuarioDTO.setCep("01234567");
        usuarioDTO.setUbsId(1L);
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void loginSuccess() {
        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));
        when(tokenProvider.generateToken(anyString(), anyString(), anyLong(), anyString(), anyString(), anyLong()))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(usuario.getId(), response.getId());
        assertEquals(usuario.getCpf(), response.getCpf());
        assertEquals(usuario.getNome(), response.getNome());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertEquals("ROLE_MORADOR", response.getRole());
        assertEquals(ubs.getId(), response.getUbsId());

        verify(authenticationManager).authenticate(any());
        verify(usuarioRepository).findByEmail(loginDTO.getEmail());
        verify(tokenProvider).generateToken(anyString(), anyString(), anyLong(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando credenciais forem inválidas")
    void loginWithInvalidCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadRequestException.class, () -> authService.login(loginDTO));

        verify(authenticationManager).authenticate(any());
        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não for encontrado")
    void loginWithNonExistentUser() {
        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(loginDTO));

        verify(authenticationManager).authenticate(any());
        verify(usuarioRepository).findByEmail(loginDTO.getEmail());
    }

    @Test
    @DisplayName("Deve registrar morador com sucesso")
    void registerMoradorSuccess() {
        when(usuarioRepository.existsByCpf(usuarioDTO.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(usuarioDTO.getTelefone())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(false);
        when(ubsRepository.findById(usuarioDTO.getUbsId())).thenReturn(Optional.of(ubs));
        when(passwordEncoder.encode(usuarioDTO.getSenha())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(tokenProvider.generateToken(anyString(), anyString(), anyLong(), anyString(), anyString(), anyLong()))
                .thenReturn("jwt-token");

        AuthResponse response = authService.registrarMorador(usuarioDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(usuario.getId(), response.getId());
        assertEquals(usuario.getCpf(), response.getCpf());
        assertEquals(usuario.getNome(), response.getNome());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertEquals("ROLE_MORADOR", response.getRole());
        assertEquals(ubs.getId(), response.getUbsId());

        verify(usuarioRepository).existsByCpf(usuarioDTO.getCpf());
        verify(usuarioRepository).existsByTelefone(usuarioDTO.getTelefone());
        verify(usuarioRepository).existsByEmail(usuarioDTO.getEmail());
        verify(ubsRepository).findById(usuarioDTO.getUbsId());
        verify(passwordEncoder).encode(usuarioDTO.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(tokenProvider).generateToken(anyString(), anyString(), anyLong(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando CPF for inválido")
    void registerWithInvalidCPF() {
        usuarioDTO.setCpf("123");

        assertThrows(BadRequestException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando CPF já existir")
    void registerWithExistingCPF() {
        when(usuarioRepository.existsByCpf(usuarioDTO.getCpf())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository).existsByCpf(usuarioDTO.getCpf());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando telefone for inválido")
    void registerWithInvalidPhone() {
        usuarioDTO.setTelefone("123");

        assertThrows(BadRequestException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando telefone já existir")
    void registerWithExistingPhone() {
        when(usuarioRepository.existsByCpf(usuarioDTO.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(usuarioDTO.getTelefone())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository).existsByCpf(usuarioDTO.getCpf());
        verify(usuarioRepository).existsByTelefone(usuarioDTO.getTelefone());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando email já existir")
    void registerWithExistingEmail() {
        when(usuarioRepository.existsByCpf(usuarioDTO.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(usuarioDTO.getTelefone())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository).existsByCpf(usuarioDTO.getCpf());
        verify(usuarioRepository).existsByTelefone(usuarioDTO.getTelefone());
        verify(usuarioRepository).existsByEmail(usuarioDTO.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando UBS não for encontrada")
    void registerWithNonExistentUBS() {
        when(usuarioRepository.existsByCpf(usuarioDTO.getCpf())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(usuarioDTO.getTelefone())).thenReturn(false);
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(false);
        when(ubsRepository.findById(usuarioDTO.getUbsId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.registrarMorador(usuarioDTO));

        verify(usuarioRepository).existsByCpf(usuarioDTO.getCpf());
        verify(usuarioRepository).existsByTelefone(usuarioDTO.getTelefone());
        verify(usuarioRepository).existsByEmail(usuarioDTO.getEmail());
        verify(ubsRepository).findById(usuarioDTO.getUbsId());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
