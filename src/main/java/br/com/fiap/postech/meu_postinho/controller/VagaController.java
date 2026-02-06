package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.VagaDTO;
import br.com.fiap.postech.meu_postinho.service.VagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vagas")
@Tag(name = "Vagas de Consulta", description = "Gerenciamento de vagas/horários para consultas")
public class VagaController {
    
    @Autowired
    private VagaService vagaService;
    
    @PostMapping
    @Operation(summary = "Criar vaga", description = "Cria uma nova vaga de consulta")
    public ResponseEntity<VagaDTO> criar(@Valid @RequestBody VagaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vagaService.criar(dto));
    }
    
    @GetMapping("/ubs/{ubsId}")
    @Operation(summary = "Listar vagas da UBS", description = "Retorna todas as vagas de uma UBS")
    public ResponseEntity<List<VagaDTO>> listarPorUBS(@PathVariable Long ubsId) {
        return ResponseEntity.ok(vagaService.listarPorUBS(ubsId));
    }
    
    @GetMapping("/disponiveis")
    @Operation(summary = "Listar vagas disponíveis", description = "Filtra vagas disponíveis por data e especialidade")
    public ResponseEntity<List<VagaDTO>> listarDisponiveis(
            @RequestParam Long ubsId,
            @RequestParam(required = false) LocalDate data,
            @RequestParam(required = false) String especialidade) {
        return ResponseEntity.ok(vagaService.listarDisponiveis(ubsId, data, especialidade));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter vaga por ID", description = "Retorna detalhes de uma vaga")
    public ResponseEntity<VagaDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vagaService.obterPorId(id));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar vaga", description = "Atualiza informações de uma vaga")
    public ResponseEntity<VagaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VagaDTO dto) {
        return ResponseEntity.ok(vagaService.atualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar vaga", description = "Remove uma vaga se não estiver ocupada")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vagaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
