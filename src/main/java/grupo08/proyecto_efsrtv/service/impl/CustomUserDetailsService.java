package grupo08.proyecto_efsrtv.service.impl;

import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    public IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        // consultar usuario por username
        Optional<Usuario> optional = usuarioRepository.findByUserName(userName);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        // retornar un UserDetails que sera usado por Spring Security
        Usuario user = optional.get();
        User.UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getUserName());
        userBuilder.password(user.getPasswordHash());
        userBuilder.roles(user.getUserRole());

        return userBuilder.build();
    }

}
