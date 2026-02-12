package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.AgendamentoDTO;
import br.com.fiap.postech.meu_postinho.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos de consultas")
public class AgendamentoController {
    
    @Autowired
    private AgendamentoService agendamentoService;
    
    @PostMapping
    @Operation(summary = "Agendar consulta", description = "Cria um novo agendamento em uma vaga disponível")
    public ResponseEntity<AgendamentoDTO> criar(
            @RequestParam Long usuarioId,
            @RequestParam Long vagaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.criar(usuarioId, vagaId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter agendamento por ID", description = "Retorna detalhes de um agendamento")
    public ResponseEntity<AgendamentoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.obterPorId(id));
    }
    
    @GetMapping("/meus")
    @Operation(summary = "Meus agendamentos", description = "Retorna agendamentos do usuário autenticado")
    public ResponseEntity<List<AgendamentoDTO>> listarMeus(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(agendamentoService.listarPorUsuario(usuarioId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar agendamento", description = "Cancela um agendamento e libera a vaga")
    public ResponseEntity<AgendamentoDTO> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivoCancelamento) {
        return ResponseEntity.ok(agendamentoService.cancelar(id, motivoCancelamento));
    }

    @PutMapping("/{id}/confirmacao")
    @Operation(summary = "Confirmar ou desistir do agendamento", description = "Paciente confirma presença (CONFIRMAR) ou desiste (DESISTIR)")
    public ResponseEntity<AgendamentoDTO> confirmarOuDesistir(
            @PathVariable Long id,
            @RequestParam String acao) {
        return ResponseEntity.ok(agendamentoService.confirmarOuDesistir(id, acao));
    }

    @PostMapping("/{id}/remanejamento")
    @Operation(summary = "Remanejar agendamento", description = "Cancela o agendamento atual e remaneja a mesma vaga para outro usuário em transação única")
    public ResponseEntity<AgendamentoDTO> remanejar(
            @PathVariable Long id,
            @RequestParam Long novoUsuarioId) {
        return ResponseEntity.ok(agendamentoService.remanejar(id, novoUsuarioId));
    }
    
    @PutMapping("/{id}/comparecimento")
    @Operation(summary = "Marcar comparecimento", description = "Marca um agendamento como comparecimento")
    public ResponseEntity<AgendamentoDTO> marcarComparecimento(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.marcarComparecimento(id));
    }
    
    @PutMapping("/{id}/nao-comparecimento")
    @Operation(summary = "Marcar não-comparecimento", description = "Marca um agendamento como não-comparecimento (no-show)")
    public ResponseEntity<AgendamentoDTO> marcarNaoComparecimento(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.marcarNaoComparecimento(id));
    }
}
