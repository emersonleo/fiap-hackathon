package br.com.fiap.postech.meu_postinho.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;
    private UBS ubs;

    @BeforeEach
    void setUp() {
        ubs = new UBS();
        ubs.setId(1L);
        ubs.setNome("UBS Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCpf("12345678901");
        usuario.setNome("Test User");
        usuario.setTelefone("11999999999");
        usuario.setEmail("test@example.com");
        usuario.setSenha("password123");
        usuario.setDataNascimento("01/01/1990");
        usuario.setEndereco("Rua Teste, 123");
        usuario.setCep("01234567");
        usuario.setUbs(ubs);
        usuario.setAtivo(true);
    }

    @Test
    @DisplayName("Deve inicializar roles vazias")
    void shouldInitializeEmptyRoles() {
        Usuario newUsuario = new Usuario();
        assertNotNull(newUsuario.getRoles());
        assertTrue(newUsuario.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Deve definir data de criação e atualização antes de persistir")
    void shouldSetCreationAndUpdateDateOnPrePersist() {
        usuario.onCreate();

        assertNotNull(usuario.getDataCriacao());
        assertNotNull(usuario.getDataAtualizacao());
        assertTrue(usuario.getDataCriacao().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(usuario.getDataAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve adicionar ROLE_MORADOR quando roles estiver vazia no prePersist")
    void shouldAddMoradorRoleOnPrePersistWhenRolesEmpty() {
        usuario.onCreate();

        assertFalse(usuario.getRoles().isEmpty());
        assertTrue(usuario.getRoles().contains("ROLE_MORADOR"));
    }

    @Test
    @DisplayName("Não deve adicionar ROLE_MORADOR quando roles já tiver conteúdo no prePersist")
    void shouldNotAddMoradorRoleOnPrePersistWhenRolesNotEmpty() {
        Set<String> existingRoles = new HashSet<>();
        existingRoles.add("ROLE_AGENTE");
        usuario.setRoles(existingRoles);

        usuario.onCreate();

        assertEquals(1, usuario.getRoles().size());
        assertTrue(usuario.getRoles().contains("ROLE_AGENTE"));
        assertFalse(usuario.getRoles().contains("ROLE_MORADOR"));
    }

    @Test
    @DisplayName("Deve atualizar data de atualização antes de atualizar")
    void shouldSetUpdateDateOnPreUpdate() {
        LocalDateTime initialUpdateTime = LocalDateTime.now().minusHours(1);
        usuario.setDataAtualizacao(initialUpdateTime);

        usuario.onUpdate();

        assertNotNull(usuario.getDataAtualizacao());
        assertTrue(usuario.getDataAtualizacao().isAfter(initialUpdateTime));
        assertTrue(usuario.getDataAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve manter data de criação inalterada no preUpdate")
    void shouldKeepCreationDateUnchangedOnPreUpdate() {
        LocalDateTime initialCreationDate = LocalDateTime.now().minusHours(1);
        usuario.setDataCriacao(initialCreationDate);

        usuario.onUpdate();

        assertEquals(initialCreationDate, usuario.getDataCriacao());
    }

    @Test
    @DisplayName("Deve gerar equals e hashCode corretamente baseado no ID")
    void shouldGenerateEqualsAndHashCodeBasedOnId() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);

        Usuario usuario2 = new Usuario();
        usuario2.setId(1L);

        Usuario usuario3 = new Usuario();
        usuario3.setId(2L);

        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
        assertNotEquals(usuario1, usuario3);
        assertNotEquals(usuario1.hashCode(), usuario3.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString excluindo campos específicos")
    void shouldGenerateToStringExcludingSpecificFields() {
        String toString = usuario.toString();

        assertTrue(toString.contains("id=" + usuario.getId()));
        assertTrue(toString.contains("cpf=" + usuario.getCpf()));
        assertTrue(toString.contains("nome=" + usuario.getNome()));
        assertFalse(toString.contains("ubs="));
        assertFalse(toString.contains("solicitacoes="));
        assertFalse(toString.contains("agendamentos="));
    }

    @Test
    @DisplayName("Deve adicionar e remover roles corretamente")
    void shouldAddAndRemoveRolesCorrectly() {
        Set<String> roles = new HashSet<>();
        usuario.setRoles(roles);

        usuario.getRoles().add("ROLE_MORADOR");
        assertTrue(usuario.getRoles().contains("ROLE_MORADOR"));
        assertEquals(1, usuario.getRoles().size());

        usuario.getRoles().add("ROLE_AGENTE");
        assertEquals(2, usuario.getRoles().size());
        assertTrue(usuario.getRoles().contains("ROLE_MORADOR"));
        assertTrue(usuario.getRoles().contains("ROLE_AGENTE"));

        usuario.getRoles().remove("ROLE_MORADOR");
        assertEquals(1, usuario.getRoles().size());
        assertFalse(usuario.getRoles().contains("ROLE_MORADOR"));
        assertTrue(usuario.getRoles().contains("ROLE_AGENTE"));
    }

    @Test
    @DisplayName("Deve inicializar coleções vazias")
    void shouldInitializeEmptyCollections() {
        Usuario newUsuario = new Usuario();
        
        assertNotNull(newUsuario.getSolicitacoes());
        assertTrue(newUsuario.getSolicitacoes().isEmpty());
        
        assertNotNull(newUsuario.getAgendamentos());
        assertTrue(newUsuario.getAgendamentos().isEmpty());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em campos opcionais")
    void shouldAcceptNullValuesInOptionalFields() {
        usuario.setDataNascimento(null);
        usuario.setEmail(null);

        assertNull(usuario.getDataNascimento());
        assertNull(usuario.getEmail());
    }

    @Test
    @DisplayName("Deve manter consistência nos relacionamentos")
    void shouldMaintainRelationshipConsistency() {
        SolicitacaoMedicamento solicitacao = new SolicitacaoMedicamento();
        solicitacao.setId(1L);
        solicitacao.setUsuario(usuario);

        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setUsuario(usuario);

        usuario.getSolicitacoes().add(solicitacao);
        usuario.getAgendamentos().add(agendamento);

        assertEquals(1, usuario.getSolicitacoes().size());
        assertEquals(1, usuario.getAgendamentos().size());
        assertTrue(usuario.getSolicitacoes().contains(solicitacao));
        assertTrue(usuario.getAgendamentos().contains(agendamento));
    }
}
