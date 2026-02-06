package br.com.fiap.postech.meu_postinho.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String cpf;
    private String nome;
    private String email;
    private String role;
    private Long ubsId;
    
    public AuthResponse(String token, Long id, String cpf, String nome, String email, String role, Long ubsId) {
        this.token = token;
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.ubsId = ubsId;
    }
}
