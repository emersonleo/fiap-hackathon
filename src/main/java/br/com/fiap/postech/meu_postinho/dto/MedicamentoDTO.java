package br.com.fiap.postech.meu_postinho.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoDTO {
    
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;
    
    private String posologia;
    
    @NotBlank(message = "Unidade é obrigatória")
    private String unidade;
    
    private String codigoCATMAT;
    
    private Boolean ativo;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
}
