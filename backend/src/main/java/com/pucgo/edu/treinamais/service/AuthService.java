package com.pucgo.edu.treinamais.service;

import com.pucgo.edu.treinamais.dto.request.LoginRequest;
import com.pucgo.edu.treinamais.dto.request.RegisterRequest;
import com.pucgo.edu.treinamais.dto.response.AuthResponse;
import com.pucgo.edu.treinamais.dto.response.MessageResponse;
import com.pucgo.edu.treinamais.model.*;
import com.pucgo.edu.treinamais.repository.AlunoRepository;
import com.pucgo.edu.treinamais.repository.ProfessorRepository;
import com.pucgo.edu.treinamais.repository.SessaoRepository;
import com.pucgo.edu.treinamais.repository.UsuarioRepository;
import com.pucgo.edu.treinamais.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final SessaoRepository sessaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UsuarioRepository usuarioRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            SessaoRepository sessaoRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.sessaoRepository = sessaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public MessageResponse cadastrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Erro: E-mail já cadastrado!");
        }

        TipoUsuario tipo = request.getTipo() != null ? request.getTipo() : TipoUsuario.ALUNO;

        if (tipo == TipoUsuario.PROFESSOR) {
            if (request.getCref() == null || request.getCref().trim().isEmpty()) {
                throw new IllegalArgumentException("Erro: O CREF é obrigatório para cadastro de professores.");
            }
            if (professorRepository.existsByCref(request.getCref().trim())) {
                throw new IllegalArgumentException("Erro: CREF já cadastrado!");
            }
        }

        if (tipo == TipoUsuario.ALUNO) {
            if (request.getCpf() != null && !request.getCpf().trim().isEmpty()) {
                if (alunoRepository.existsByCpf(request.getCpf().trim())) {
                    throw new IllegalArgumentException("Erro: CPF já cadastrado!");
                }
            }
            if (request.getProfessorId() != null) {
                if (!professorRepository.existsById(request.getProfessorId())) {
                    throw new IllegalArgumentException("Erro: Professor informado não existe.");
                }
            }
        }

        Usuario usuario = new Usuario(
                request.getNome().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getSenha()),
                tipo
        );
        usuario = usuarioRepository.save(usuario);

        if (tipo == TipoUsuario.PROFESSOR) {
            Professor professor = new Professor(usuario, request.getCref().trim(), request.getTelefone());
            professorRepository.save(professor);
        } else if (tipo == TipoUsuario.ALUNO) {
            Professor professor = null;
            if (request.getProfessorId() != null) {
                professor = professorRepository.findById(request.getProfessorId()).orElse(null);
            }
            Aluno aluno = new Aluno(
                    usuario,
                    professor,
                    request.getCpf() != null ? request.getCpf().trim() : null,
                    request.getDataNascimento(),
                    request.getTelefone()
            );
            alunoRepository.save(aluno);
        }

        return new MessageResponse("Usuário cadastrado com sucesso!");
    }

    @Transactional
    public AuthResponse autenticar(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getSenha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getStatus() != StatusUsuario.ATIVO) {
            throw new IllegalArgumentException("Conta de usuário inativa ou bloqueada.");
        }

        String tokenHash = tokenProvider.hashToken(jwt);
        LocalDateTime expiraEm = LocalDateTime.now().plusSeconds(tokenProvider.getJwtExpirationMs() / 1000);
        Sessao sessao = new Sessao(usuario, tokenHash, expiraEm);
        sessaoRepository.save(sessao);

        long expiraEmMs = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();

        return new AuthResponse(
                jwt,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo().name(),
                usuario.getStatus().name(),
                expiraEmMs
        );
    }

    @Transactional
    public AuthResponse renovar(String rawToken) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Token não fornecido para renovação.");
        }

        String token = rawToken.startsWith("Bearer ") ? rawToken.substring(7).trim() : rawToken.trim();

        if (!tokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        String tokenHash = tokenProvider.hashToken(token);
        Sessao sessao = sessaoRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada ou já expirada."));

        if (!sessao.isValida()) {
            throw new IllegalArgumentException("Sessão revogada ou expirada. Faça login novamente.");
        }

        Usuario usuario = sessao.getUsuario();
        if (usuario.getStatus() != StatusUsuario.ATIVO) {
            throw new IllegalArgumentException("Conta de usuário inativa ou bloqueada.");
        }

        sessao.revogar();
        sessaoRepository.save(sessao);

        String novoJwt = tokenProvider.generateTokenFromUsername(usuario.getEmail());
        String novoTokenHash = tokenProvider.hashToken(novoJwt);
        LocalDateTime novaExpiracao = LocalDateTime.now().plusSeconds(tokenProvider.getJwtExpirationMs() / 1000);

        Sessao novaSessao = new Sessao(usuario, novoTokenHash, novaExpiracao);
        sessaoRepository.save(novaSessao);

        long expiraEmMs = System.currentTimeMillis() + tokenProvider.getJwtExpirationMs();

        return new AuthResponse(
                novoJwt,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo().name(),
                usuario.getStatus().name(),
                expiraEmMs
        );
    }

    @Transactional
    public MessageResponse logout(String rawToken) {
        if (rawToken != null && !rawToken.trim().isEmpty()) {
            String token = rawToken.startsWith("Bearer ") ? rawToken.substring(7).trim() : rawToken.trim();
            try {
                String tokenHash = tokenProvider.hashToken(token);
                sessaoRepository.findByTokenHash(tokenHash).ifPresent(sessao -> {
                    sessao.revogar();
                    sessaoRepository.save(sessao);
                });
            } catch (Exception ignored) {
            }
        }

        SecurityContextHolder.clearContext();
        return new MessageResponse("Logout realizado com sucesso! Sessão encerrada.");
    }
}
