package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Agendamento;
import br.com.fiap.postech.meu_postinho.domain.Agendamento.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByPacienteId(Long usuarioId);
    List<Agendamento> findByStatus(Status status);
    Optional<Agendamento> findByVagaIdAndStatus(Long vagaId, Status status);
    List<Agendamento> findByVagaId(Long vagaId);
    List<Agendamento> findByPacienteIdAndStatusIn(Long usuarioId, List<Status> statuses);
}
