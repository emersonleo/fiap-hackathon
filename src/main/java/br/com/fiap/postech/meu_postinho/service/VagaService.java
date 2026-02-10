package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Vaga;
import br.com.fiap.postech.meu_postinho.dto.VagaDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.VagaRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VagaService {
    
    @Autowired
    private VagaRepository vagaRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    public VagaDTO criar(VagaDTO dto) {
        var ubs = ubsRepository.findById(dto.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        if (dto.getData().isBefore(LocalDate.now())) {
            throw new BadRequestException("Não é possível criar vaga para data passada");
        }
        
        Vaga vaga = new Vaga();
        vaga.setUbs(ubs);
        vaga.setData(dto.getData());
        vaga.setHoraInicio(dto.getHoraInicio());
        vaga.setHoraFim(dto.getHoraFim());
        vaga.setEspecialidade(dto.getEspecialidade());
        vaga.setProfissional(dto.getProfissional());
        vaga.setDisponivel(true);
        
        vaga = vagaRepository.save(vaga);
        return converterParaDTO(vaga);
    }
    
    public List<VagaDTO> listarPorUBS(Long ubsId) {
        return vagaRepository.findByUbsId(ubsId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<VagaDTO> listarDisponiveis(Long ubsId, LocalDate data, String especialidade) {
        List<Vaga> vagas = especialidade != null ? 
                vagaRepository.findByUbsIdAndEspecialidade(ubsId, especialidade) :
                vagaRepository.findByUbsIdAndDisponivelTrue(ubsId);
        
        return vagas.stream()
                .filter(v -> data == null || v.getData().equals(data))
                .filter(Vaga::getDisponivel)
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public VagaDTO obterPorId(Long id) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada"));
        return converterParaDTO(vaga);
    }
    
    public VagaDTO atualizar(Long id, VagaDTO dto) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada"));
        
        vaga.setData(dto.getData());
        vaga.setHoraInicio(dto.getHoraInicio());
        vaga.setHoraFim(dto.getHoraFim());
        vaga.setEspecialidade(dto.getEspecialidade());
        vaga.setProfissional(dto.getProfissional());
        
        vaga = vagaRepository.save(vaga);
        return converterParaDTO(vaga);
    }
    
    public void deletar(Long id) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada"));
        
        if (vaga.temAgendamento()) {
            throw new BadRequestException("Não é possível deletar uma vaga com agendamentos");
        }
        
        vagaRepository.delete(vaga);
    }
    
    public VagaDTO converterParaDTO(Vaga vaga) {
        VagaDTO dto = new VagaDTO();
        dto.setId(vaga.getId());
        dto.setUbsId(vaga.getUbs().getId());
        dto.setData(vaga.getData());
        dto.setHoraInicio(vaga.getHoraInicio());
        dto.setHoraFim(vaga.getHoraFim());
        dto.setEspecialidade(vaga.getEspecialidade());
        dto.setProfissional(vaga.getProfissional());
        dto.setDisponivel(vaga.getDisponivel());
        dto.setDataCriacao(vaga.getDataCriacao());
        dto.setDataAtualizacao(vaga.getDataAtualizacao());
        return dto;
    }
}
