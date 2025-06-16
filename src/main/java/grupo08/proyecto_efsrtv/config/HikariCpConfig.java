package grupo08.proyecto_efsrtv.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HikariCpConfig {

    @Value("${db.prestamos.url}")
    public String dbPrestamosUrl;
    @Value("${db.prestamos.user}")
    public String dbPrestamosUser;
    @Value("${db.prestamos.pass}")
    public String dbPrestamosPass;
    @Value("${db.prestamos.driver}")
    public String dbPrestamosDriver;

    /*Puede que sea necesario reiniciar pc o ide para que intellij registre los cambios de variables de sistema
    * */

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbPrestamosUrl);
        config.setUsername(dbPrestamosUser);
        config.setPassword(dbPrestamosPass);
        config.setDriverClassName(dbPrestamosDriver);


        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(30000);

        System.out.println("Hikari inicializado");
        return new HikariDataSource(config);

    }

}
