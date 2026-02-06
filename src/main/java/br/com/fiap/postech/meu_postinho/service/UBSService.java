package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.UBS;
import br.com.fiap.postech.meu_postinho.dto.UBSDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService.CneEstabelecimentoResponse;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UBSService {
    
    @Autowired
    private UBSRepository ubsRepository;
    
    @Autowired
    private CnesMockService cnesMockService;
    
    public List<UBSDTO> listarTodas() {
        return ubsRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    public UBSDTO obterPorId(Long id) {
        UBS ubs = ubsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        return converterParaDTO(ubs);
    }
    
    public UBSDTO criar(UBSDTO ubsDTO) {
        if (ubsRepository.findByNome(ubsDTO.getNome()).isPresent()) {
            throw new BadRequestException("UBS com este nome já existe");
        }
        
        UBS ubs = new UBS();
        ubs.setNome(ubsDTO.getNome());
        ubs.setCodigoCNES(ubsDTO.getCodigoCNES());
        ubs.setEndereco(ubsDTO.getEndereco());
        ubs.setCep(ubsDTO.getCep());
        ubs.setTelefone(ubsDTO.getTelefone());
        ubs.setCidade(ubsDTO.getCidade());
        ubs.setEstado(ubsDTO.getEstado());
        ubs.setLatitude(ubsDTO.getLatitude());
        ubs.setLongitude(ubsDTO.getLongitude());
        ubs.setAtiva(true);
        
        ubs = ubsRepository.save(ubs);
        return converterParaDTO(ubs);
    }
    
    public UBSDTO atualizar(Long id, UBSDTO ubsDTO) {
        UBS ubs = ubsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        ubs.setNome(ubsDTO.getNome());
        ubs.setCodigoCNES(ubsDTO.getCodigoCNES());
        ubs.setEndereco(ubsDTO.getEndereco());
        ubs.setCep(ubsDTO.getCep());
        ubs.setTelefone(ubsDTO.getTelefone());
        ubs.setCidade(ubsDTO.getCidade());
        ubs.setEstado(ubsDTO.getEstado());
        ubs.setLatitude(ubsDTO.getLatitude());
        ubs.setLongitude(ubsDTO.getLongitude());
        ubs.setAtiva(ubsDTO.getAtiva());
        
        ubs = ubsRepository.save(ubs);
        return converterParaDTO(ubs);
    }
    
    public void deletar(Long id) {
        UBS ubs = ubsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        ubsRepository.delete(ubs);
    }
    
    public int importarUBSviaCNES(String codigoMunicipio) {
        List<CneEstabelecimentoResponse> estabelecimentos = 
                cnesMockService.importarUBS(codigoMunicipio);
        
        int contador = 0;
        for (CneEstabelecimentoResponse estab : estabelecimentos) {
            if (ubsRepository.findByCodigoCNES(estab.getCodigoCNES()).isEmpty()) {
                UBS ubs = new UBS();
                ubs.setCodigoCNES(estab.getCodigoCNES());
                ubs.setNome(estab.getNome());
                ubs.setEndereco(estab.getEndereco());
                ubs.setCidade(estab.getCidade());
                ubs.setEstado(estab.getEstado());
                ubs.setCep(estab.getCep());
                ubs.setTelefone(estab.getTelefone());
                
                try {
                    ubs.setLatitude(Double.parseDouble(estab.getLatitude()));
                    ubs.setLongitude(Double.parseDouble(estab.getLongitude()));
                } catch (NumberFormatException e) {
                    // Ignorar se coordenadas inválidas
                }
                
                ubs.setAtiva(true);
                ubsRepository.save(ubs);
                contador++;
            }
        }
        
        return contador;
    }
    
    private UBSDTO converterParaDTO(UBS ubs) {
        UBSDTO dto = new UBSDTO();
        dto.setId(ubs.getId());
        dto.setNome(ubs.getNome());
        dto.setCodigoCNES(ubs.getCodigoCNES());
        dto.setEndereco(ubs.getEndereco());
        dto.setCep(ubs.getCep());
        dto.setTelefone(ubs.getTelefone());
        dto.setCidade(ubs.getCidade());
        dto.setEstado(ubs.getEstado());
        dto.setLatitude(ubs.getLatitude());
        dto.setLongitude(ubs.getLongitude());
        dto.setAtiva(ubs.getAtiva());
        dto.setDataCriacao(ubs.getDataCriacao());
        dto.setDataAtualizacao(ubs.getDataAtualizacao());
        return dto;
    }
}
