package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ubs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuarios", "agentes", "estoques", "vagas", "noticias"})
public class UBS {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(name = "codigo_cnes", unique = true)
    private String codigoCNES;
    
    @NotBlank
    @Column(nullable = false)
    private String endereco;
    
    @NotBlank
    @Column(nullable = false)
    private String cep;
    
    @NotBlank
    @Column(nullable = false)
    private String telefone;
    
    @Column(name = "cidade", nullable = false)
    private String cidade;
    
    @Column(name = "estado", nullable = false)
    private String estado;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "updated_at")
    private LocalDateTime dataAtualizacao;
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Usuario> usuarios = new HashSet<>();
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Agente> agentes = new HashSet<>();
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EstoqueMedicamento> estoques = new HashSet<>();
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vaga> vagas = new HashSet<>();
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Noticia> noticias = new HashSet<>();
    
    @OneToMany(mappedBy = "ubs", cascade = CascadeType.ALL, orphanRemoval = true)
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
