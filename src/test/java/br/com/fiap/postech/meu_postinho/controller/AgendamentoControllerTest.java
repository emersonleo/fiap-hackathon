package br.com.fiap.postech.meu_postinho.controller;

import br.com.fiap.postech.meu_postinho.dto.AgendamentoDTO;
import br.com.fiap.postech.meu_postinho.service.AgendamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AgendamentoControllerTest {

    @Mock
    private AgendamentoService agendamentoService;

    @InjectMocks
    private AgendamentoController agendamentoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(agendamentoController).build();
    }

    @Test
    @DisplayName("Deve confirmar agendamento com sucesso")
    void confirmarOuDesistirConfirmarSuccess() throws Exception {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(1L);
        dto.setStatus("CONFIRMADO");

        when(agendamentoService.confirmarOuDesistir(1L, "CONFIRMAR")).thenReturn(dto);

        mockMvc.perform(put("/api/agendamentos/1/confirmacao")
                        .param("acao", "CONFIRMAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));

        verify(agendamentoService).confirmarOuDesistir(1L, "CONFIRMAR");
    }

    @Test
    @DisplayName("Deve desistir do agendamento com sucesso")
    void confirmarOuDesistirDesistirSuccess() throws Exception {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(1L);
        dto.setStatus("CANCELADO");
        dto.setMotivoCancelamento("Desistência informada pelo paciente");

        when(agendamentoService.confirmarOuDesistir(1L, "DESISTIR")).thenReturn(dto);

        mockMvc.perform(put("/api/agendamentos/1/confirmacao")
                        .param("acao", "DESISTIR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELADO"))
                .andExpect(jsonPath("$.motivoCancelamento").value("Desistência informada pelo paciente"));

        verify(agendamentoService).confirmarOuDesistir(1L, "DESISTIR");
    }

    @Test
    @DisplayName("Deve remanejar agendamento com sucesso")
    void remanejarSuccess() throws Exception {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(1L);
        dto.setUsuarioId(2L);
        dto.setStatus("CONFIRMADO");

        when(agendamentoService.remanejar(1L, 2L)).thenReturn(dto);

        mockMvc.perform(post("/api/agendamentos/1/remanejamento")
                        .param("novoUsuarioId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(2))
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));

        verify(agendamentoService).remanejar(1L, 2L);
    }
}
