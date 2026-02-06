package br.com.fiap.postech.meu_postinho.integration;

import br.com.fiap.postech.meu_postinho.util.CPFValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CnesMockService {
    
    /**
     * Simula a validação de um profissional via CNES
     * Nota: Em produção, isso faria uma chamada real à API CNES
     */
    public CneProfissionalResponse validarProfissional(String cpf, String cns) {
        // Validar CPF
        if (!CPFValidator.isValid(cpf)) {
            return new CneProfissionalResponse(false, "CPF inválido ou não encontrado no CNES");
        }
        
        // Simular validação de CNS
        if (cns == null || cns.length() < 10) {
            return new CneProfissionalResponse(false, "CNS inválido");
        }
        
        // Simular retorno bem-sucedido
        return new CneProfissionalResponse(
                true,
                "Profissional verificado com sucesso",
                cpf,
                cns,
                "Agente Comunitário de Saúde",
                "ATIVO"
        );
    }
    
    /**
     * Simula a importação de UBS via CNES
     */
    public List<CneEstabelecimentoResponse> importarUBS(String codigoMunicipio) {
        List<CneEstabelecimentoResponse> estabelecimentos = new ArrayList<>();
        
        // Mock data de UBS
        estabelecimentos.add(new CneEstabelecimentoResponse(
                "3509502",
                "UBS Parque São Vicente",
                "Rua São Vicente, 100",
                "São Paulo",
                "SP",
                "04561-100",
                "(11) 3333-1111",
                "-23.5505",
                "-46.6333"
        ));
        
        estabelecimentos.add(new CneEstabelecimentoResponse(
                "3509503",
                "UBS Vila Mariana",
                "Av. Paulista, 500",
                "São Paulo",
                "SP",
                "01311-000",
                "(11) 3333-2222",
                "-23.5615",
                "-46.6560"
        ));
        
        estabelecimentos.add(new CneEstabelecimentoResponse(
                "3509504",
                "UBS Centro",
                "Rua 15 de Novembro, 1000",
                "São Paulo",
                "SP",
                "01038-001",
                "(11) 3333-3333",
                "-23.5505",
                "-46.6361"
        ));
        
        estabelecimentos.add(new CneEstabelecimentoResponse(
                "3509505",
                "UBS Zona Leste",
                "Rua Azevedo, 800",
                "São Paulo",
                "SP",
                "03165-000",
                "(11) 3333-4444",
                "-23.5500",
                "-46.4500"
        ));
        
        estabelecimentos.add(new CneEstabelecimentoResponse(
                "3509506",
                "UBS Zona Norte",
                "Avenida Nações Unidas, 2000",
                "São Paulo",
                "SP",
                "02311-100",
                "(11) 3333-5555",
                "-23.4700",
                "-46.5000"
        ));
        
        return estabelecimentos;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CneProfissionalResponse {
        private boolean sucesso;
        private String mensagem;
        private String cpf;
        private String cns;
        private String profissao;
        private String status;
        
        public CneProfissionalResponse(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CneEstabelecimentoResponse {
        private String codigoCNES;
        private String nome;
        private String endereco;
        private String cidade;
        private String estado;
        private String cep;
        private String telefone;
        private String latitude;
        private String longitude;
    }
}
