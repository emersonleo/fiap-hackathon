package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento;
import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoMedicamentoRepository extends JpaRepository<SolicitacaoMedicamento, Long> {
    List<SolicitacaoMedicamento> findByUsuarioId(Long usuarioId);
    List<SolicitacaoMedicamento> findByUbsId(Long ubsId);
    List<SolicitacaoMedicamento> findByStatus(Status status);
    List<SolicitacaoMedicamento> findByUbsIdAndStatus(Long ubsId, Status status);
}
