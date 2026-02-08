package br.com.fiap.postech.meu_postinho.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ConversorStatus implements AttributeConverter<Agendamento.Status, String> {
    @Override
    public String convertToDatabaseColumn(Agendamento.Status atributo) {
        if (atributo == null) return null;
        switch (atributo) {
            case CONFIRMADO:
                return "AGENDADO";
            case COMPARECEU:
                return "COMPARECEU";
            case NAO_COMPARECEU:
                return "FALTOU";
            case CANCELADO:
                return "CANCELADO";
            default:
                throw new IllegalArgumentException("Status desconhecido: " + atributo);
        }
    }

    @Override
    public Agendamento.Status convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        switch (dbData) {
            case "AGENDADO":
                return Agendamento.Status.CONFIRMADO;
            case "COMPARECEU":
                return Agendamento.Status.COMPARECEU;
            case "FALTOU":
                return Agendamento.Status.NAO_COMPARECEU;
            case "CANCELADO":
                return Agendamento.Status.CANCELADO;
            default:
                throw new IllegalArgumentException("Valor de status no banco desconhecido: " + dbData);
        }
    }

    // Métodos auxiliares em português que delegam aos métodos JPA
    public String converteParaColunaBanco(Agendamento.Status atributo) {
        return convertToDatabaseColumn(atributo);
    }

    public Agendamento.Status converteParaAtributoEntidade(String dadosBanco) {
        return convertToEntityAttribute(dadosBanco);
    }

}
