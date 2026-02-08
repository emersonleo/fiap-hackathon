package br.com.fiap.postech.meu_postinho.service;

import br.com.fiap.postech.meu_postinho.domain.Usuario;
import br.com.fiap.postech.meu_postinho.dto.LoginDTO;
import br.com.fiap.postech.meu_postinho.dto.AuthResponse;
import br.com.fiap.postech.meu_postinho.dto.UsuarioDTO;
import br.com.fiap.postech.meu_postinho.exception.BadRequestException;
import br.com.fiap.postech.meu_postinho.exception.ResourceNotFoundException;
import br.com.fiap.postech.meu_postinho.repository.UsuarioRepository;
import br.com.fiap.postech.meu_postinho.repository.UBSRepository;
import br.com.fiap.postech.meu_postinho.config.JwtTokenProvider;
import br.com.fiap.postech.meu_postinho.util.CPFValidator;
import br.com.fiap.postech.meu_postinho.util.PhoneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UBSRepository ubsRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponse login(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );
            
            Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            
            String role = usuario.getRoles().isEmpty() ? "ROLE_MORADOR" : usuario.getRoles().iterator().next();
            
            String token = tokenProvider.generateToken(
                    usuario.getEmail(),
                    role,
                    usuario.getId(),
                    usuario.getCpf(),
                    usuario.getNome(),
                    usuario.getUbs().getId()
            );
            
            return new AuthResponse(token, usuario.getId(), usuario.getCpf(), 
                    usuario.getNome(), usuario.getEmail(), role, usuario.getUbs().getId());
        } catch (Exception e) {
            throw new BadRequestException("Email ou senha incorretos");
        }
    }
    
    public AuthResponse registrarMorador(UsuarioDTO usuarioDTO) {
        // Validar CPF
        if (!CPFValidator.isValid(usuarioDTO.getCpf())) {
            throw new BadRequestException("CPF inválido");
        }
        
        // Validar se CPF já existe
        if (usuarioRepository.existsByCpf(usuarioDTO.getCpf())) {
            throw new BadRequestException("CPF já cadastrado");
        }
        
        // Validar telefone
        if (!PhoneValidator.isValid(usuarioDTO.getTelefone())) {
            throw new BadRequestException("Telefone inválido");
        }
        
        if (usuarioRepository.existsByTelefone(usuarioDTO.getTelefone())) {
            throw new BadRequestException("Telefone já registrado");
        }
        
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BadRequestException("Email já registrado");
        }
        
        // Validar UBS
        var ubs = ubsRepository.findById(usuarioDTO.getUbsId())
                .orElseThrow(() -> new ResourceNotFoundException("UBS não encontrada"));
        
        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setNome(usuarioDTO.getNome());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setDataNascimento(usuarioDTO.getDataNascimento());
        usuario.setEndereco(usuarioDTO.getEndereco());
        usuario.setCep(usuarioDTO.getCep());
        usuario.setUbs(ubs);
        usuario.setAtivo(true);
        usuario.getRoles().add("ROLE_MORADOR");
        
        usuario = usuarioRepository.save(usuario);
        
        String token = tokenProvider.generateToken(
                usuario.getEmail(),
                "ROLE_MORADOR",
                usuario.getId(),
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getUbs().getId()
        );
        
        return new AuthResponse(token, usuario.getId(), usuario.getCpf(),
                usuario.getNome(), usuario.getEmail(), "ROLE_MORADOR", usuario.getUbs().getId());
    }
}
