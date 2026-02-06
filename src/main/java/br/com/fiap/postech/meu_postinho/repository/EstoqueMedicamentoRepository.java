package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.EstoqueMedicamento;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueMedicamentoRepository extends JpaRepository<EstoqueMedicamento, Long> {
    List<EstoqueMedicamento> findByUbs(UBS ubs);
    Optional<EstoqueMedicamento> findByUbsIdAndMedicamentoId(Long ubsId, Long medicamentoId);
    List<EstoqueMedicamento> findByUbsIdAndEmFaltaTrue(Long ubsId);
    List<EstoqueMedicamento> findByEmFaltaTrue();
}
