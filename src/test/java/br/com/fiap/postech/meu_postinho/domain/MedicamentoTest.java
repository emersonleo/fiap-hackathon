package br.com.fiap.postech.meu_postinho.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicamentoTest {

    private Medicamento medicamento;

    @BeforeEach
    void setUp() {
        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setNome("Paracetamol");
        medicamento.setDescricao("Analgésico e antipirético");
        medicamento.setCategoria("Analgésico");
        medicamento.setPosologia("Comprimido");
        medicamento.setUnidade("500mg");
        medicamento.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar medicamento com dados válidos")
    void criarMedicamentoSuccess() {
        assertNotNull(medicamento);
        assertEquals(1L, medicamento.getId());
        assertEquals("Paracetamol", medicamento.getNome());
        assertEquals("Analgésico e antipirético", medicamento.getDescricao());
        assertEquals("Analgésico", medicamento.getCategoria());
        assertEquals("Comprimido", medicamento.getPosologia());
        assertEquals("500mg", medicamento.getUnidade());
        assertTrue(medicamento.getAtivo());
    }

    @Test
    @DisplayName("Deve testar equals com mesmo objeto")
    void testEqualsSameObject() {
        Medicamento outro = new Medicamento();
        outro.setId(1L);
        outro.setNome("Paracetamol");
        outro.setDescricao("Analgésico e antipirético");

        assertEquals(medicamento, outro);
        assertEquals(medicamento.hashCode(), outro.hashCode());
    }

    @Test
    @DisplayName("Deve testar equals com objeto diferente")
    void testEqualsDifferentObject() {
        Medicamento outro = new Medicamento();
        outro.setId(2L);
        outro.setNome("Ibuprofeno");

        assertNotEquals(medicamento, outro);
    }

    @Test
    @DisplayName("Deve testar equals com null")
    void testEqualsNull() {
        assertNotEquals(medicamento, null);
    }

    @Test
    @DisplayName("Deve testar equals com classe diferente")
    void testEqualsDifferentClass() {
        assertNotEquals(medicamento, "string");
    }

    @Test
    @DisplayName("Deve testar hashCode consistente")
    void testHashCodeConsistency() {
        Medicamento outro = new Medicamento();
        outro.setId(1L);
        outro.setNome("Paracetamol");
        outro.setDescricao("Analgésico e antipirético");

        assertEquals(medicamento.hashCode(), outro.hashCode());
    }

    @Test
    @DisplayName("Deve testar toString")
    void testToString() {
        String result = medicamento.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Paracetamol"));
        assertTrue(result.contains("Analgésico"));
    }

    @Test
    @DisplayName("Deve testar setters e getters")
    void testSettersAndGetters() {
        Medicamento novo = new Medicamento();
        
        novo.setId(999L);
        novo.setNome("Dipirona");
        novo.setDescricao("Anti-inflamatório");
        novo.setCategoria("Analgésico");
        novo.setPosologia("Comprimido");
        novo.setUnidade("100mg");
        novo.setAtivo(false);
        
        assertEquals(999L, novo.getId());
        assertEquals("Dipirona", novo.getNome());
        assertEquals("Anti-inflamatório", novo.getDescricao());
        assertEquals("Analgésico", novo.getCategoria());
        assertEquals("Comprimido", novo.getPosologia());
        assertEquals("100mg", novo.getUnidade());
        assertFalse(novo.getAtivo());
    }

    @Test
    @DisplayName("Deve testar criação e atualização")
    void testCriacaoEAtualizacao() {
        Medicamento novo = new Medicamento();
        novo.setNome("Test");
        
        // Testa se os campos de data são inicialmente nulos
        assertNull(novo.getDataCriacao());
        assertNull(novo.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve testar ativo padrão")
    void testDefaultAtivo() {
        Medicamento novo = new Medicamento();
        
        // Por padrão, Medicamento deve estar ativo
        assertTrue(novo.getAtivo());
    }
}
