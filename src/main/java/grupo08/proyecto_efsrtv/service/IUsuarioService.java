package grupo08.proyecto_efsrtv.service;

import grupo08.proyecto_efsrtv.dto.UsuarioDetDto;
import grupo08.proyecto_efsrtv.dto.UsuarioDto;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<UsuarioDto> getAllUsers() ;

    Optional<UsuarioDetDto> getUserById(Integer id) ;

    UsuarioDetDto createUser(UsuarioDetDto usuarioDetDto) ;

    boolean saveUser(UsuarioDetDto usuarioDDto) ;

    boolean deleteUser(UsuarioDetDto usuarioDDto) ;

}
