package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Noticia;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.NoticiaDTO;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.NoticiaRepository;
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
class NoticiaServiceTest {

    @Mock
    private NoticiaRepository noticiaRepository;

    @Mock
    private UBSRepository ubsRepository;

    @InjectMocks
    private NoticiaService noticiaService;

    private Noticia noticia;
    private NoticiaDTO noticiaDTO;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        noticia = new Noticia();
        noticia.setId(1L);
        noticia.setUbs(ubs);
        noticia.setTitulo("Noticia Teste");
        noticia.setConteudo("Conteúdo da notícia");
        noticia.setAtivo(true);

        noticiaDTO = new NoticiaDTO();
        noticiaDTO.setId(1L);
        noticiaDTO.setUbsId(1L);
        noticiaDTO.setTitulo("Noticia Teste");
        noticiaDTO.setConteudo("Conteúdo da notícia");
        noticiaDTO.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar notícia com sucesso")
    void criarNoticiaSuccess() {
        when(ubsRepository.findById(1L)).thenReturn(Optional.of(ubs));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(noticia);

        NoticiaDTO result = noticiaService.criar(noticiaDTO);

        assertNotNull(result);
        assertEquals(noticia.getId(), result.getId());
        assertEquals(noticia.getTitulo(), result.getTitulo());
        verify(ubsRepository).findById(1L);
        verify(noticiaRepository).save(any(Noticia.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar notícia com UBS inexistente")
    void criarNoticiaUBSInexistente() {
        NoticiaDTO dtoComUBSInvalida = new NoticiaDTO();
        dtoComUBSInvalida.setUbsId(999L);
        dtoComUBSInvalida.setTitulo("Noticia Teste");
        dtoComUBSInvalida.setConteudo("Conteúdo da notícia");
        
        when(ubsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noticiaService.criar(dtoComUBSInvalida));
        verify(ubsRepository).findById(999L);
        verify(noticiaRepository, never()).save(any(Noticia.class));
    }

    @Test
    @DisplayName("Deve obter notícia por ID com sucesso")
    void obterPorIdSuccess() {
        when(noticiaRepository.findById(1L)).thenReturn(Optional.of(noticia));

        NoticiaDTO result = noticiaService.obterPorId(1L);

        assertNotNull(result);
        assertEquals(noticia.getId(), result.getId());
        verify(noticiaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar notícia inexistente")
    void obterPorIdInexistente() {
        when(noticiaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noticiaService.obterPorId(999L));
        verify(noticiaRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve listar notícias por UBS com sucesso")
    void listarPorUBSSuccess() {
        List<Noticia> noticias = Arrays.asList(noticia);
        when(noticiaRepository.findByUbsIdOrderByDataPublicacaoDesc(1L)).thenReturn(noticias);

        List<NoticiaDTO> result = noticiaService.listarPorUBS(1L);

        assertEquals(1, result.size());
        assertEquals(noticia.getId(), result.get(0).getId());
        verify(noticiaRepository).findByUbsIdOrderByDataPublicacaoDesc(1L);
    }

    @Test
    @DisplayName("Deve atualizar notícia com sucesso")
    void atualizarNoticiaSuccess() {
        when(noticiaRepository.findById(1L)).thenReturn(Optional.of(noticia));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(noticia);

        NoticiaDTO result = noticiaService.atualizar(1L, noticiaDTO);

        assertNotNull(result);
        assertEquals(noticia.getId(), result.getId());
        verify(noticiaRepository).findById(1L);
        verify(noticiaRepository).save(any(Noticia.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar notícia inexistente")
    void atualizarNoticiaInexistente() {
        when(noticiaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noticiaService.atualizar(999L, noticiaDTO));
        verify(noticiaRepository).findById(999L);
        verify(noticiaRepository, never()).save(any(Noticia.class));
    }

    @Test
    @DisplayName("Deve desativar notícia ao deletar")
    void deletarNoticiaSuccess() {
        when(noticiaRepository.findById(1L)).thenReturn(Optional.of(noticia));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(noticia);

        noticiaService.deletar(1L);

        verify(noticiaRepository).findById(1L);
        verify(noticiaRepository).save(noticia);
        assertFalse(noticia.getAtivo());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar notícia inexistente")
    void deletarNoticiaInexistente() {
        when(noticiaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noticiaService.deletar(999L));
        verify(noticiaRepository).findById(999L);
        verify(noticiaRepository, never()).save(any(Noticia.class));
    }
}
