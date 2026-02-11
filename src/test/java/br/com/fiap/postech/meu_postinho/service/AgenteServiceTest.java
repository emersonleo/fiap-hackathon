package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Agente;
import br.com.fiap.postech.meu_postinho.domain.Agente.StatusCNES;
import br.com.fiap.postech.meu_postinho.domain.Agente.TipoAgente;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.AgenteDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService;
import br.com.fiap.postech.meu_postinho.repository.AgenteRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenteServiceTest {

    @Mock
    private AgenteRepository agenteRepository;

    @Mock
    private UBSRepository ubsRepository;

    @Mock
    private CnesMockService cnesMockService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AgenteService agenteService;

    private Agente agente;
    private AgenteDTO agenteDTO;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        agente = new Agente();
        agente.setId(1L);
        agente.setNome("Agente Teste");
        agente.setCpf("12345678909");
        agente.setTelefone("11999999999");
        agente.setEmail("agente@teste.com");
        agente.setSenha("senha123");
        agente.setTipo(TipoAgente.ACS);
        agente.setStatusCnes(StatusCNES.VERIFICADO);
        agente.setUbs(ubs);
        agente.setAtivo(true);

        agenteDTO = new AgenteDTO();
        agenteDTO.setId(1L);
        agenteDTO.setNome("Agente Teste");
        agenteDTO.setCpf("12345678909");
        agenteDTO.setTelefone("11999999999");
        agenteDTO.setEmail("agente@teste.com");
        agenteDTO.setSenha("senha123");
        agenteDTO.setTipo("ACS");
        agenteDTO.setStatusCnes("VERIFICADO");
        agenteDTO.setUbsId(1L);
        agenteDTO.setDataNascimento("1990-01-01");
        agenteDTO.setEndereco("Rua Teste, 123");
        agenteDTO.setCep("12345678");
        agenteDTO.setCns("123456789012345");
    }

    @Test
    @DisplayName("Deve registrar agente com sucesso")
    void registrarAgenteSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncoded");
        when(agenteRepository.existsByCns("123456789012345")).thenReturn(false);
        when(cnesMockService.validarProfissional(anyString(), anyString())).thenReturn(new CnesMockService.CneProfissionalResponse(true, "Sucesso"));
        when(agenteRepository.save(any(Agente.class))).thenReturn(agente);

        AgenteDTO result = agenteService.registrarAgente(agenteDTO);

        assertNotNull(result);
        assertEquals(agente.getId(), result.getId());
        assertEquals(agente.getNome(), result.getNome());
        verify(ubsRepository).findById(1L);
        verify(passwordEncoder).encode("senha123");
        verify(agenteRepository).existsByCns("123456789012345");
        verify(cnesMockService).validarProfissional("12345678909", "123456789012345");
        verify(agenteRepository).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao registrar agente com CPF inválido")
    void registrarAgenteCPFInvalido() {
        agenteDTO.setCpf("123");

        assertThrows(BadRequestException.class, () -> agenteService.registrarAgente(agenteDTO));
        verify(ubsRepository, never()).findById(1L);
        verify(agenteRepository, never()).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao registrar agente com CNS já cadastrado")
    void registrarAgenteCNSJaCadastrado() {
        when(agenteRepository.existsByCns("123456789012345")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> agenteService.registrarAgente(agenteDTO));
        verify(ubsRepository, never()).findById(1L);
        verify(agenteRepository).existsByCns("123456789012345");
        verify(agenteRepository, never()).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao registrar agente com telefone inválido")
    void registrarAgenteTelefoneInvalido() {
        agenteDTO.setTelefone("123");

        assertThrows(BadRequestException.class, () -> agenteService.registrarAgente(agenteDTO));
        verify(ubsRepository, never()).findById(1L);
        verify(agenteRepository, never()).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao registrar agente com UBS inexistente")
    void registrarAgenteUBSInexistente() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agenteService.registrarAgente(agenteDTO));
        verify(ubsRepository).findById(1L);
        verify(agenteRepository, never()).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao registrar agente com falha na validação CNES")
    void registrarAgenteFalhaCNES() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(agenteRepository.existsByCns("123456789012345")).thenReturn(false);
        when(cnesMockService.validarProfissional(anyString(), anyString())).thenReturn(new CnesMockService.CneProfissionalResponse(false, "Falha na validação"));

        assertThrows(BadRequestException.class, () -> agenteService.registrarAgente(agenteDTO));
        verify(ubsRepository).findById(1L);
        verify(cnesMockService).validarProfissional("12345678909", "123456789012345");
        verify(agenteRepository, never()).save(any(Agente.class));
    }

    @Test
    @DisplayName("Deve obter agente por ID com sucesso")
    void obterAgentePorIdSuccess() {
        when(agenteRepository.findById(1L)).thenReturn(Optional.of(agente));

        AgenteDTO result = agenteService.obterAgentePorId(1L);

        assertNotNull(result);
        assertEquals(agente.getId(), result.getId());
        verify(agenteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar agente inexistente")
    void obterAgenteInexistente() {
        when(agenteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agenteService.obterAgentePorId(999L));
        verify(agenteRepository).findById(999L);
    }
}
