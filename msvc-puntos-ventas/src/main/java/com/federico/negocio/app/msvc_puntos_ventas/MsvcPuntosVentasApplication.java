package com.federico.negocio.app.msvc_puntos_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MsvcPuntosVentasApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsvcPuntosVentasApplication.class, args);
	}
}
