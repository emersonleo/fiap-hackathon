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
public class EstoqueMedicamentoDTO {
    
    private Long id;
    
    @NotNull(message = "UBS é obrigatória")
    private Long ubsId;
    
    @NotNull(message = "Medicamento é obrigatório")
    private Long medicamentoId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;
    
    private Integer quantidadeMinima;
    
    private LocalDateTime dataEntrada;
    
    private LocalDateTime dataVencimento;
    
    private Boolean emFalta;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
}
