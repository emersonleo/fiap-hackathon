package br.com.fiap.postech.meu_postinho.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoTest {

    private Agendamento agendamento;
    private Usuario usuario;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");

        vaga = new Vaga();
        vaga.setId(1L);
        vaga.setDisponivel(true);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setUsuario(usuario);
        agendamento.setVaga(vaga);
        agendamento.setStatus(Agendamento.Status.CONFIRMADO);
        agendamento.setObservacoes("Observações do agendamento");
    }

    @Test
    @DisplayName("Deve inicializar com status CONFIRMADO por padrão")
    void shouldInitializeWithConfirmedStatusByDefault() {
        Agendamento newAgendamento = new Agendamento();
        
        assertEquals(Agendamento.Status.CONFIRMADO, newAgendamento.getStatus());
    }

    @Test
    @DisplayName("Deve definir data de criação e atualização antes de persistir")
    void shouldSetCreationAndUpdateDateOnPrePersist() {
        agendamento.onCreate();

        assertNotNull(agendamento.getDataCriacao());
        assertNotNull(agendamento.getDataAtualizacao());
        assertTrue(agendamento.getDataCriacao().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(agendamento.getDataAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve atualizar data de atualização antes de atualizar")
    void shouldSetUpdateDateOnPreUpdate() {
        LocalDateTime initialUpdateTime = LocalDateTime.now().minusHours(1);
        agendamento.setDataAtualizacao(initialUpdateTime);

        agendamento.onUpdate();

        assertNotNull(agendamento.getDataAtualizacao());
        assertTrue(agendamento.getDataAtualizacao().isAfter(initialUpdateTime));
        assertTrue(agendamento.getDataAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve manter data de criação inalterada no preUpdate")
    void shouldKeepCreationDateUnchangedOnPreUpdate() {
        LocalDateTime initialCreationDate = LocalDateTime.now().minusHours(1);
        agendamento.setDataCriacao(initialCreationDate);

        agendamento.onUpdate();

        assertEquals(initialCreationDate, agendamento.getDataCriacao());
    }

    @Test
    @DisplayName("Deve gerar equals e hashCode corretamente baseado no ID")
    void shouldGenerateEqualsAndHashCodeBasedOnId() {
        Agendamento agendamento1 = new Agendamento();
        agendamento1.setId(1L);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(1L);

        Agendamento agendamento3 = new Agendamento();
        agendamento3.setId(2L);

        assertEquals(agendamento1, agendamento2);
        assertEquals(agendamento1.hashCode(), agendamento2.hashCode());
        assertNotEquals(agendamento1, agendamento3);
        assertNotEquals(agendamento1.hashCode(), agendamento3.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString excluindo campos específicos")
    void shouldGenerateToStringExcludingSpecificFields() {
        String toString = agendamento.toString();

        assertTrue(toString.contains("id=" + agendamento.getId()));
        assertTrue(toString.contains("status=" + agendamento.getStatus()));
        assertFalse(toString.contains("paciente="));
        assertFalse(toString.contains("vaga="));
    }

    @Test
    @DisplayName("Deve manter compatibilidade com métodos get/set de usuário")
    void shouldMaintainCompatibilityWithUserGetSetMethods() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setId(2L);

        agendamento.setUsuario(novoUsuario);
        assertEquals(novoUsuario, agendamento.getUsuario());
        assertEquals(novoUsuario, agendamento.getPaciente());
    }

    @Test
    @DisplayName("Deve definir paciente através do setUsuario")
    void shouldSetPacienteThroughSetUser() {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setId(2L);

        agendamento.setUsuario(novoUsuario);
        assertEquals(novoUsuario, agendamento.getPaciente());
    }

    @Test
    @DisplayName("Deve retornar usuário através do getPaciente")
    void shouldReturnUserThroughGetPaciente() {
        assertEquals(usuario, agendamento.getPaciente());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em campos opcionais")
    void shouldAcceptNullValuesInOptionalFields() {
        agendamento.setObservacoes(null);
        agendamento.setDataCancelamento(null);
        agendamento.setMotivoCancelamento(null);

        assertNull(agendamento.getObservacoes());
        assertNull(agendamento.getDataCancelamento());
        assertNull(agendamento.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve testar enum Status")
    void shouldTestStatusEnum() {
        assertEquals("Agendamento confirmado", Agendamento.Status.CONFIRMADO.getDescricao());
        assertEquals("Paciente compareceu", Agendamento.Status.COMPARECEU.getDescricao());
        assertEquals("Paciente não compareceu", Agendamento.Status.NAO_COMPARECEU.getDescricao());
        assertEquals("Agendamento cancelado", Agendamento.Status.CANCELADO.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir mudança de status")
    void shouldAllowStatusChange() {
        agendamento.setStatus(Agendamento.Status.COMPARECEU);
        assertEquals(Agendamento.Status.COMPARECEU, agendamento.getStatus());

        agendamento.setStatus(Agendamento.Status.CANCELADO);
        assertEquals(Agendamento.Status.CANCELADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar e retornar observações")
    void shouldAcceptAndReturnObservacoes() {
        String observacoes = "Paciente com alergia a penicilina";
        agendamento.setObservacoes(observacoes);

        assertEquals(observacoes, agendamento.getObservacoes());
    }

    @Test
    @DisplayName("Deve aceitar e retornar motivo de cancelamento")
    void shouldAcceptAndReturnMotivoCancelamento() {
        String motivo = "Paciente solicitou cancelamento";
        agendamento.setMotivoCancelamento(motivo);

        assertEquals(motivo, agendamento.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve aceitar e retornar data de cancelamento")
    void shouldAcceptAndReturnDataCancelamento() {
        LocalDateTime dataCancelamento = LocalDateTime.now();
        agendamento.setDataCancelamento(dataCancelamento);

        assertEquals(dataCancelamento, agendamento.getDataCancelamento());
    }
}
