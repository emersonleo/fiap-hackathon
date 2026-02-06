package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.AgenteDTO;
import br.com.fiap.postech.meu_postinho.service.AgenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agentes")
@Tag(name = "Agentes", description = "Gerenciamento de Agentes (ACS/ACE)")
public class AgenteController {
    
    @Autowired
    private AgenteService agenteService;
    
    @PostMapping
    @Operation(summary = "Registrar novo agente", description = "Cria um novo agente e valida CPF + CNS via CNES")
    public ResponseEntity<AgenteDTO> registrar(@Valid @RequestBody AgenteDTO agenteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenteService.registrarAgente(agenteDTO));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter agente por ID", description = "Retorna detalhes de um agente")
    public ResponseEntity<AgenteDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agenteService.obterAgentePorId(id));
    }
}
