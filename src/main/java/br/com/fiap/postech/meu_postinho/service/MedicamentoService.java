package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Medicamento;
import br.com.fiap.postech.meu_postinho.dto.MedicamentoDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    public List<MedicamentoDTO> listarTodos() {
        return medicamentoRepository.findByAtivoTrue().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public List<MedicamentoDTO> listarPorCategoria(String categoria) {
        return medicamentoRepository.findByCategoria(categoria).stream()
                .filter(Medicamento::getAtivo)
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public MedicamentoDTO obterPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        return converterParaDTO(medicamento);
    }
    
    public MedicamentoDTO criar(MedicamentoDTO medicamentoDTO) {
        if (medicamentoRepository.findByNome(medicamentoDTO.getNome()).isPresent()) {
            throw new BadRequestException("Medicamento com este nome já existe");
        }
        
        Medicamento medicamento = new Medicamento();
        medicamento.setNome(medicamentoDTO.getNome());
        medicamento.setDescricao(medicamentoDTO.getDescricao());
        medicamento.setCategoria(medicamentoDTO.getCategoria());
        medicamento.setPosologia(medicamentoDTO.getPosologia());
        medicamento.setUnidade(medicamentoDTO.getUnidade());
        medicamento.setCodigoCATMAT(medicamentoDTO.getCodigoCATMAT());
        medicamento.setAtivo(true);
        
        medicamento = medicamentoRepository.save(medicamento);
        return converterParaDTO(medicamento);
    }
    
    public MedicamentoDTO atualizar(Long id, MedicamentoDTO medicamentoDTO) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        
        medicamento.setNome(medicamentoDTO.getNome());
        medicamento.setDescricao(medicamentoDTO.getDescricao());
        medicamento.setCategoria(medicamentoDTO.getCategoria());
        medicamento.setPosologia(medicamentoDTO.getPosologia());
        medicamento.setUnidade(medicamentoDTO.getUnidade());
        medicamento.setCodigoCATMAT(medicamentoDTO.getCodigoCATMAT());
        medicamento.setAtivo(medicamentoDTO.getAtivo());
        
        medicamento = medicamentoRepository.save(medicamento);
        return converterParaDTO(medicamento);
    }
    
    public void deletar(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        medicamento.setAtivo(false);
        medicamentoRepository.save(medicamento);
    }
    
    private MedicamentoDTO converterParaDTO(Medicamento medicamento) {
        MedicamentoDTO dto = new MedicamentoDTO();
        dto.setId(medicamento.getId());
        dto.setNome(medicamento.getNome());
        dto.setDescricao(medicamento.getDescricao());
        dto.setCategoria(medicamento.getCategoria());
        dto.setPosologia(medicamento.getPosologia());
        dto.setUnidade(medicamento.getUnidade());
        dto.setCodigoCATMAT(medicamento.getCodigoCATMAT());
        dto.setAtivo(medicamento.getAtivo());
        dto.setDataCriacao(medicamento.getDataCriacao());
        dto.setDataAtualizacao(medicamento.getDataAtualizacao());
        return dto;
    }
}
