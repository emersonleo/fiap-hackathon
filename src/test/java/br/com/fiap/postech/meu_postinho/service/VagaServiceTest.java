package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Vaga;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.VagaDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.VagaRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    @Mock
    private VagaRepository vagaRepository;
    
    @Mock
    private UBSRepository ubsRepository;

    @InjectMocks
    private VagaService vagaService;

    private Vaga vaga;
    private VagaDTO vagaDTO;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");
        
        vaga = new Vaga();
        vaga.setId(1L);
        vaga.setUbs(ubs);
        vaga.setData(LocalDate.now().plusDays(1));
        vaga.setHoraInicio(LocalTime.of(8, 0));
        vaga.setHoraFim(LocalTime.of(12, 0));
        vaga.setEspecialidade("Clínico Geral");
        vaga.setProfissional("Dr. Teste");
        vaga.setDisponivel(true);

        vagaDTO = new VagaDTO();
        vagaDTO.setId(1L);
        vagaDTO.setUbsId(1L);
        vagaDTO.setData(LocalDate.now().plusDays(1));
        vagaDTO.setHoraInicio(LocalTime.of(8, 0));
        vagaDTO.setHoraFim(LocalTime.of(12, 0));
        vagaDTO.setEspecialidade("Clínico Geral");
        vagaDTO.setProfissional("Dr. Teste");
        vagaDTO.setDisponivel(true);
    }

    @Test
    @DisplayName("Deve criar vaga com sucesso")
    void criarVagaSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(vagaRepository.save(any(Vaga.class))).thenReturn(vaga);

        VagaDTO result = vagaService.criar(vagaDTO);

        assertNotNull(result);
        assertEquals(vaga.getId(), result.getId());
        assertEquals(vaga.getEspecialidade(), result.getEspecialidade());
        verify(ubsRepository).findById(1L);
        verify(vagaRepository).save(any(Vaga.class));
    }

    @Test
    @DisplayName("Deve obter vaga por ID com sucesso")
    void obterVagaPorIdSuccess() {
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));

        VagaDTO result = vagaService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(vaga.getId(), result.getId());
        assertEquals(vaga.getEspecialidade(), result.getEspecialidade());
        verify(vagaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar vaga inexistente")
    void obterVagaInexistente() {
        when(vagaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vagaService.obterPorId(999L));
        verify(vagaRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve atualizar vaga com sucesso")
    void atualizarVagaSuccess() {
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(vagaRepository.save(any(Vaga.class))).thenReturn(vaga);

        VagaDTO result = vagaService.atualizar(1L, vagaDTO);

        assertNotNull(result);
        assertEquals(vaga.getId(), result.getId());
        assertEquals(vaga.getEspecialidade(), result.getEspecialidade());
        verify(vagaRepository).findById(1L);
        verify(vagaRepository).save(any(Vaga.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar vaga inexistente")
    void atualizarVagaInexistente() {
        when(vagaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vagaService.atualizar(999L, vagaDTO));
        verify(vagaRepository).findById(999L);
        verify(vagaRepository, never()).save(any(Vaga.class));
    }

    @Test
    @DisplayName("Deve deletar vaga com sucesso")
    void deletarVagaSuccess() {
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));

        vagaService.deletar(1L);

        verify(vagaRepository).findById(1L);
        verify(vagaRepository).delete(vaga);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar vaga inexistente")
    void deletarVagaInexistente() {
        when(vagaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vagaService.deletar(999L));
        verify(vagaRepository).findById(999L);
        verify(vagaRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("Deve testar conversão para DTO")
    void testConverterParaDTO() {
        VagaDTO result = vagaService.converterParaDTO(vaga);

        assertNotNull(result);
        assertEquals(vaga.getId(), result.getId());
        assertEquals(vaga.getEspecialidade(), result.getEspecialidade());
        assertEquals(vaga.getProfissional(), result.getProfissional());
        assertEquals(vaga.getDisponivel(), result.getDisponivel());
    }

    @Test
    @DisplayName("Deve testar datas e horários")
    void testDatasEHorarios() {
        assertEquals(LocalDate.now().plusDays(1), vaga.getData());
        assertEquals(LocalTime.of(8, 0), vaga.getHoraInicio());
        assertEquals(LocalTime.of(12, 0), vaga.getHoraFim());
        assertTrue(vaga.getDisponivel());
    }
}
