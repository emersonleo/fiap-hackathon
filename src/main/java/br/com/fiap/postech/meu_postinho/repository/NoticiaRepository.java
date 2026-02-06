package br.com.fiap.postech.meu_postinho.repository;

import br.com.fiap.postech.meu_postinho.domain.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByUbsIdAndAtivoTrue(Long ubsId);
    List<Noticia> findByUbsIdOrderByDataPublicacaoDesc(Long ubsId);
    List<Noticia> findByAtivoTrue();
}
