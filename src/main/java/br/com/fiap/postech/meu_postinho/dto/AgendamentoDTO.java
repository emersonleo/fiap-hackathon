package br.com.fiap.postech.meu_postinho.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoDTO {
    
    private Long id;
    
    private Long usuarioId;
    
    @NotNull(message = "Vaga é obrigatória")
    private Long vagaId;
    
    private String status;
    
    private String observacoes;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
    
    private LocalDateTime dataCancelamento;
    
    private String motivoCancelamento;
}
