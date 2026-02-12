package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Agendamento;
import br.com.fiap.postech.meu_postinho.domain.Agendamento.Status;
import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.domain.Vaga;
import br.com.fiap.postech.meu_postinho.dto.AgendamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Agendamento agendamento;
    private AgendamentoDTO agendamentoDTO;
    private Usuario usuario;
    private Usuario novoUsuario;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");

        novoUsuario = new Usuario();
        novoUsuario.setId(2L);
        novoUsuario.setNome("Novo Usuário");

        vaga = new Vaga();
        vaga.setId(1L);
        vaga.setDisponivel(true);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setUsuario(usuario);
        agendamento.setVaga(vaga);
        agendamento.setStatus(Status.CONFIRMADO);
        agendamento.setObservacoes("Teste de observações");

        agendamentoDTO = new AgendamentoDTO();
        agendamentoDTO.setId(1L);
        agendamentoDTO.setStatus("CONFIRMADO");
        agendamentoDTO.setObservacoes("Teste de observações");
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void criarAgendamentoSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.criar(1L, 1L);

        assertNotNull(result);
        assertEquals(agendamento.getId(), result.getId());
        assertEquals(Status.CONFIRMADO.name(), result.getStatus());
        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar agendamento para usuário inexistente")
    void criarAgendamentoUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.criar(1L, 1L));
        verify(usuarioRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar agendamento para vaga inexistente")
    void criarAgendamentoVagaInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.criar(1L, 1L));
        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar agendamento para usuário com agendamento ativo")
    void criarAgendamentoUsuarioComAgendamentoAtivo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(1L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU))))
                .thenReturn(Arrays.asList(new Agendamento()));

        assertThrows(BadRequestException.class, () -> agendamentoService.criar(1L, 1L));
        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(1L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)));
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar agendamento para vaga indisponível")
    void criarAgendamentoVagaIndisponivel() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        vaga.setDisponivel(false);
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));

        assertThrows(BadRequestException.class, () -> agendamentoService.criar(1L, 1L));
        verify(usuarioRepository).findById(1L);
        verify(vagaRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve obter agendamento por ID com sucesso")
    void obterAgendamentoPorIdSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        AgendamentoDTO result = agendamentoService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(agendamento.getId(), result.getId());
        assertEquals(agendamento.getStatus().name(), result.getStatus());
        verify(agendamentoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar agendamento inexistente")
    void obterAgendamentoInexistente() {
        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.obterPorId(999L));
        verify(agendamentoRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário")
    void listarAgendamentosPorUsuario() {
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        when(agendamentoRepository.findByPacienteId(1L)).thenReturn(agendamentos);

        List<AgendamentoDTO> result = agendamentoService.listarPorUsuario(1L);

        assertEquals(1, result.size());
        assertEquals(agendamento.getId(), result.get(0).getId());
        verify(agendamentoRepository).findByPacienteId(1L);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void cancelarAgendamentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.cancelar(1L, "Cancelamento de teste");

        assertNotNull(result);
        assertEquals(Status.CANCELADO.name(), result.getStatus());
        assertEquals("Cancelamento de teste", result.getMotivoCancelamento());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve confirmar agendamento quando ação for CONFIRMAR")
    void confirmarOuDesistirConfirmarSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        AgendamentoDTO result = agendamentoService.confirmarOuDesistir(1L, "CONFIRMAR");

        assertNotNull(result);
        assertEquals(Status.CONFIRMADO.name(), result.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve cancelar e liberar vaga quando ação de confirmação for DESISTIR")
    void confirmarOuDesistirDesistirSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendamentoDTO result = agendamentoService.confirmarOuDesistir(1L, "DESISTIR");

        assertNotNull(result);
        assertEquals(Status.CANCELADO.name(), result.getStatus());
        assertEquals("Desistência informada pelo paciente", result.getMotivoCancelamento());
        assertTrue(vaga.getDisponivel());
        verify(agendamentoRepository, times(2)).findById(1L);
        verify(vagaRepository).save(any(Vaga.class));
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao confirmar com ação inválida")
    void confirmarOuDesistirAcaoInvalida() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.confirmarOuDesistir(1L, "TALVEZ"));
        verify(agendamentoRepository).findById(1L);
        verify(vagaRepository, never()).save(any(Vaga.class));
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve remanejar agendamento para novo usuário com sucesso")
    void remanejarSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(novoUsuario));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU))))
                .thenReturn(List.of());
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendamentoDTO result = agendamentoService.remanejar(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getUsuarioId());
        assertEquals(Status.CONFIRMADO.name(), result.getStatus());
        assertFalse(vaga.getDisponivel());
        assertTrue(result.getObservacoes().contains("Remanejado do usuário 1 para 2"));
        verify(agendamentoRepository, times(3)).findById(1L);
        verify(usuarioRepository).findById(2L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)));
        verify(vagaRepository, times(2)).save(any(Vaga.class));
        verify(agendamentoRepository, times(2)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao remanejar para o mesmo usuário")
    void remanejarMesmoUsuario() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.remanejar(1L, 1L));
        verify(agendamentoRepository).findById(1L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao remanejar para usuário inexistente")
    void remanejarUsuarioInexistente() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.remanejar(1L, 2L));
        verify(agendamentoRepository).findById(1L);
        verify(usuarioRepository).findById(2L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao remanejar para usuário com agendamento ativo")
    void remanejarUsuarioComAgendamentoAtivo() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(novoUsuario));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU))))
                .thenReturn(List.of(new Agendamento()));

        assertThrows(BadRequestException.class, () -> agendamentoService.remanejar(1L, 2L));
        verify(agendamentoRepository).findById(1L);
        verify(usuarioRepository).findById(2L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)));
        verify(vagaRepository, never()).save(any(Vaga.class));
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve remanejar agendamento cancelado com sucesso")
    void remanejarAgendamentoCanceladoSuccess() {
        agendamento.setStatus(Status.CANCELADO);
        agendamento.setMotivoCancelamento("Desistência informada pelo paciente");
        vaga.setDisponivel(true);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(novoUsuario));
        when(agendamentoRepository.findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU))))
                .thenReturn(List.of());
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendamentoDTO result = agendamentoService.remanejar(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getUsuarioId());
        assertEquals(Status.CONFIRMADO.name(), result.getStatus());
        assertFalse(vaga.getDisponivel());
        assertNull(result.getMotivoCancelamento());
        assertNull(result.getDataCancelamento());
        verify(agendamentoRepository).findById(1L);
        verify(usuarioRepository).findById(2L);
        verify(agendamentoRepository).findByPacienteIdAndStatusIn(eq(2L), eq(Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)));
        verify(vagaRepository).save(any(Vaga.class));
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao remanejar agendamento com status não permitido")
    void remanejarStatusNaoPermitido() {
        agendamento.setStatus(Status.COMPARECEU);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThrows(BadRequestException.class, () -> agendamentoService.remanejar(1L, 2L));
        verify(agendamentoRepository).findById(1L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(vagaRepository, never()).save(any(Vaga.class));
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao cancelar agendamento inexistente")
    void cancelarAgendamentoInexistente() {
        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.cancelar(999L, "Teste"));
        verify(agendamentoRepository).findById(999L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao cancelar agendamento já cancelado")
    void cancelarAgendamentoJaCancelado() {
        Agendamento agendamentoCancelado = new Agendamento();
        agendamentoCancelado.setStatus(Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoCancelado));

        assertThrows(BadRequestException.class, () -> agendamentoService.cancelar(1L, "Teste"));
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve marcar comparecimento com sucesso")
    void marcarComparecimentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.marcarComparecimento(1L);

        assertNotNull(result);
        assertEquals(Status.COMPARECEU.name(), result.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao marcar comparecimento de agendamento inexistente")
    void marcarComparecimentoInexistente() {
        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.marcarComparecimento(999L));
        verify(agendamentoRepository).findById(999L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao marcar comparecimento de agendamento não confirmado")
    void marcarComparecimentoNaoConfirmado() {
        Agendamento agendamentoNaoConfirmado = new Agendamento();
        agendamentoNaoConfirmado.setStatus(Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoNaoConfirmado));

        assertThrows(BadRequestException.class, () -> agendamentoService.marcarComparecimento(1L));
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve marcar não comparecimento com sucesso")
    void marcarNaoComparecimentoSuccess() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoDTO result = agendamentoService.marcarNaoComparecimento(1L);

        assertNotNull(result);
        assertEquals(Status.NAO_COMPARECEU.name(), result.getStatus());
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao marcar não comparecimento de agendamento inexistente")
    void marcarNaoComparecimentoInexistente() {
        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendamentoService.marcarNaoComparecimento(999L));
        verify(agendamentoRepository).findById(999L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao marcar não comparecimento de agendamento não confirmado")
    void marcarNaoComparecimentoNaoConfirmado() {
        Agendamento agendamentoNaoConfirmado = new Agendamento();
        agendamentoNaoConfirmado.setStatus(Status.CANCELADO);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoNaoConfirmado));

        assertThrows(BadRequestException.class, () -> agendamentoService.marcarNaoComparecimento(1L));
        verify(agendamentoRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve testar datas de criação e atualização")
    void testDatasCriacaoEAtualizacao() {
        assertNull(agendamento.getDataCriacao());
        assertNull(agendamento.getDataAtualizacao());
        assertNull(agendamento.getDataCancelamento());
        assertNull(agendamento.getMotivoCancelamento());
    }
}
