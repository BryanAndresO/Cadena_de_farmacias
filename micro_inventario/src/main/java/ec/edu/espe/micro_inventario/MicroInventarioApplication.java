package ec.edu.espe.micro_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroInventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroInventarioApplication.class, args);
    }

}
