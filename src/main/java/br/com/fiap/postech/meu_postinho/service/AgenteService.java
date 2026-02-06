package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Agente;
import br.com.fiap.postech.meu_postinho.domain.Agente.StatusCNES;
import br.com.fiap.postech.meu_postinho.domain.Agente.TipoAgente;
import br.com.fiap.postech.meu_postinho.dto.AgenteDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService;
import br.com.fiap.postech.meu_postinho.integration.CnesMockService.CneProfissionalResponse;
import br.com.fiap.postech.meu_postinho.repository.AgenteRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import br.com.fiap.postech.meu_postinho.util.CPFValidator;
import br.com.fiap.postech.meu_postinho.util.PhoneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgenteService {
    
    @Autowired
    private AgenteRepository agenteRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    @Autowired
    private CnesMockService cnesMockService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AgenteDTO registrarAgente(AgenteDTO agenteDTO) {
        // Validar CPF
        if (!CPFValidator.isValid(agenteDTO.getCpf())) {
            throw new BadRequestException("CPF inválido");
        }
        
        // Validar CNS
        if (agenteRepository.existsByCns(agenteDTO.getCns())) {
            throw new BadRequestException("CNS já cadastrado");
        }
        
        // Validar telefone
        if (!PhoneValidator.isValid(agenteDTO.getTelefone())) {
            throw new BadRequestException("Telefone inválido");
        }
        
        // Validar UBS
        var ubs = ubsRepository.findById(agenteDTO.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        // Validar via CNES
        CneProfissionalResponse cnesResponse = cnesMockService.validarProfissional(
                agenteDTO.getCpf(),
                agenteDTO.getCns()
        );
        
        if (!cnesResponse.isSucesso()) {
            throw new BadRequestException("Falha na validação CNES: " + cnesResponse.getMensagem());
        }
        
        // Criar novo agente
        Agente agente = new Agente(
                agenteDTO.getCpf(),
                agenteDTO.getNome(),
                agenteDTO.getTelefone(),
                agenteDTO.getEmail(),
                passwordEncoder.encode(agenteDTO.getSenha()),
                agenteDTO.getDataNascimento(),
                agenteDTO.getEndereco(),
                agenteDTO.getCep(),
                ubs,
                agenteDTO.getCns(),
                TipoAgente.valueOf(agenteDTO.getTipo().toUpperCase()),
                StatusCNES.VERIFICADO
        );
        
        agente.setDataVerificacaoCnes(LocalDateTime.now());
        agente.setAtivo(true);
        
        agente = agenteRepository.save(agente);
        
        return converterParaDTO(agente);
    }
    
    public AgenteDTO obterAgentePorId(Long id) {
        Agente agente = agenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente não encontrado"));
        return converterParaDTO(agente);
    }
    
    private AgenteDTO converterParaDTO(Agente agente) {
        AgenteDTO dto = new AgenteDTO();
        dto.setId(agente.getId());
        dto.setCpf(agente.getCpf());
        dto.setNome(agente.getNome());
        dto.setTelefone(agente.getTelefone());
        dto.setEmail(agente.getEmail());
        dto.setDataNascimento(agente.getDataNascimento());
        dto.setEndereco(agente.getEndereco());
        dto.setCep(agente.getCep());
        dto.setUbsId(agente.getUbs().getId());
        dto.setCns(agente.getCns());
        dto.setTipo(agente.getTipo().name());
        dto.setStatusCnes(agente.getStatusCnes().name());
        dto.setDataVerificacaoCnes(agente.getDataVerificacaoCnes());
        dto.setAtivo(agente.getAtivo());
        dto.setDataCriacao(agente.getDataCriacao());
        dto.setDataAtualizacao(agente.getDataAtualizacao());
        return dto;
    }
}
