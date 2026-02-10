package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Agendamento;
import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.domain.Vaga;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.AgendamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.AgendamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.UsuarioRepository;
import br.com.fiap.postech.meu_postinho.repository.VagaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VagaRepository vagaRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario;
    private Vaga vaga;
    private Agendamento agendamento;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");
        usuario.setUbs(ubs);

        vaga = new Vaga();
        vaga.setId(1L);
        vaga.setDisponivel(true);
        vaga.setUbs(ubs);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setUsuario(usuario);
        agendamento.setVaga(vaga);
        agendamento.setStatus(Agendamento.Status.CONFIRMADO);
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setDataAtualizacao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void criarAgendamentoSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(1L), anyList()))
                .thenReturn(Arrays.asList());
        when(vagaRepository.save(vaga)).thenReturn(vaga);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.criar(1L, 1L);

        assertNotNull(result);
        assertEquals(agendamento.getId(), result.getId());
        assertEquals(usuario.getId(), result.getUsuarioId());
        assertEquals(vaga.getId(), result.getVagaId());
        assertEquals(Agendamento.Status.CONFIRMADO.name(), result.getStatus());

        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(1L), anyList());
        verify(vagaRepository).save(vaga);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não for encontrado")
    void criarAgendamentoUsuarioNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.criar(1L, 1L));

        verify(usuarioRepository).findById(1L);
        verify(vagaRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando vaga não for encontrada")
    void criarAgendamentoVagaNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.criar(1L, 1L));

        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando usuário já tiver agendamento ativo")
    void criarAgendamentoUsuarioComAgendamentoAtivo() {
        Agendamento agendamentoAtivo = new Agendamento();
        agendamentoAtivo.setStatus(Agendamento.Status.CONFIRMADO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(1L), anyList()))
                .thenReturn(Arrays.asList(agendamentoAtivo));

        assertThrows(BadRequestException.class, () -> agendamentoService.criar(1L, 1L));

        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(1L), anyList());
        verify(vagaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando vaga não estiver disponível")
    void criarAgendamentoVagaIndisponivel() {
        vaga.setDisponivel(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(1L), anyList()))
                .thenReturn(Arrays.asList());

        assertThrows(BadRequestException.class, () -> agendamentoService.criar(1L, 1L));

        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(1L), anyList());
        verify(vagaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve obter agendamento por ID com sucesso")
    void obterAgendamentoPorIdSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        AgendamentoDTO result = agendamentoService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(agendamento.getId(), result.getId());
        assertEquals(usuario.getId(), result.getUsuarioId());
        assertEquals(vaga.getId(), result.getVagaId());

        verify(agendamentoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando agendamento não for encontrado")
    void obterAgendamentoPorIdNotFound() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.obterPorId(1L));

        verify(agendamentoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário")
    void listarAgendamentosPorUsuario() {
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        when(agendamentoRepository.findByPacienteId(1L)).thenReturn(agendamentos);

        List<AgendamentoDTO> result = agendamentoService.listarPorUsuario(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(agendamento.getId(), result.get(0).getId());

        verify(agendamentoRepository).findByPacienteId(1L);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void cancelarAgendamentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(vagaRepository.save(vaga)).thenReturn(vaga);
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.cancelar(1L, "Motivo de cancelamento");

        assertNotNull(result);
        assertEquals(Agendamento.Status.CANCELADO.name(), result.getStatus());
        assertEquals("Motivo de cancelamento", result.getMotivoCancelamento());
        assertNotNull(result.getDataCancelamento());

        verify(agendamentoRepository).findById(1L);
        verify(vagaRepository).save(vaga);
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao tentar cancelar agendamento já cancelado")
    void cancelarAgendamentoJaCancelado() {
        agendamento.setStatus(Agendamento.Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.cancelar(1L, "Motivo"));

        verify(agendamentoRepository).findById(1L);
        verify(vagaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve marcar comparecimento com sucesso")
    void marcarComparecimentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.marcarComparecimento(1L);

        assertNotNull(result);
        assertEquals(Agendamento.Status.COMPARECEU.name(), result.getStatus());

        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao marcar comparecimento em agendamento não confirmado")
    void marcarComparecimentoAgendamentoNaoConfirmado() {
        agendamento.setStatus(Agendamento.Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.marcarComparecimento(1L));

        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve marcar não comparecimento com sucesso")
    void marcarNaoComparecimentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(agendamento)).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.marcarNaoComparecimento(1L);

        assertNotNull(result);
        assertEquals(Agendamento.Status.NAO_COMPARECEU.name(), result.getStatus());

        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao marcar não comparecimento em agendamento não confirmado")
    void marcarNaoComparecimentoAgendamentoNaoConfirmado() {
        agendamento.setStatus(Agendamento.Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.marcarNaoComparecimento(1L));

        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any());
    }
}
