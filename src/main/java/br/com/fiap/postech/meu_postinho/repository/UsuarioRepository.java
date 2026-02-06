package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByTelefone(String telefone);
    List<Usuario> findByUbsId(Long ubsId);
    boolean existsByCpf(String cpf);
    boolean existByTelefone(String telefone);
    boolean existsByEmail(String email);
}
