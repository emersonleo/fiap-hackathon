package br.com.fiap.postech.meu_postinho.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CnesMockServiceTest {

    private CnesMockService cnesMockService;

    @BeforeEach
    void setUp() {
        cnesMockService = new CnesMockService();
    }

    @Test
    @DisplayName("Deve validar profissional com CPF e CNS válidos")
    void validarProfissionalSuccess() {
        CnesMockService.CneProfissionalResponse response = cnesMockService.validarProfissional("12345678909", "123456789012345");
        
        assertNotNull(response);
        assertTrue(response.isSucesso());
        assertEquals("Profissional verificado com sucesso", response.getMensagem());
        assertEquals("12345678909", response.getCpf());
        assertEquals("123456789012345", response.getCns());
        assertEquals("Agente Comunitário de Saúde", response.getProfissao());
        assertEquals("ATIVO", response.getStatus());
    }

    @Test
    @DisplayName("Deve invalidar profissional com CPF inválido")
    void validarProfissionalCPFInvalido() {
        CnesMockService.CneProfissionalResponse response = cnesMockService.validarProfissional("123456789", "123456789012345");
        
        assertNotNull(response);
        assertFalse(response.isSucesso());
        assertEquals("CPF inválido ou não encontrado no CNES", response.getMensagem());
    }

    @Test
    @DisplayName("Deve invalidar profissional com CNS inválido")
    void validarProfissionalCNSInvalido() {
        CnesMockService.CneProfissionalResponse response = cnesMockService.validarProfissional("12345678909", "123");
        
        assertNotNull(response);
        assertFalse(response.isSucesso());
        assertEquals("CNS inválido", response.getMensagem());
    }

    @Test
    @DisplayName("Deve importar UBS com código de município válido")
    void importarUBSSuccess() {
        List<CnesMockService.CneEstabelecimentoResponse> estabelecimentos = cnesMockService.importarUBS("12345");
        
        assertNotNull(estabelecimentos);
        assertEquals(5, estabelecimentos.size());
        
        var estabelecimento1 = estabelecimentos.get(0);
        assertEquals("3509502", estabelecimento1.getCodigoCNES());
        assertEquals("UBS Parque São Vicente", estabelecimento1.getNome());
        assertEquals("Rua São Vicente, 100", estabelecimento1.getEndereco());
        assertEquals("São Paulo", estabelecimento1.getCidade());
        assertEquals("SP", estabelecimento1.getEstado());
        assertEquals("04561-100", estabelecimento1.getCep());
        assertEquals("(11) 3333-1111", estabelecimento1.getTelefone());
        assertEquals("-23.5505", estabelecimento1.getLatitude());
        assertEquals("-46.6333", estabelecimento1.getLongitude());
    }

    @Test
    @DisplayName("Deve criar CneProfissionalResponse com sucesso")
    void criarCneProfissionalResponseSuccess() {
        CnesMockService.CneProfissionalResponse response = new CnesMockService.CneProfissionalResponse(true, "Sucesso");
        
        assertTrue(response.isSucesso());
        assertEquals("Sucesso", response.getMensagem());
    }

    @Test
    @DisplayName("Deve criar CneProfissionalResponse com falha")
    void criarCneProfissionalResponseFalha() {
        CnesMockService.CneProfissionalResponse response = new CnesMockService.CneProfissionalResponse(false, "Falha");
        
        assertFalse(response.isSucesso());
        assertEquals("Falha", response.getMensagem());
    }

    @Test
    @DisplayName("Deve criar CneEstabelecimentoResponse com todos os parâmetros")
    void criarCneEstabelecimentoResponseSuccess() {
        CnesMockService.CneEstabelecimentoResponse response = new CnesMockService.CneEstabelecimentoResponse(
                "123456", "UBS Teste", "Rua Teste, 123", "São Paulo", 
                "SP", "12345-678", "11999999999", 
                "-23.5505", "-46.6333");
        
        assertEquals("123456", response.getCodigoCNES());
        assertEquals("UBS Teste", response.getNome());
        assertEquals("Rua Teste, 123", response.getEndereco());
        assertEquals("São Paulo", response.getCidade());
        assertEquals("SP", response.getEstado());
        assertEquals("12345-678", response.getCep());
        assertEquals("11999999999", response.getTelefone());
        assertEquals("-23.5505", response.getLatitude());
        assertEquals("-46.6333", response.getLongitude());
    }
}
