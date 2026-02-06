package br.com.fiap.postech.meu_postinho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoMedicamentoDTO {
    
    private Long id;
    
    private Long usuarioId;
    
    @NotNull(message = "Medicamento é obrigatório")
    private Long medicamentoId;
    
    @NotNull(message = "UBS é obrigatória")
    private Long ubsId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;
    
    private String status;
    
    private String justificativaRecusa;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
    
    private LocalDateTime dataProcessamento;
}
