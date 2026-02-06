package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento;
import br.com.fiap.postech.meu_postinho.domain.SolicitacaoMedicamento.Status;
import br.com.fiap.postech.meu_postinho.dto.SolicitacaoMedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitacaoMedicamentoService {
    
    @Autowired
    private SolicitacaoMedicamentoRepository solicitacaoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    @Autowired
    private EstoqueMedicamentoRepository estoqueRepository;
    
    public SolicitacaoMedicamentoDTO criar(SolicitacaoMedicamentoDTO dto) {
        var usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        var medicamento = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        var ubs = ubsRepository.findById(dto.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        if (dto.getQuantidade() <= 0) {
            throw new BadRequestException("Quantidade deve ser maior que zero");
        }
        
        SolicitacaoMedicamento solicitacao = new SolicitacaoMedicamento();
        solicitacao.setUsuario(usuario);
        solicitacao.setMedicamento(medicamento);
        solicitacao.setUbs(ubs);
        solicitacao.setQuantidade(dto.getQuantidade());
        solicitacao.setStatus(Status.PENDENTE);
        
        solicitacao = solicitacaoRepository.save(solicitacao);
        return converterParaDTO(solicitacao);
    }
    
    public SolicitacaoMedicamentoDTO obterPorId(Long id) {
        SolicitacaoMedicamento solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        return converterParaDTO(solicitacao);
    }
    
    public List<SolicitacaoMedicamentoDTO> listarPorUsuario(Long usuarioId) {
        return solicitacaoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<SolicitacaoMedicamentoDTO> listarPendentes(Long ubsId) {
        return solicitacaoRepository.findByUbsIdAndStatus(ubsId, Status.PENDENTE).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public SolicitacaoMedicamentoDTO aceitar(Long id) {
        SolicitacaoMedicamento solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        
        if (!solicitacao.getStatus().equals(Status.PENDENTE)) {
            throw new BadRequestException("Apenas solicitações pendentes podem ser aceitas");
        }
        
        // Validar estoque
        var estoque = estoqueRepository.findByUbsIdAndMedicamentoId(
                solicitacao.getUbs().getId(), solicitacao.getMedicamento().getId())
                .orElseThrow(() -> new BadRequestException("Medicamento não disponível no estoque"));
        
        if (estoque.getQuantidade() < solicitacao.getQuantidade()) {
            throw new BadRequestException("Quantidade em estoque insuficiente");
        }
        
        // Decrementar estoque
        estoque.setQuantidade(estoque.getQuantidade() - solicitacao.getQuantidade());
        estoqueRepository.save(estoque);
        
        solicitacao.setStatus(Status.ACEITA);
        solicitacao.setDataProcessamento(LocalDateTime.now());
        
        solicitacao = solicitacaoRepository.save(solicitacao);
        return converterParaDTO(solicitacao);
    }
    
    public SolicitacaoMedicamentoDTO recusar(Long id, String justificativa) {
        SolicitacaoMedicamento solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        
        if (!solicitacao.getStatus().equals(Status.PENDENTE)) {
            throw new BadRequestException("Apenas solicitações pendentes podem ser recusadas");
        }
        
        solicitacao.setStatus(Status.RECUSADA);
        solicitacao.setJustificativaRecusa(justificativa);
        solicitacao.setDataProcessamento(LocalDateTime.now());
        
        solicitacao = solicitacaoRepository.save(solicitacao);
        return converterParaDTO(solicitacao);
    }
    
    private SolicitacaoMedicamentoDTO converterParaDTO(SolicitacaoMedicamento solicitacao) {
        SolicitacaoMedicamentoDTO dto = new SolicitacaoMedicamentoDTO();
        dto.setId(solicitacao.getId());
        dto.setUsuarioId(solicitacao.getUsuario().getId());
        dto.setMedicamentoId(solicitacao.getMedicamento().getId());
        dto.setUbsId(solicitacao.getUbs().getId());
        dto.setQuantidade(solicitacao.getQuantidade());
        dto.setStatus(solicitacao.getStatus().name());
        dto.setJustificativaRecusa(solicitacao.getJustificativaRecusa());
        dto.setDataCriacao(solicitacao.getDataCriacao());
        dto.setDataAtualizacao(solicitacao.getDataAtualizacao());
        dto.setDataProcessamento(solicitacao.getDataProcessamento());
        return dto;
    }
}
