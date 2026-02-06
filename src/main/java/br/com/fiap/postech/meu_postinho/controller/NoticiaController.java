package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.NoticiaDTO;
import br.com.fiap.postech.meu_postinho.service.NoticiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
@Tag(name = "Notícias", description = "Gerenciamento de comunicados e notícias das UBS")
public class NoticiaController {
    
    @Autowired
    private NoticiaService noticiaService;
    
    @PostMapping
    @Operation(summary = "Criar notícia", description = "Publica uma nova notícia (Agentes apenas)")
    public ResponseEntity<NoticiaDTO> criar(@Valid @RequestBody NoticiaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticiaService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter notícia por ID", description = "Retorna detalhes de uma notícia")
    public ResponseEntity<NoticiaDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(noticiaService.obterPorId(id));
    }
    
    @GetMapping("/minhas")
    @Operation(summary = "Minhas notícias", description = "Retorna notícias da UBS do usuário autenticado")
    public ResponseEntity<List<NoticiaDTO>> listarMinhas(@RequestParam Long ubsId) {
        return ResponseEntity.ok(noticiaService.listarPorUBS(ubsId));
    }
    
    @GetMapping("/ubs/{ubsId}")
    @Operation(summary = "Notícias da UBS", description = "Retorna todas as notícias de uma UBS específica")
    public ResponseEntity<List<NoticiaDTO>> listarPorUBS(@PathVariable Long ubsId) {
        return ResponseEntity.ok(noticiaService.listarPorUBS(ubsId));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar notícia", description = "Edita uma notícia existente")
    public ResponseEntity<NoticiaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody NoticiaDTO dto) {
        return ResponseEntity.ok(noticiaService.atualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar notícia", description = "Remove uma notícia")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        noticiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
