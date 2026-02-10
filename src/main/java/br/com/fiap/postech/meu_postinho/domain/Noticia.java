package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "noticia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"ubs"})
public class Noticia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubs_id", nullable = false)
    private UBS ubs;
    
    @NotBlank
    @Column(nullable = false)
    private String titulo;
    
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String conteudo;
    
    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        if (this.dataPublicacao == null) {
            this.dataPublicacao = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
