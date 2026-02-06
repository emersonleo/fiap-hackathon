package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    Optional<Medicamento> findByNome(String nome);
    Optional<Medicamento> findByCodigoCATMAT(String codigoCATMAT);
    List<Medicamento> findByCategoria(String categoria);
    List<Medicamento> findByAtivoTrue();
}
