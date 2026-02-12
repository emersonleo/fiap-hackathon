package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Agendamento;
import br.com.fiap.postech.meu_postinho.domain.Agendamento.Status;
import br.com.fiap.postech.meu_postinho.dto.AgendamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private static final String ACAO_CONFIRMAR = "CONFIRMAR";
    private static final String ACAO_DESISTIR = "DESISTIR";
    
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private VagaRepository vagaRepository;
    
    public AgendamentoDTO criar(Long usuarioId, Long vagaId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        var vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada"));
        
        // Verificar se usuário já tem agendamento ativo
        List<Agendamento> agendamentosAtivos = agendamentoRepository.findByPacienteIdAndStatusIn(
                usuarioId, Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)
        );
        
        if (!agendamentosAtivos.isEmpty()) {
            throw new BadRequestException("Usuário já possui um agendamento ativo");
        }
        
        // Verificar se vaga já está ocupada
        if (!vaga.getDisponivel()) {
            throw new BadRequestException("Vaga não está disponível");
        }
        
        Agendamento agendamento = new Agendamento();
        agendamento.setUsuario(usuario);
        agendamento.setVaga(vaga);
        agendamento.setStatus(Status.CONFIRMADO);
        
        vaga.setDisponivel(false);
        vagaRepository.save(vaga);
        
        agendamento = agendamentoRepository.save(agendamento);
        return converterParaDTO(agendamento);
    }
    
    public AgendamentoDTO obterPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        return converterParaDTO(agendamento);
    }
    
    public List<AgendamentoDTO> listarPorUsuario(Long usuarioId) {
        return agendamentoRepository.findByPacienteId(usuarioId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public AgendamentoDTO cancelar(Long id, String motivoCancelamento) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        
        if (agendamento.getStatus().equals(Status.CANCELADO)) {
            throw new BadRequestException("Agendamento já foi cancelado");
        }
        agendamento.setStatus(Status.CANCELADO);
        agendamento.setMotivoCancelamento(motivoCancelamento);
        agendamento.setDataCancelamento(LocalDateTime.now());
        // Liberar vaga
        var vaga = agendamento.getVaga();
        vaga.setDisponivel(true);
        vagaRepository.save(vaga);
        agendamento = agendamentoRepository.save(agendamento);
        return converterParaDTO(agendamento);
    }

    public AgendamentoDTO confirmarOuDesistir(Long id, String acao) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (!agendamento.getStatus().equals(Status.CONFIRMADO)) {
            throw new BadRequestException("Ação de confirmação permitida apenas para agendamentos confirmados");
        }

        String acaoNormalizada = normalizarAcaoConfirmacao(acao);
        if (ACAO_DESISTIR.equals(acaoNormalizada)) {
            return cancelar(id, "Desistência informada pelo paciente");
        }

        return converterParaDTO(agendamento);
    }
    
    public AgendamentoDTO marcarComparecimento(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        
        if (!agendamento.getStatus().equals(Status.CONFIRMADO)) {
            throw new BadRequestException("Apenas agendamentos confirmados podem ser marcados como comparecimento");
        }
        
        agendamento.setStatus(Status.COMPARECEU);
        agendamento = agendamentoRepository.save(agendamento);
        return converterParaDTO(agendamento);
    }
    
    public AgendamentoDTO marcarNaoComparecimento(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        
        if (!agendamento.getStatus().equals(Status.CONFIRMADO)) {
            throw new BadRequestException("Apenas agendamentos confirmados podem ser marcados como não comparecimento");
        }
        
        agendamento.setStatus(Status.NAO_COMPARECEU);
        agendamento = agendamentoRepository.save(agendamento);
        return converterParaDTO(agendamento);
    }

    @Transactional
    public AgendamentoDTO remanejar(Long id, Long novoUsuarioId) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        Status statusAtual = agendamento.getStatus();
        if (!statusAtual.equals(Status.CONFIRMADO) && !statusAtual.equals(Status.CANCELADO)) {
            throw new BadRequestException("Apenas agendamentos confirmados ou cancelados podem ser remanejados");
        }

        Long usuarioAtualId = agendamento.getUsuario().getId();
        if (usuarioAtualId.equals(novoUsuarioId)) {
            throw new BadRequestException("Novo usuário deve ser diferente do paciente atual");
        }

        var novoUsuario = usuarioRepository.findById(novoUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Agendamento> agendamentosAtivos = agendamentoRepository.findByPacienteIdAndStatusIn(
                novoUsuarioId, Arrays.asList(Status.CONFIRMADO, Status.COMPARECEU)
        );

        if (!agendamentosAtivos.isEmpty()) {
            throw new BadRequestException("Usuário já possui um agendamento ativo");
        }

        if (statusAtual.equals(Status.CONFIRMADO)) {
            cancelar(id, "Remanejamento para usuário " + novoUsuarioId);

            agendamento = agendamentoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        }

        String observacaoRemanejamento = "Remanejado do usuário " + usuarioAtualId + " para " + novoUsuarioId;
        if (agendamento.getObservacoes() == null || agendamento.getObservacoes().isBlank()) {
            agendamento.setObservacoes(observacaoRemanejamento);
        } else {
            agendamento.setObservacoes(agendamento.getObservacoes() + " | " + observacaoRemanejamento);
        }

        agendamento.setUsuario(novoUsuario);
        agendamento.setStatus(Status.CONFIRMADO);
        agendamento.setDataCancelamento(null);
        agendamento.setMotivoCancelamento(null);

        // Garante consistência do estado da vaga na transação de remanejamento
        var vaga = agendamento.getVaga();
        vaga.setDisponivel(false);
        vagaRepository.save(vaga);

        agendamento = agendamentoRepository.save(agendamento);
        return converterParaDTO(agendamento);
    }

    private String normalizarAcaoConfirmacao(String acao) {
        if (acao == null || acao.isBlank()) {
            throw new BadRequestException("Ação de confirmação deve ser CONFIRMAR ou DESISTIR");
        }

        String acaoNormalizada = acao.trim().toUpperCase(Locale.ROOT);
        if (!ACAO_CONFIRMAR.equals(acaoNormalizada) && !ACAO_DESISTIR.equals(acaoNormalizada)) {
            throw new BadRequestException("Ação de confirmação deve ser CONFIRMAR ou DESISTIR");
        }

        return acaoNormalizada;
    }
    
    private AgendamentoDTO converterParaDTO(Agendamento agendamento) {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(agendamento.getId());
        dto.setUsuarioId(agendamento.getUsuario().getId());
        dto.setVagaId(agendamento.getVaga().getId());
        dto.setStatus(agendamento.getStatus().name());
        dto.setObservacoes(agendamento.getObservacoes());
        dto.setDataCriacao(agendamento.getDataCriacao());
        dto.setDataAtualizacao(agendamento.getDataAtualizacao());
        dto.setDataCancelamento(agendamento.getDataCancelamento());
        dto.setMotivoCancelamento(agendamento.getMotivoCancelamento());
        return dto;
    }
}
