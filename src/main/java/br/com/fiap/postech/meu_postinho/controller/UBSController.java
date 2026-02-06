package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.UBSDTO;
import br.com.fiap.postech.meu_postinho.service.UBSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubs")
@Tag(name = "UBS", description = "Gerenciamento de Unidades Básicas de Saúde")
public class UBSController {
    
    @Autowired
    private UBSService ubsService;
    
    @GetMapping
    @Operation(summary = "Listar todas as UBS", description = "Retorna lista de todas as UBS cadastradas")
    public ResponseEntity<List<UBSDTO>> listarTodas() {
        return ResponseEntity.ok(ubsService.listarTodas());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obter UBS por ID", description = "Retorna detalhes de uma UBS específica")
    public ResponseEntity<UBSDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubsService.obterPorId(id));
    }
    
    @PostMapping
    @Operation(summary = "Criar nova UBS", description = "Cria uma nova unidade básica de saúde")
    public ResponseEntity<UBSDTO> criar(@Valid @RequestBody UBSDTO ubsDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubsService.criar(ubsDTO));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar UBS", description = "Atualiza informações de uma UBS")
    public ResponseEntity<UBSDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UBSDTO ubsDTO) {
        return ResponseEntity.ok(ubsService.atualizar(id, ubsDTO));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar UBS", description = "Remove uma UBS do sistema")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ubsService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/importar")
    @Operation(summary = "Importar UBS via CNES", description = "Importa UBS de um município via CNES (Admin only)")
    public ResponseEntity<String> importarUBSviaCNES(@RequestParam String codigoMunicipio) {
        int quantidade = ubsService.importarUBSviaCNES(codigoMunicipio);
        return ResponseEntity.ok("Total de " + quantidade + " UBS importadas com sucesso");
    }
}
