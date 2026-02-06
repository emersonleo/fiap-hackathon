package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.UBS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UBSRepository extends JpaRepository<UBS, Long> {
    Optional<UBS> findByNome(String nome);
    Optional<UBS> findByCodigoCNES(String codigoCNES);
}
