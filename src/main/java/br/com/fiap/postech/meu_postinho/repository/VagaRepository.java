package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByUbsId(Long ubsId);
    List<Vaga> findByUbsIdAndData(Long ubsId, LocalDate data);
    List<Vaga> findByUbsIdAndEspecialidade(Long ubsId, String especialidade);
    List<Vaga> findByUbsIdAndDisponivelTrue(Long ubsId);
    List<Vaga> findByDataAfterAndDisponivelTrue(LocalDate data);
}
