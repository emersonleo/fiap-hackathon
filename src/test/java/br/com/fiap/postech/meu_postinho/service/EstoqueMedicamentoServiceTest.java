package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.EstoqueMedicamento;
import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.EstoqueMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.EstoqueMedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.MedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
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
class EstoqueMedicamentoServiceTest {

    @Mock
    private EstoqueMedicamentoRepository estoqueRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private UBSRepository ubsRepository;

    @InjectMocks
    private EstoqueMedicamentoService estoqueService;

    private EstoqueMedicamento estoque;
    private EstoqueMedicamentoDTO estoqueDTO;
    private Medicamento medicamento;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setNome("Paracetamol");

        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        estoque = new EstoqueMedicamento();
        estoque.setId(1L);
        estoque.setMedicamento(medicamento);
        estoque.setUbs(ubs);
        estoque.setQuantidade(100);
        estoque.setQuantidadeMinima(10);

        estoqueDTO = new EstoqueMedicamentoDTO();
        estoqueDTO.setId(1L);
        estoqueDTO.setMedicamentoId(1L);
        estoqueDTO.setUbsId(1L);
        estoqueDTO.setQuantidade(100);
        estoqueDTO.setQuantidadeMinima(10);
    }

    @Test
    @DisplayName("Deve listar estoque por UBS com sucesso")
    void listarPorUBSSuccess() {
        List<EstoqueMedicamento> estoques = Arrays.asList(estoque);
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(estoqueRepository.findByUbs(ubs)).thenReturn(estoques);

        List<EstoqueMedicamentoDTO> result = estoqueService.listarPorUBS(1L);

        assertEquals(1, result.size());
        assertEquals(estoque.getId(), result.get(0).getId());
        verify(ubsRepository).findById(1L);
        verify(estoqueRepository).findByUbs(ubs);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao listar estoque com UBS inexistente")
    void listarPorUBSInexistente() {
        when(ubsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.listarPorUBS(999L));
        verify(ubsRepository).findById(999L);
        verify(estoqueRepository, never()).findByUbs(any(UBS.class));
    }

    @Test
    @DisplayName("Deve obter estoque por ID com sucesso")
    void obterPorIdSuccess() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        EstoqueMedicamentoDTO result = estoqueService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(estoque.getId(), result.getId());
        verify(estoqueRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar estoque inexistente")
    void obterPorIdInexistente() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.obterPorId(999L));
        verify(estoqueRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve criar estoque com sucesso")
    void criarEstoqueSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(estoqueRepository.findByUbsIdAndMedicamentoId(1L, 1L)).thenReturn(Optional.empty());
        when(estoqueRepository.save(any(EstoqueMedicamento.class))).thenReturn(estoque);

        EstoqueMedicamentoDTO result = estoqueService.criar(estoqueDTO);

        assertNotNull(result);
        assertEquals(estoque.getId(), result.getId());
        verify(ubsRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(estoqueRepository).findByUbsIdAndMedicamentoId(1L, 1L);
        verify(estoqueRepository).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar estoque com UBS inexistente")
    void criarEstoqueUBSInexistente() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.criar(estoqueDTO));
        verify(ubsRepository).findById(1L);
        verify(medicamentoRepository, never()).findById(1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar estoque com medicamento inexistente")
    void criarEstoqueMedicamentoInexistente() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.criar(estoqueDTO));
        verify(ubsRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar estoque duplicado")
    void criarEstoqueDuplicado() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(estoqueRepository.findByUbsIdAndMedicamentoId(1L, 1L)).thenReturn(Optional.of(estoque));

        assertThrows(BadRequestException.class, () -> estoqueService.criar(estoqueDTO));
        verify(ubsRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(estoqueRepository).findByUbsIdAndMedicamentoId(1L, 1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar estoque com quantidade negativa")
    void criarEstoqueQuantidadeNegativa() {
        estoqueDTO.setQuantidade(-1);
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(estoqueRepository.findByUbsIdAndMedicamentoId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> estoqueService.criar(estoqueDTO));
        verify(ubsRepository).findById(1L);
        verify(medicamentoRepository).findById(1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve atualizar quantidade do estoque com sucesso")
    void atualizarQuantidadeSuccess() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(EstoqueMedicamento.class))).thenReturn(estoque);

        EstoqueMedicamentoDTO result = estoqueService.atualizarQuantidade(1L, 150);

        assertNotNull(result);
        assertEquals(estoque.getId(), result.getId());
        verify(estoqueRepository).findById(1L);
        verify(estoqueRepository).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar quantidade de estoque inexistente")
    void atualizarQuantidadeInexistente() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.atualizarQuantidade(999L, 150));
        verify(estoqueRepository).findById(999L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao atualizar quantidade com valor negativo")
    void atualizarQuantidadeNegativa() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        assertThrows(BadRequestException.class, () -> estoqueService.atualizarQuantidade(1L, -1));
        verify(estoqueRepository).findById(1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve atualizar estoque com sucesso")
    void atualizarEstoqueSuccess() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(EstoqueMedicamento.class))).thenReturn(estoque);

        EstoqueMedicamentoDTO result = estoqueService.atualizar(1L, estoqueDTO);

        assertNotNull(result);
        assertEquals(estoque.getId(), result.getId());
        verify(estoqueRepository).findById(1L);
        verify(estoqueRepository).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar estoque inexistente")
    void atualizarEstoqueInexistente() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.atualizar(999L, estoqueDTO));
        verify(estoqueRepository).findById(999L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao atualizar estoque com quantidade negativa")
    void atualizarEstoqueQuantidadeNegativa() {
        estoqueDTO.setQuantidade(-1);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        assertThrows(BadRequestException.class, () -> estoqueService.atualizar(1L, estoqueDTO));
        verify(estoqueRepository).findById(1L);
        verify(estoqueRepository, never()).save(any(EstoqueMedicamento.class));
    }

    @Test
    @DisplayName("Deve deletar estoque com sucesso")
    void deletarEstoqueSuccess() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        estoqueService.deletar(1L);

        verify(estoqueRepository).findById(1L);
        verify(estoqueRepository).delete(estoque);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar estoque inexistente")
    void deletarEstoqueInexistente() {
        when(estoqueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estoqueService.deletar(999L));
        verify(estoqueRepository).findById(999L);
        verify(estoqueRepository, never()).delete(any(EstoqueMedicamento.class));
    }
}
