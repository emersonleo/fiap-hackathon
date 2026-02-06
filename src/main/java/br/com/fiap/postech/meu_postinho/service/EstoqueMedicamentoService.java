package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.EstoqueMedicamento;
import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.EstoqueMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.EstoqueMedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.MedicamentoRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueMedicamentoService {
    
    @Autowired
    private EstoqueMedicamentoRepository estoqueRepository;
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    public List<EstoqueMedicamentoDTO> listarPorUBS(Long ubsId) {
        UBS ubs = ubsRepository.findById(ubsId)
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        return estoqueRepository.findByUbs(ubs).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public EstoqueMedicamentoDTO obterPorId(Long id) {
        EstoqueMedicamento estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        return converterParaDTO(estoque);
    }
    
    public EstoqueMedicamentoDTO criar(EstoqueMedicamentoDTO estoqueDTO) {
        UBS ubs = ubsRepository.findById(estoqueDTO.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        Medicamento medicamento = medicamentoRepository.findById(estoqueDTO.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        
        // Verificar se já existe estoque para este medicamento nesta UBS
        var existente = estoqueRepository.findByUbsIdAndMedicamentoId(
                estoqueDTO.getUbsId(), estoqueDTO.getMedicamentoId());
        if (existente.isPresent()) {
            throw new BadRequestException("Já existe um estoque para este medicamento nesta UBS");
        }
        
        if (estoqueDTO.getQuantidade() < 0) {
            throw new BadRequestException("Quantidade não pode ser negativa");
        }
        
        EstoqueMedicamento estoque = new EstoqueMedicamento();
        estoque.setUbs(ubs);
        estoque.setMedicamento(medicamento);
        estoque.setQuantidade(estoqueDTO.getQuantidade());
        estoque.setQuantidadeMinima(estoqueDTO.getQuantidadeMinima() != null ? 
                estoqueDTO.getQuantidadeMinima() : 10);
        estoque.setDataEntrada(LocalDateTime.now());
        estoque.setEmFalta(estoque.getQuantidade() <= 0);
        
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }
    
    public EstoqueMedicamentoDTO atualizarQuantidade(Long id, Integer novaQuantidade) {
        EstoqueMedicamento estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        
        if (novaQuantidade < 0) {
            throw new BadRequestException("Quantidade não pode ser negativa");
        }
        
        Integer quantidadeAnterior = estoque.getQuantidade();
        estoque.setQuantidade(novaQuantidade);
        estoque.setEmFalta(novaQuantidade <= 0);
        
        // Log de alerta se estoque baixo
        if (novaQuantidade < estoque.getQuantidadeMinima() && quantidadeAnterior >= estoque.getQuantidadeMinima()) {
            // Aqui poderia disparar um alerta/notificação
            // logger.warn("Estoque baixo para medicamento: " + estoque.getMedicamento().getNome());
        }
        
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }
    
    public EstoqueMedicamentoDTO atualizar(Long id, EstoqueMedicamentoDTO estoqueDTO) {
        EstoqueMedicamento estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        
        if (estoqueDTO.getQuantidade() != null && estoqueDTO.getQuantidade() < 0) {
            throw new BadRequestException("Quantidade não pode ser negativa");
        }
        
        if (estoqueDTO.getQuantidade() != null) {
            estoque.setQuantidade(estoqueDTO.getQuantidade());
            estoque.setEmFalta(estoqueDTO.getQuantidade() <= 0);
        }
        
        if (estoqueDTO.getQuantidadeMinima() != null) {
            estoque.setQuantidadeMinima(estoqueDTO.getQuantidadeMinima());
        }
        
        if (estoqueDTO.getDataVencimento() != null) {
            estoque.setDataVencimento(estoqueDTO.getDataVencimento());
        }
        
        estoque = estoqueRepository.save(estoque);
        return converterParaDTO(estoque);
    }
    
    public void deletar(Long id) {
        EstoqueMedicamento estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        estoqueRepository.delete(estoque);
    }
    
    private EstoqueMedicamentoDTO converterParaDTO(EstoqueMedicamento estoque) {
        EstoqueMedicamentoDTO dto = new EstoqueMedicamentoDTO();
        dto.setId(estoque.getId());
        dto.setUbsId(estoque.getUbs().getId());
        dto.setMedicamentoId(estoque.getMedicamento().getId());
        dto.setQuantidade(estoque.getQuantidade());
        dto.setQuantidadeMinima(estoque.getQuantidadeMinima());
        dto.setDataEntrada(estoque.getDataEntrada());
        dto.setDataVencimento(estoque.getDataVencimento());
        dto.setEmFalta(estoque.getEmFalta());
        dto.setDataCriacao(estoque.getDataCriacao());
        dto.setDataAtualizacao(estoque.getDataAtualizacao());
        return dto;
    }
}
