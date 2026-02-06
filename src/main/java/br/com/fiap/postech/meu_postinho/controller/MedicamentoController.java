package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.MedicamentoDTO;
import br.com.fiap.postech.meu_postinho.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@Tag(name = "Medicamentos", description = "Gerenciamento de Medicamentos")
public class MedicamentoController {
    
    @Autowired
    private MedicamentoService medicamentoService;
    
    @GetMapping("/disponiveis")
    @Operation(summary = "Listar medicamentos disponíveis", description = "Retorna lista de medicamentos ativos")
    public ResponseEntity<List<MedicamentoDTO>> listarDisponiveis() {
        return ResponseEntity.ok(medicamentoService.listarTodos());
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os medicamentos", description = "Retorna lista de todos os medicamentos")
    public ResponseEntity<List<MedicamentoDTO>> listarTodos() {
        return ResponseEntity.ok(medicamentoService.listarTodos());
    }
    
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar por categoria", description = "Filtra medicamentos por categoria")
    public ResponseEntity<List<MedicamentoDTO>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(medicamentoService.listarPorCategoria(categoria));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter medicamento por ID", description = "Retorna detalhes de um medicamento")
    public ResponseEntity<MedicamentoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.obterPorId(id));
    }
    
    @PostMapping
    @Operation(summary = "Criar novo medicamento", description = "Cria um novo medicamento no catálogo")
    public ResponseEntity<MedicamentoDTO> criar(@Valid @RequestBody MedicamentoDTO medicamentoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoService.criar(medicamentoDTO));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar medicamento", description = "Atualiza informações de um medicamento")
    public ResponseEntity<MedicamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody MedicamentoDTO medicamentoDTO) {
        return ResponseEntity.ok(medicamentoService.atualizar(id, medicamentoDTO));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar medicamento", description = "Marca medicamento como inativo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        medicamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
