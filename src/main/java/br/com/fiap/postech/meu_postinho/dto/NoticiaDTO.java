package br.com.fiap.postech.meu_postinho.dto;

import jakarta.validation.constraints.NotBlank;
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
public class NoticiaDTO {
    
    private Long id;
    
    @NotNull(message = "UBS é obrigatória")
    private Long ubsId;
    
    @NotBlank(message = "Título é obrigatório")
    private String titulo;
    
    @NotBlank(message = "Conteúdo é obrigatório")
    private String conteudo;
    
    private LocalDateTime dataPublicacao;
    
    private Boolean ativo;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
}
