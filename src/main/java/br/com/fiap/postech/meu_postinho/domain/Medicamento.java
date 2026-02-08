package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"estoques", "solicitacoes"})
public class Medicamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "descricao")
    private String descricao;
    
    @NotBlank
    @Column(nullable = false)
    private String categoria;
    
    @Column(name = "posologia")
    private String posologia;
    
    @Column(name = "unidade", nullable = false)
    private String unidade;
    
    @Column(name = "codigo_catmat", unique = true)
    private String codigoCATMAT;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "updated_at")
    private LocalDateTime dataAtualizacao;
    
    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EstoqueMedicamento> estoques = new HashSet<>();
    
    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SolicitacaoMedicamento> solicitacoes = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
