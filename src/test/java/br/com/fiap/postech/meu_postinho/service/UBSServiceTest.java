package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.UBSDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UBSServiceTest {

    @Mock
    private UBSRepository ubsRepository;

    @Mock
    private CnesMockService cnesMockService;

    @InjectMocks
    private UBSService ubsService;

    private UBS ubs;
    private UBSDTO ubsDTO;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");
        ubs.setCodigoCNES("123456");
        ubs.setEndereco("Rua Teste, 123");
        ubs.setCidade("Cidade Teste");
        ubs.setEstado("SP");
        ubs.setCep("12345678");
        ubs.setTelefone("11999999999");
        ubs.setAtiva(true);

        ubsDTO = new UBSDTO();
        ubsDTO.setId(1L);
        ubsDTO.setNome("UBS Teste");
        ubsDTO.setCodigoCNES("123456");
        ubsDTO.setEndereco("Rua Teste, 123");
        ubsDTO.setCidade("Cidade Teste");
        ubsDTO.setEstado("SP");
        ubsDTO.setCep("12345678");
        ubsDTO.setTelefone("11999999999");
        ubsDTO.setAtiva(true);
    }

    @Test
    @DisplayName("Deve listar todas as UBS com sucesso")
    void listarTodasSuccess() {
        List<UBS> ubsList = Arrays.asList(ubs);
        when(ubsRepository.findAll()).thenReturn(ubsList);

        List<UBSDTO> result = ubsService.listarTodas();

        assertEquals(1, result.size());
        assertEquals(ubs.getId(), result.get(0).getId());
        assertEquals(ubs.getNome(), result.get(0).getNome());
        verify(ubsRepository).findAll();
    }

    @Test
    @DisplayName("Deve obter UBS por ID com sucesso")
    void obterPorIdSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));

        UBSDTO result = ubsService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(ubs.getId(), result.getId());
        assertEquals(ubs.getNome(), result.getNome());
        verify(ubsRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar UBS inexistente")
    void obterPorIdInexistente() {
        when(ubsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ubsService.obterPorId(999L));
        verify(ubsRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve criar UBS com sucesso")
    void criarUBSSuccess() {
        when(ubsRepository.findByNome("UBS Teste")).thenReturn(Optional.empty());
        when(ubsRepository.save(any(UBS.class))).thenReturn(ubs);

        UBSDTO result = ubsService.criar(ubsDTO);

        assertNotNull(result);
        assertEquals(ubs.getId(), result.getId());
        assertEquals(ubs.getNome(), result.getNome());
        verify(ubsRepository).findByNome("UBS Teste");
        verify(ubsRepository).save(any(UBS.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao criar UBS com nome já existente")
    void criarUBSNomeExistente() {
        when(ubsRepository.findByNome("UBS Teste")).thenReturn(Optional.of(ubs));

        assertThrows(BadRequestException.class, () -> ubsService.criar(ubsDTO));
        verify(ubsRepository).findByNome("UBS Teste");
        verify(ubsRepository, never()).save(any(UBS.class));
    }

    @Test
    @DisplayName("Deve atualizar UBS com sucesso")
    void atualizarUBSSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(ubsRepository.save(any(UBS.class))).thenReturn(ubs);

        UBSDTO result = ubsService.atualizar(1L, ubsDTO);

        assertNotNull(result);
        assertEquals(ubs.getId(), result.getId());
        assertEquals(ubs.getNome(), result.getNome());
        verify(ubsRepository).findById(1L);
        verify(ubsRepository).save(any(UBS.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar UBS inexistente")
    void atualizarUBSInexistente() {
        when(ubsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ubsService.atualizar(999L, ubsDTO));
        verify(ubsRepository).findById(999L);
        verify(ubsRepository, never()).save(any(UBS.class));
    }

    @Test
    @DisplayName("Deve deletar UBS com sucesso")
    void deletarUBSSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));

        ubsService.deletar(1L);

        verify(ubsRepository).findById(1L);
        verify(ubsRepository).delete(ubs);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar UBS inexistente")
    void deletarUBSInexistente() {
        when(ubsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ubsService.deletar(999L));
        verify(ubsRepository).findById(999L);
        verify(ubsRepository, never()).delete(any(UBS.class));
    }

    @Test
    @DisplayName("Deve importar UBS via CNES com sucesso")
    void importarUBSviaCNESSuccess() {
        List<CnesMockService.CneEstabelecimentoResponse> estabelecimentos = Arrays.asList(
                new CnesMockService.CneEstabelecimentoResponse("123456", "UBS Teste", "Rua Teste", "Cidade Teste", "SP", "12345678", "11999999999", "0.0", "0.0")
        );
        
        when(ubsRepository.findByCodigoCNES("123456")).thenReturn(Optional.empty());
        when(cnesMockService.importarUBS("12345")).thenReturn(estabelecimentos);
        when(ubsRepository.save(any(UBS.class))).thenReturn(ubs);

        int result = ubsService.importarUBSviaCNES("12345");

        assertEquals(1, result);
        verify(cnesMockService).importarUBS("12345");
        verify(ubsRepository).findByCodigoCNES("123456");
        verify(ubsRepository).save(any(UBS.class));
    }

    @Test
    @DisplayName("Deve ignorar UBS já existente ao importar via CNES")
    void importarUBSviaCNESSkipExistente() {
        List<CnesMockService.CneEstabelecimentoResponse> estabelecimentos = Arrays.asList(
                new CnesMockService.CneEstabelecimentoResponse("123456", "UBS Teste", "Rua Teste", "Cidade Teste", "SP", "12345678", "11999999999", "0.0", "0.0")
        );
        
        when(ubsRepository.findByCodigoCNES("123456")).thenReturn(Optional.of(ubs));
        when(cnesMockService.importarUBS("12345")).thenReturn(estabelecimentos);

        int result = ubsService.importarUBSviaCNES("12345");

        assertEquals(0, result);
        verify(cnesMockService).importarUBS("12345");
        verify(ubsRepository).findByCodigoCNES("123456");
        verify(ubsRepository, never()).save(any(UBS.class));
    }
}
