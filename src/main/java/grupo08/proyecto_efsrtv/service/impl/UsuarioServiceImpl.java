package grupo08.proyecto_efsrtv.service.impl;

import grupo08.proyecto_efsrtv.dto.UsuarioDetDto;
import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import grupo08.proyecto_efsrtv.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    public IUsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public List<UsuarioDto> getAllUsers()  {
        List<UsuarioDto> users = new ArrayList<>();
        Iterable<Usuario> iterable = usuarioRepository.findAll();
        iterable.forEach(user -> users.add(new UsuarioDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserRole())));
        return users;
    }

    @Override
    public Optional<UsuarioDetDto> getUserById(Integer id) {
        try {
            Optional<Usuario> optional = usuarioRepository.findById(id);

            if (!optional.isPresent()) {
                return Optional.empty();
            }
            return optional.map(usuario -> new UsuarioDetDto(
                    usuario.getId(),
                    usuario.getUserName(),
                    usuario.getPasswordHash(),
                    usuario.getFirstName(),
                    usuario.getLastName(),
                    usuario.getUserRole()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public UsuarioDetDto createUser(UsuarioDetDto dto) {
        Usuario newUser = new Usuario();
        newUser.setUserName(dto.userName());
        newUser.setPasswordHash(passwordEncoder.encode(dto.passwordHash())); // Hash the password
        newUser.setFirstName(dto.firstName());
        newUser.setLastName(dto.lastName());
        newUser.setUserRole(dto.userRole());
        newUser.setCreated(new Date());
        newUser.setUpdated(new Date());

        Usuario saved = usuarioRepository.save(newUser);

        return new UsuarioDetDto(
                saved.getId(), saved.getUserName(), null, // don’t return password
                saved.getFirstName(), saved.getLastName(), saved.getUserRole()
        );
    }
    @Override
    public boolean saveUser(UsuarioDetDto dto)  {
        Optional<Usuario> optional = usuarioRepository.findById(dto.id());
        if (optional.isEmpty()) {
            return false;
        }

        Usuario usuarioACambiar = optional.get();
        usuarioACambiar.setUserName(dto.userName());
        usuarioACambiar.setFirstName(dto.firstName());
        usuarioACambiar.setLastName(dto.lastName());
        usuarioACambiar.setUserRole(dto.userRole());
        usuarioACambiar.setUpdated(new Date());

        usuarioRepository.save(usuarioACambiar);
        System.out.println("Usuario cambiado con exito:"+dto.userName());
        return true;
    }

    @Override
    public boolean deleteUser(UsuarioDetDto dto)  {
        if (dto.id() == null) return false;
        if (!usuarioRepository.existsById(dto.id())) return false;

        usuarioRepository.deleteById(dto.id());
        return true;
    }

}
