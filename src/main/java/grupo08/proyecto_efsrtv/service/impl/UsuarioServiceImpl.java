package grupo08.proyecto_efsrtv.service.impl;

import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import grupo08.proyecto_efsrtv.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    public IUsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioDto> getAllUsers() throws Exception {
        List<UsuarioDto> users = new ArrayList<>();
        Iterable<Usuario> iterable = usuarioRepository.findAll();
        iterable.forEach(user -> users.add(new UsuarioDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserRole())));
        return users;
    }

}
