package br.com.fiap.postech.meu_postinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VagaDTO {
    
    private Long id;
    
    @NotNull(message = "UBS é obrigatória")
    private Long ubsId;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
    
    @NotNull(message = "Hora início é obrigatória")
    private LocalTime horaInicio;
    
    @NotNull(message = "Hora fim é obrigatória")
    private LocalTime horaFim;
    
    @NotBlank(message = "Especialidade é obrigatória")
    private String especialidade;
    
    @NotBlank(message = "Profissional é obrigatório")
    private String profissional;
    
    private Boolean disponivel;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
}
