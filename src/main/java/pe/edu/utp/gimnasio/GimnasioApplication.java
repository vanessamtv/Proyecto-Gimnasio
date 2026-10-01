package pe.edu.utp.gimnasio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal del sistema de Imperium Cross.
 * Levanta el contexto de Spring y expone la web (Thymeleaf) para
 * gestionar clientes, membresias y asistencias.
 */
@SpringBootApplication
@EnableScheduling // permite que MembresiaService actualice el estado de las membresias automaticamente
public class GimnasioApplication {

    public static void main(String[] args) {
        SpringApplication.run(GimnasioApplication.class, args);
    }

}
