package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"paciente", "vaga"})
public class Agendamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    // Compatibilidade com código existente que usava `usuario`
    public Usuario getUsuario() {
        return this.paciente;
    }

    public void setUsuario(Usuario usuario) {
        this.paciente = usuario;
    }
    
    @Convert(converter = ConversorStatus.class)
    @Column(name = "status", nullable = false)
    private Status status = Status.CONFIRMADO;
    
    @Column(name = "observacoes")
    private String observacoes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "updated_at")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;
    
    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;
    
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public enum Status {
        CONFIRMADO("Agendamento confirmado"),
        COMPARECEU("Paciente compareceu"),
        NAO_COMPARECEU("Paciente não compareceu"),
        CANCELADO("Agendamento cancelado");
        
        private final String descricao;
        
        Status(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
}
