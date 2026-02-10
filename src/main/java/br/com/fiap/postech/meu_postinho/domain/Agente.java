package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@DiscriminatorValue("Agente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Agente extends Usuario {
    
    @NotBlank
    @Column(name = "cns", unique = true, nullable = true)
    private String cns;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tipo_agente", nullable = true)
    private TipoAgente tipo;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status_cnes", nullable = true)
    private StatusCNES statusCnes;
    
    @Column(name = "data_verificacao_cnes")
    private LocalDateTime dataVerificacaoCnes;
    
    public Agente(String cpf, String nome, String telefone, String email, String senha, 
                  String dataNascimento, String endereco, String cep, UBS ubs,
                  String cns, TipoAgente tipo, StatusCNES statusCnes) {
        super(null, cpf, nome, telefone, email, senha, dataNascimento, endereco, cep, ubs, new HashSet<>(), true, null, null, null, null);
        this.cns = cns;
        this.tipo = tipo;
        this.statusCnes = statusCnes;
        this.dataVerificacaoCnes = LocalDateTime.now();
        this.getRoles().clear();
        this.getRoles().add("ROLE_AGENTE");
    }
    
    public enum TipoAgente {
        ACS("Agente Comunitário de Saúde"),
        ACE("Agente de Combate a Endemias");
        
        private final String descricao;
        
        TipoAgente(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    public enum StatusCNES {
        VERIFICADO("Verificado no CNES"),
        PENDENTE("Pendente de verificação"),
        REJEITADO("Rejeitado");
        
        private final String descricao;
        
        StatusCNES(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
}
