package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_medicamento", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ubs_id", "medicamento_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"ubs", "medicamento"})
public class EstoqueMedicamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubs_id", nullable = false)
    private UBS ubs;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;
    
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Column(name = "quantidade_minima", nullable = false)
    private Integer quantidadeMinima = 10;
    
    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;
    
    @Column(name = "data_vencimento")
    private LocalDateTime dataVencimento;
    
    @Column(name = "em_falta", nullable = false)
    private Boolean emFalta = false;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.emFalta = this.quantidade <= 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
        this.emFalta = this.quantidade <= 0;
    }
    
    public Integer calcularPercentualEstoque(Integer mediasMensais) {
        if (mediasMensais == null || mediasMensais == 0) {
            return 100;
        }
        return (quantidade * 100) / mediasMensais;
    }
    
    public Boolean estaAbaixoDoMinimo() {
        return quantidade < quantidadeMinima;
    }
}
