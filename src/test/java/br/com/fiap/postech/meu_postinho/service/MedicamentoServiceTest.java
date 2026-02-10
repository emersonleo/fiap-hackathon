package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import br.com.fiap.postech.meu_postinho.dto.MedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.MedicamentoRepository;
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
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private MedicamentoService medicamentoService;

    private Medicamento medicamento;
    private MedicamentoDTO medicamentoDTO;

    @BeforeEach
    void setUp() {
        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setNome("Paracetamol");
        medicamento.setDescricao("Analgésico e antipirético");
        medicamento.setCategoria("Analgésico");
        medicamento.setPosologia("Comprimido");
        medicamento.setUnidade("500mg");
        medicamento.setAtivo(true);

        medicamentoDTO = new MedicamentoDTO();
        medicamentoDTO.setId(1L);
        medicamentoDTO.setNome("Paracetamol");
        medicamentoDTO.setDescricao("Analgésico e antipirético");
        medicamentoDTO.setCategoria("Analgésico");
        medicamentoDTO.setPosologia("Comprimido");
        medicamentoDTO.setUnidade("500mg");
        medicamentoDTO.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar medicamento com sucesso")
    void criarMedicamentoSuccess() {
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);

        MedicamentoDTO result = medicamentoService.criar(medicamentoDTO);

        assertNotNull(result);
        assertEquals(medicamento.getId(), result.getId());
        assertEquals(medicamento.getNome(), result.getNome());
        verify(medicamentoRepository).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar medicamento duplicado")
    void criarMedicamentoDuplicado() {
        when(medicamentoRepository.findByNome(medicamentoDTO.getNome())).thenReturn(Optional.of(medicamento));

        assertThrows(BadRequestException.class, () -> medicamentoService.criar(medicamentoDTO));
        verify(medicamentoRepository).findByNome(medicamentoDTO.getNome());
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve listar todos os medicamentos")
    void listarTodosMedicamentos() {
        List<Medicamento> medicamentos = Arrays.asList(medicamento);
        when(medicamentoRepository.findByAtivoTrue()).thenReturn(medicamentos);

        List<MedicamentoDTO> result = medicamentoService.listarTodos();

        assertEquals(1, result.size());
        assertEquals(medicamento.getNome(), result.get(0).getNome());
        verify(medicamentoRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve obter medicamento por ID com sucesso")
    void obterMedicamentoPorIdSuccess() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));

        MedicamentoDTO result = medicamentoService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(medicamento.getId(), result.getId());
        verify(medicamentoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar medicamento inexistente")
    void obterMedicamentoInexistente() {
        when(medicamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicamentoService.obterPorId(999L));
        verify(medicamentoRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve atualizar medicamento com sucesso")
    void atualizarMedicamentoSuccess() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);

        MedicamentoDTO result = medicamentoService.atualizar(1L, medicamentoDTO);

        assertNotNull(result);
        assertEquals(medicamento.getNome(), result.getNome());
        verify(medicamentoRepository).findById(1L);
        verify(medicamentoRepository).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar medicamento inexistente")
    void atualizarMedicamentoInexistente() {
        when(medicamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicamentoService.atualizar(999L, medicamentoDTO));
        verify(medicamentoRepository).findById(999L);
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve deletar medicamento com sucesso")
    void deletarMedicamentoSuccess() {
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);

        medicamentoService.deletar(1L);

        verify(medicamentoRepository).findById(1L);
        verify(medicamentoRepository).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar medicamento inexistente")
    void deletarMedicamentoInexistente() {
        when(medicamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicamentoService.deletar(999L));
        verify(medicamentoRepository).findById(999L);
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    @DisplayName("Deve buscar medicamentos por categoria")
    void buscarMedicamentosPorCategoria() {
        List<Medicamento> medicamentos = Arrays.asList(medicamento);
        when(medicamentoRepository.findByCategoria("Analgésico")).thenReturn(medicamentos);

        List<MedicamentoDTO> result = medicamentoService.listarPorCategoria("Analgésico");

        assertEquals(1, result.size());
        assertEquals(medicamento.getNome(), result.get(0).getNome());
        verify(medicamentoRepository).findByCategoria("Analgésico");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar categoria inexistente")
    void buscarMedicamentoPorCategoriaInexistente() {
        when(medicamentoRepository.findByCategoria("Inexistente")).thenReturn(Arrays.asList());

        List<MedicamentoDTO> result = medicamentoService.listarPorCategoria("Inexistente");

        assertTrue(result.isEmpty());
        verify(medicamentoRepository).findByCategoria("Inexistente");
    }

    @Test
    @DisplayName("Deve listar apenas medicamentos ativos")
    void listarMedicamentosAtivos() {
        List<Medicamento> medicamentos = Arrays.asList(medicamento);
        when(medicamentoRepository.findByAtivoTrue()).thenReturn(medicamentos);

        List<MedicamentoDTO> result = medicamentoService.listarTodos();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAtivo());
        verify(medicamentoRepository).findByAtivoTrue();
    }
}
