package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.EstoqueMedicamento;
import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento;
import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento.Status;
import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.SolicitacaoMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.EstoqueMedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.MedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.SolicitacaoMedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import br.com.fiap.postech.meu_postinho.repository.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoMedicamentoServiceTest {

    @Mock
    private SolicitacaoMedicamentoRepository solicitacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private UBSRepository ubsRepository;

    @Mock
    private EstoqueMedicamentoRepository estoqueRepository;

    @InjectMocks
    private SolicitacaoMedicamentoService solicitacaoService;

    private SolicitacaoMedicamento solicitacao;
    private SolicitacaoMedicamentoDTO solicitacaoDTO;
    private Usuario usuario;
    private Medicamento medicamento;
    private UBS ubs;
    private EstoqueMedicamento estoque;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");

        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setNome("Paracetamol");

        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        solicitacao = new SolicitacaoMedicamento();
        solicitacao.setId(1L);
        solicitacao.setUsuario(usuario);
        solicitacao.setMedicamento(medicamento);
        solicitacao.setUbs(ubs);
        solicitacao.setQuantidade(10);
        solicitacao.setStatus(Status.PENDENTE);

        solicitacaoDTO = new SolicitacaoMedicamentoDTO();
        solicitacaoDTO.setId(1L);
        solicitacaoDTO.setUsuarioId(1L);
        solicitacaoDTO.setMedicamentoId(1L);
        solicitacaoDTO.setUbsId(1L);
        solicitacaoDTO.setQuantidade(10);
        solicitacaoDTO.setStatus(Status.PENDENTE.name());

        estoque = new EstoqueMedicamento();
        estoque.setId(1L);
        estoque.setMedicamento(medicamento);
        estoque.setUbs(ubs);
        estoque.setQuantidade(50);
        estoque.setQuantidadeMinima(10);
    }

    @Test
    @DisplayName("Deve criar solicitação com sucesso")
    void criarSolicitacaoSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(solicitacaoRepository.save(any(SolicitacaoMedicamento.class))).thenReturn(solicitacao);

        SolicitacaoMedicamentoDTO result = solicitacaoService.criar(solicitacaoDTO);

        assertNotNull(result);
        assertEquals(solicitacao.getId(), result.getId());
        verify(usuarioRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(ubsRepository).findById(1L);
        verify(solicitacaoRepository).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar solicitação com quantidade zero")
    void criarSolicitacaoQuantidadeZero() {
        solicitacaoDTO.setQuantidade(0);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));

        assertThrows(BadRequestException.class, () -> solicitacaoService.criar(solicitacaoDTO));
        verify(usuarioRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(ubsRepository).findById(1L);
        verify(solicitacaoRepository, never()).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar solicitação com usuário inexistente")
    void criarSolicitacaoUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitacaoService.criar(solicitacaoDTO));
        verify(usuarioRepository).findById(1L);
        verify(medicamentoRepository, never()).findById(1L);
        verify(ubsRepository, never()).findById(1L);
        verify(solicitacaoRepository, never()).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve obter solicitação por ID com sucesso")
    void obterPorIdSuccess() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        SolicitacaoMedicamentoDTO result = solicitacaoService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(solicitacao.getId(), result.getId());
        verify(solicitacaoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar solicitação inexistente")
    void obterPorIdInexistente() {
        when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitacaoService.obterPorId(999L));
        verify(solicitacaoRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve listar solicitações por usuário com sucesso")
    void listarPorUsuarioSuccess() {
        List<SolicitacaoMedicamento> solicitacoes = Arrays.asList(solicitacao);
        when(solicitacaoRepository.findByUsuarioId(1L)).thenReturn(solicitacoes);

        List<SolicitacaoMedicamentoDTO> result = solicitacaoService.listarPorUsuario(1L);

        assertEquals(1, result.size());
        assertEquals(solicitacao.getId(), result.get(0).getId());
        verify(solicitacaoRepository).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Deve listar solicitações pendentes por UBS com sucesso")
    void listarPendentesSuccess() {
        List<SolicitacaoMedicamento> solicitacoes = Arrays.asList(solicitacao);
        when(solicitacaoRepository.findByUbsIdAndStatus(1L, Status.PENDENTE)).thenReturn(solicitacoes);

        List<SolicitacaoMedicamentoDTO> result = solicitacaoService.listarPendentes(1L);

        assertEquals(1, result.size());
        assertEquals(solicitacao.getId(), result.get(0).getId());
        verify(solicitacaoRepository).findByUbsIdAndStatus(1L, Status.PENDENTE);
    }

    @Test
    @DisplayName("Deve aceitar solicitação com sucesso")
    void aceitarSolicitacaoSuccess() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(estoqueRepository.findByUbsIdAndMedicamentoId(1L, 1L)).thenReturn(Optional.of(estoque));
        when(solicitacaoRepository.save(any(SolicitacaoMedicamento.class))).thenReturn(solicitacao);
        when(estoqueRepository.save(any(EstoqueMedicamento.class))).thenReturn(estoque);

        SolicitacaoMedicamentoDTO result = solicitacaoService.aceitar(1L);

        assertNotNull(result);
        assertEquals(solicitacao.getId(), result.getId());
        assertEquals(Status.ACEITA.name(), result.getStatus());
        verify(solicitacaoRepository).findById(1L);
        verify(estoqueRepository).findByUbsIdAndMedicamentoId(1L, 1L);
        verify(estoqueRepository).save(any(EstoqueMedicamento.class));
        verify(solicitacaoRepository).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao aceitar solicitação não pendente")
    void aceitarSolicitacaoNaoPendente() {
        solicitacao.setStatus(Status.ACEITA);
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        assertThrows(BadRequestException.class, () -> solicitacaoService.aceitar(1L));
        verify(solicitacaoRepository).findById(1L);
        verify(estoqueRepository, never()).findByUbsIdAndMedicamentoId(anyLong(), anyLong());
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
        verify(solicitacaoRepository, never()).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao aceitar solicitação com estoque insuficiente")
    void aceitarSolicitacaoEstoqueInsuficiente() {
        estoque.setQuantidade(5); // Menor que a solicitação (10)
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(estoqueRepository.findByUbsIdAndMedicamentoId(1L, 1L)).thenReturn(Optional.of(estoque));

        assertThrows(BadRequestException.class, () -> solicitacaoService.aceitar(1L));
        verify(solicitacaoRepository).findById(1L);
        verify(estoqueRepository).findByUbsIdAndMedicamentoId(1L, 1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
        verify(solicitacaoRepository, never()).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve recusar solicitação com sucesso")
    void recusarSolicitacaoSuccess() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(SolicitacaoMedicamento.class))).thenReturn(solicitacao);

        SolicitacaoMedicamentoDTO result = solicitacaoService.recusar(1L, "Justificativa de teste");

        assertNotNull(result);
        assertEquals(solicitacao.getId(), result.getId());
        assertEquals(Status.RECUSADA.name(), result.getStatus());
        assertEquals("Justificativa de teste", result.getJustificativaRecusa());
        verify(solicitacaoRepository).findById(1L);
        verify(solicitacaoRepository).save(any(SolicitacaoMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao recusar solicitação não pendente")
    void recusarSolicitacaoNaoPendente() {
        solicitacao.setStatus(Status.RECUSADA);
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        assertThrows(BadRequestException.class, () -> solicitacaoService.recusar(1L, "Justificativa"));
        verify(solicitacaoRepository).findById(1L);
        verify(solicitacaoRepository, never()).save(any(SolicitacaoMedicamento.class));
    }
}
