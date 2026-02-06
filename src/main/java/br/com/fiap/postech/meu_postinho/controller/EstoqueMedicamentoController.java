package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.EstoqueMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.service.EstoqueMedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoques")
@Tag(name = "Estoque de Medicamentos", description = "Gerenciamento de estoque de medicamentos por UBS")
public class EstoqueMedicamentoController {
    
    @Autowired
    private EstoqueMedicamentoService estoqueService;
    
    @PostMapping
    @Operation(summary = "Criar estoque", description = "Vincula um medicamento ao estoque de uma UBS")
    public ResponseEntity<EstoqueMedicamentoDTO> criar(@Valid @RequestBody EstoqueMedicamentoDTO estoqueDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.criar(estoqueDTO));
    }
    
    @GetMapping("/ubs/{ubsId}")
    @Operation(summary = "Listar estoque da UBS", description = "Retorna todos os medicamentos em estoque de uma UBS")
    public ResponseEntity<List<EstoqueMedicamentoDTO>> listarPorUBS(@PathVariable Long ubsId) {
        return ResponseEntity.ok(estoqueService.listarPorUBS(ubsId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter estoque por ID", description = "Retorna detalhes de um estoque específico")
    public ResponseEntity<EstoqueMedicamentoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.obterPorId(id));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estoque", description = "Atualiza quantidade e informações de estoque")
    public ResponseEntity<EstoqueMedicamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EstoqueMedicamentoDTO estoqueDTO) {
        return ResponseEntity.ok(estoqueService.atualizar(id, estoqueDTO));
    }
    
    @PutMapping("/{id}/quantidade")
    @Operation(summary = "Atualizar quantidade", description = "Atualiza apenas a quantidade em estoque")
    public ResponseEntity<EstoqueMedicamentoDTO> atualizarQuantidade(@PathVariable Long id, @RequestParam Integer novaQuantidade) {
        return ResponseEntity.ok(estoqueService.atualizarQuantidade(id, novaQuantidade));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar estoque", description = "Remove um medicamento do estoque de uma UBS")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        estoqueService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
