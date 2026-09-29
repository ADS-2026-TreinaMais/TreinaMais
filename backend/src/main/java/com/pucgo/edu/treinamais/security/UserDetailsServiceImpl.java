package com.pucgo.edu.treinamais.security;

import com.pucgo.edu.treinamais.model.StatusUsuario;
import com.pucgo.edu.treinamais.model.Usuario;
import com.pucgo.edu.treinamais.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        boolean ativo = usuario.getStatus() == StatusUsuario.ATIVO;

        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                ativo,
                true,
                true,
                ativo,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()))
        );
    }
}
