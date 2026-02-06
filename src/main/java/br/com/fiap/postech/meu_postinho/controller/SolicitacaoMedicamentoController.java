package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.SolicitacaoMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.service.SolicitacaoMedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@Tag(name = "Solicitações de Medicamentos", description = "Gerenciamento de pedidos de medicamentos")
public class SolicitacaoMedicamentoController {
    
    @Autowired
    private SolicitacaoMedicamentoService solicitacaoService;
    
    @PostMapping
    @Operation(summary = "Criar solicitação", description = "Cria uma nova solicitação de medicamento")
    public ResponseEntity<SolicitacaoMedicamentoDTO> criar(@Valid @RequestBody SolicitacaoMedicamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoService.criar(dto));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter solicitação por ID", description = "Retorna detalhes de uma solicitação")
    public ResponseEntity<SolicitacaoMedicamentoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.obterPorId(id));
    }
    
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar solicitações do usuário", description = "Retorna todas as solicitações de um morador")
    public ResponseEntity<List<SolicitacaoMedicamentoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(solicitacaoService.listarPorUsuario(usuarioId));
    }
    
    @GetMapping("/ubs/{ubsId}/pendentes")
    @Operation(summary = "Listar solicitações pendentes", description = "Retorna solicitações pendentes de processamento da UBS")
    public ResponseEntity<List<SolicitacaoMedicamentoDTO>> listarPendentes(@PathVariable Long ubsId) {
        return ResponseEntity.ok(solicitacaoService.listarPendentes(ubsId));
    }
    
    @PutMapping("/{id}/aceitar")
    @Operation(summary = "Aceitar solicitação", description = "Aprova uma solicitação e decrementa estoque")
    public ResponseEntity<SolicitacaoMedicamentoDTO> aceitar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.aceitar(id));
    }
    
    @PutMapping("/{id}/recusar")
    @Operation(summary = "Recusar solicitação", description = "Rejeita uma solicitação com justificativa")
    public ResponseEntity<SolicitacaoMedicamentoDTO> recusar(@PathVariable Long id, @RequestParam String justificativa) {
        return ResponseEntity.ok(solicitacaoService.recusar(id, justificativa));
    }
}
