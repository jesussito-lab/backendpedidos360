package com.pedidos360.pedidos_service;

import com.pedidos360.pedidos_service.model.Producto;
import com.pedidos360.pedidos_service.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class PedidosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(ProductoRepository productoRepository) {
        return args -> {
            if (productoRepository.count() == 0) {
                productoRepository.save(Producto.builder()
                        .nombre("Mouse Inalámbrico")
                        .descripcion("Mouse ergonómico 2.4GHz")
                        .precio(new BigDecimal("15990.00"))
                        .stock(50)
                        .build());

                productoRepository.save(Producto.builder()
                        .nombre("Teclado Mecánico")
                        .descripcion("Teclado RGB Switch Red")
                        .precio(new BigDecimal("49990.00"))
                        .stock(20)
                        .build());

                productoRepository.save(Producto.builder()
                        .nombre("Monitor 24'' Full HD")
                        .descripcion("Monitor IPS 144Hz 1ms")
                        .precio(new BigDecimal("129990.00"))
                        .stock(15)
                        .build());
            }
        };
    }
}