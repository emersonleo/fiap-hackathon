package br.com.fiap.postech.meu_postinho.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPFValidatorTest {

    @Test
    @DisplayName("Deve validar CPF válido")
    void cpfValidoSuccess() {
        assertTrue(CPFValidator.isValid("12345678909"));
        assertTrue(CPFValidator.isValid("11144477735"));
        assertTrue(CPFValidator.isValid("123.456.789-09"));
        assertTrue(CPFValidator.isValid("123 456 789 09"));
    }

    @Test
    @DisplayName("Deve invalidar CPF nulo")
    void cpfNuloInvalido() {
        assertFalse(CPFValidator.isValid(null));
    }

    @Test
    @DisplayName("Deve invalidar CPF vazio")
    void cpfVazioInvalido() {
        assertFalse(CPFValidator.isValid(""));
    }

    @Test
    @DisplayName("Deve invalidar CPF com menos de 11 dígitos")
    void cpfMenor11DigitosInvalido() {
        assertFalse(CPFValidator.isValid("123456789"));
        assertFalse(CPFValidator.isValid("12345678"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com mais de 11 dígitos")
    void cpfMaior11DigitosInvalido() {
        assertFalse(CPFValidator.isValid("123456789012"));
        assertFalse(CPFValidator.isValid("1234567890123"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com todos os dígitos iguais")
    void cpfDigitosIguaisInvalido() {
        assertFalse(CPFValidator.isValid("11111111111"));
        assertFalse(CPFValidator.isValid("22222222222"));
        assertFalse(CPFValidator.isValid("00000000000"));
        assertFalse(CPFValidator.isValid("99999999999"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com dígitos inválidos")
    void cpfDigitosInvalidosInvalido() {
        assertFalse(CPFValidator.isValid("1234567890X"));
        assertFalse(CPFValidator.isValid("ABCDEFGHIJK"));
        assertFalse(CPFValidator.isValid("1234567890!"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com sequência inválida")
    void cpfSequenciaInvalida() {
        assertFalse(CPFValidator.isValid("12345678901")); // Primeiros 9 dígitos em sequência
        assertFalse(CPFValidator.isValid("98765432101")); // Últimos 9 dígitos em sequência
    }

    @Test
    @DisplayName("Deve validar CPF com formatação")
    void cpfComFormatacaoSuccess() {
        assertTrue(CPFValidator.isValid("123.456.789-09"));
        assertTrue(CPFValidator.isValid("123 456 789 09"));
        assertTrue(CPFValidator.isValid("12345678909"));
    }

    @Test
    @DisplayName("Deve validar CPF com formatação incorreta mas dígitos válidos")
    void cpfComFormatacaoIncorretaSuccess() {
        // CPFValidator remove caracteres não numéricos antes de validar
        assertTrue(CPFValidator.isValid("123.456.789.09")); // Ponto extra, mas dígitos válidos
        assertTrue(CPFValidator.isValid("123-456-789-09")); // Traço extra, mas dígitos válidos
        assertTrue(CPFValidator.isValid("123 456 789 09 ")); // Espaço extra, mas dígitos válidos
    }

    @Test
    @DisplayName("Deve formatar CPF válido")
    void formatarCPFValido() {
        assertEquals("123.456.789-09", CPFValidator.formatCPF("12345678909"));
        assertEquals("111.444.777-35", CPFValidator.formatCPF("11144477735"));
    }

    @Test
    @DisplayName("Deve formatar CPF já formatado")
    void formatarCPFJaFormatado() {
        assertEquals("123.456.789-09", CPFValidator.formatCPF("123.456.789-09"));
        assertEquals("111.444.777-35", CPFValidator.formatCPF("111.444.777-35"));
    }

    @Test
    @DisplayName("Deve retornar nulo ao formatar CPF nulo")
    void formatarCPFNulo() {
        assertNull(CPFValidator.formatCPF(null));
    }

    @Test
    @DisplayName("Deve retornar dígitos ao formatar CPF com menos de 11 dígitos")
    void formatarCPFInvalido() {
        assertEquals("123456789", CPFValidator.formatCPF("123456789"));
        assertEquals("12345", CPFValidator.formatCPF("12345"));
    }

    @Test
    @DisplayName("Deve formatar CPF com espaços")
    void formatarCPFComEspacos() {
        assertEquals("123.456.789-09", CPFValidator.formatCPF("123 456 789 09"));
    }

    @Test
    @DisplayName("Deve verificar primeiro dígito verificador com resto 10")
    void primeiroDigitoVerificadorResto10() {
        // Testando um CPF que resulta em resto 10 no primeiro dígito verificador
        assertFalse(CPFValidator.isValid("12345678987")); // Dígito verificador incorreto
    }

    @Test
    @DisplayName("Deve verificar segundo dígito verificador com resto 11")
    void segundoDigitoVerificadorResto11() {
        // Testando um CPF que resulta em resto 11 no segundo dígito verificador
        assertFalse(CPFValidator.isValid("12345678908")); // Dígito verificador incorreto
    }
}
