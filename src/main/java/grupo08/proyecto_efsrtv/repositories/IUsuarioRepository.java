package grupo08.proyecto_efsrtv.repositories;

import grupo08.proyecto_efsrtv.models.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUsuarioRepository extends CrudRepository<Usuario, Integer> {
    Optional<Usuario> findByUserName(String userName);
}
