package br.com.fiap.postech.meu_postinho.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    private ResourceNotFoundException resourceNotFoundException;
    private BadRequestException badRequestException;
    private BadCredentialsException badCredentialsException;
    private MethodArgumentNotValidException methodArgumentNotValidException;
    private Exception generalException;

    @BeforeEach
    void setUp() {
        resourceNotFoundException = new ResourceNotFoundException("Recurso não encontrado");
        badRequestException = new BadRequestException("Dados inválidos");
        badCredentialsException = new BadCredentialsException("Credenciais inválidas");
        generalException = new Exception("Erro geral");

        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    @DisplayName("Deve retornar 404 quando ResourceNotFoundException é lançada")
    void handleResourceNotFoundTest() {
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceNotFound(
                resourceNotFoundException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso não encontrado", response.getBody().getError());
        assertEquals("Recurso não encontrado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve retornar 400 quando BadRequestException é lançada")
    void handleBadRequestTest() {
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequest(
                badRequestException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Requisição inválida", response.getBody().getError());
        assertEquals("Dados inválidos", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve retornar 401 quando BadCredentialsException é lançada")
    void handleBadCredentialsTest() {
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadCredentials(
                badCredentialsException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Credenciais inválidas", response.getBody().getError());
        assertEquals("Email ou senha incorretos", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve retornar 400 com erros de validação para MethodArgumentNotValidException")
    void handleValidationExceptionsTest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Deve ser válido");
        List<org.springframework.validation.ObjectError> errors = new ArrayList<>();
        errors.add(fieldError);

        when(bindingResult.getAllErrors()).thenReturn(errors);

        methodArgumentNotValidException = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(
                methodArgumentNotValidException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro de validação", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("field"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve retornar 500 para exceção geral")
    void handleGlobalExceptionTest() {
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(
                generalException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Erro interno do servidor", response.getBody().getError());
        assertEquals("Erro geral", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
