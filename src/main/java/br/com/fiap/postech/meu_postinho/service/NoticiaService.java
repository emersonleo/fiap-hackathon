package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Noticia;
import br.com.fiap.postech.meu_postinho.dto.NoticiaDTO;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.NoticiaRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticiaService {
    
    @Autowired
    private NoticiaRepository noticiaRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    public NoticiaDTO criar(NoticiaDTO dto) {
        var ubs = ubsRepository.findById(dto.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        Noticia noticia = new Noticia();
        noticia.setUbs(ubs);
        noticia.setTitulo(dto.getTitulo());
        noticia.setConteudo(dto.getConteudo());
        noticia.setDataPublicacao(LocalDateTime.now());
        noticia.setAtivo(true);
        
        noticia = noticiaRepository.save(noticia);
        return converterParaDTO(noticia);
    }
    
    public NoticiaDTO obterPorId(Long id) {
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada"));
        return converterParaDTO(noticia);
    }
    
    public List<NoticiaDTO> listarPorUBS(Long ubsId) {
        return noticiaRepository.findByUbsIdOrderByDataPublicacaoDesc(ubsId).stream()
                .filter(Noticia::getAtivo)
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public NoticiaDTO atualizar(Long id, NoticiaDTO dto) {
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada"));
        
        noticia.setTitulo(dto.getTitulo());
        noticia.setConteudo(dto.getConteudo());
        noticia.setAtivo(dto.getAtivo());
        
        noticia = noticiaRepository.save(noticia);
        return converterParaDTO(noticia);
    }
    
    public void deletar(Long id) {
        Noticia noticia = noticiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada"));
        noticia.setAtivo(false);
        noticiaRepository.save(noticia);
    }
    
    private NoticiaDTO converterParaDTO(Noticia noticia) {
        NoticiaDTO dto = new NoticiaDTO();
        dto.setId(noticia.getId());
        dto.setUbsId(noticia.getUbs().getId());
        dto.setTitulo(noticia.getTitulo());
        dto.setConteudo(noticia.getConteudo());
        dto.setDataPublicacao(noticia.getDataPublicacao());
        dto.setAtivo(noticia.getAtivo());
        dto.setDataCriacao(noticia.getDataCriacao());
        dto.setDataAtualizacao(noticia.getDataAtualizacao());
        return dto;
    }
}
