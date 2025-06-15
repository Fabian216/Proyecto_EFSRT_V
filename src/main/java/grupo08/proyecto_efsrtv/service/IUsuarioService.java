package grupo08.proyecto_efsrtv.service;

import grupo08.proyecto_efsrtv.dto.UsuarioDto;

import java.util.List;

public interface IUsuarioService {
    List<UsuarioDto> getAllUsers() throws Exception;
}
