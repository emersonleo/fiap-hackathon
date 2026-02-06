package br.com.fiap.postech.meu_postinho.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenteDTO {
    
    private Long id;
    
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150)
    private String nome;
    
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
    
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;
    
    private String dataNascimento;
    
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
    
    @NotBlank(message = "CEP é obrigatório")
    private String cep;
    
    @NotNull(message = "UBS é obrigatória")
    private Long ubsId;
    
    @NotBlank(message = "CNS é obrigatório")
    private String cns;
    
    @NotBlank(message = "Tipo de agente é obrigatório")
    private String tipo;
    
    @JsonProperty("statusCnes")
    private String statusCnes;
    
    private LocalDateTime dataVerificacaoCnes;
    
    private Boolean ativo;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
}
