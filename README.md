# 🧠 Challenge_Java_2025_1
> Proyecto desarrollado con Java y Spring Boot, dockerizado y con test coverage.  
> Permite la ejecución local mediante Docker y expone una API documentada con Swagger.

---

## 📦 Tecnologías Utilizadas

- ⚙️ **Java 17**
- 🌱 **Spring Boot**
- 🐳 **Docker / Docker Compose**
- ✅ **JUnit y Mockito para testing**
- 🧪 **Cobertura de test: ~70%**
- 🧾 **Swagger para documentación**
- 🟥 **Redis** – Cache en memoria
- 🐬 **MySQL** – Base de datos relacional


## ☕️ Características de Java 17

Este proyecto utiliza **Java 17**, lo que nos permite aprovechar nuevas funcionalidades del lenguaje que mejoran la legibilidad, mantenibilidad y seguridad del código.

### 🔹 `record` para clases inmutables

Simplificamos clases que sólo representan datos como requests/responses utilizando `record`, lo cual reduce el boilerplate automáticamente:

```java
public record AcreditacionResponse(
    String id,
    int identificadorPuntoVenta,
    String nombrePuntoventa,
    double importe,
    LocalDate fechaPedido) {}
```

## 🧩 Patrones de diseño utilizados

Este proyecto aplica diversos patrones de diseño para mejorar la escalabilidad, mantenibilidad y claridad del código. La mayoría se encarga SpringBoot como el Singleton.

### 🟢 Singleton
Se utilizó para clases que deben tener una única instancia compartida, como servicios utilitarios o manejadores de configuración.

> Ejemplo: `Servicios anotados con @Service, controladores con @RestController`

---

### 🧱 Builder
Se aplicó para la construcción de objetos complejos de manera controlada, especialmente en DTOs o configuraciones con muchos parámetros opcionales.

```java
PuntoVenta puntoVenta = PuntoVenta.builder()
        .id(1)
        .puntoVenta("CABA")
        .build();
```

## 🧱 Patrones de Microservicios Utilizados

Este proyecto adopta patrones arquitectónicos comunes en sistemas distribuidos para garantizar escalabilidad, disponibilidad y mantenimiento.

### 🔍 Service Discovery
Mediante **Spring Cloud Eureka**, cada microservicio se registra en un servidor central que actúa como "páginas amarillas", permitiendo la detección dinámica entre ellos.

> 📌 Patrón aplicado: **Service Registry & Discovery**

---

### 🧭 Load Balancing
Con la integración de **Spring Cloud LoadBalancer** (o mediante un Gateway), se reparten automáticamente las peticiones entre múltiples instancias de un mismo servicio.

> 📌 Patrón aplicado: **Client-Side Load Balancing**

---

### ⚡ Resiliencia (Circuit Breaker)

Se implementó el patrón **Circuit Breaker** para proteger los microservicios ante fallos repetidos en dependencias externas. Esto evita que una falla en un servicio propague errores al resto del sistema.

> 📌 Patrón aplicado: **Circuit Breaker (Resilience Pattern)**  
> 🛠️ Implementado mediante: **Resilience4j**

Cuando un servicio falla repetidamente:
- El circuito **se abre** y se detienen temporalmente los llamados.
- Luego, pasa a un estado **half-open** para verificar si el servicio se recuperó.
- Si es exitoso, vuelve a estado **closed**.

---


## ⚡ Uso de Lombok

Este proyecto utiliza [**Lombok**](https://projectlombok.org/) para reducir la verbosidad del código Java, generando automáticamente métodos comunes como getters, setters, constructores, `equals()`, `hashCode()` y más.

### ✨ Anotaciones comunes utilizadas

- `@Getter`, `@Setter` → Generan automáticamente los métodos de acceso.
- `@Builder` → Facilita la creación de objetos con el patrón Builder.
- `@AllArgsConstructor`, `@NoArgsConstructor` → Generan constructores.
- `@Data` → Combina `@Getter`, `@Setter`, `@ToString`, `@EqualsAndHashCode`, y `@RequiredArgsConstructor`.
- `@Value` → Marca la clase como inmutable.

### 🔍 Ejemplo:

```java
@Data
@AllArgsConstructor
@Builder
public class CaminoPK {

    private PuntoVenta puntoA;
    private PuntoVenta puntoB;

}
```

🧼 Esto promueve un enfoque de código limpio, enfocado en la lógica y no en el boilerplate.

=======

### 📂 Estructura del proyecto

![image](https://github.com/user-attachments/assets/1dc52c8e-eedd-4829-ac07-fb7654c318c0)
 
---

### 📘 Swagger UI

_Interfaz para explorar y probar los endpoints disponibles:_  
![image](https://github.com/user-attachments/assets/c4e2aa5f-b839-49bc-8535-f722cec4930a)
![image](https://github.com/user-attachments/assets/0cb33a1c-5896-4139-82bf-2c68d9731a44)
![image](https://github.com/user-attachments/assets/b3354ab7-14f8-4892-95d1-879d8faa947d)


Accedé a la documentación en:  
➡️ [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

---

### ✅ Cobertura de tests

![image](https://github.com/user-attachments/assets/adc47106-5ce5-40ec-9ae6-d5d5ceba3759)


**msvc-acreditaciones si bien tiene un porcentaje bajo, se debe a metodos privados y/o configuracion de inicalizacion que no se pueden acceder desde testing.
=======



---

## ⚙️ Requisitos

- Tener instalado uno de los siguientes:
  - 🐳 [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  - 🧪 [Podman](https://podman.io/) (como alternativa a Docker)
  
> ⚠️ Si usás Podman, asegurate de tener habilitado el modo rootless o configurar `podman-docker` para que los comandos de Docker funcionen con Podman.

- ☕ Java y 🧰 Maven (solo si querés correr la app sin contenedores)

---

## 🚀 Ejecutar en entorno de desarrollo

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/DserafiniGiraudo/accenture-contents.git
   ```
2. **Navegar a la raiz del proyecto
   ```
   cd Challenge1
   ```
3. **hacer una copia de ```env.template``` quitando el .template y cargandole los valores deseados ejemplo:
    ```
    #Entorno
    ENVIRONMENT=dev
    #Eureka
    EUREKA_SERVER_PORT=8761
    #Config Server
    CONFIG_SERVER_PORT=8888
    #Redis
    REDIS_PORT=6379
    #Microservicios
    MSVC_PUNTOS_VENTAS_PORT=0
    MSVC_PUNTOS_COSTOS_PORT=0
    MSVC_ACREDITACIONES_PORT=0
    GATEWAY_PORT=8080
    
    ##MySQL
    MYSQL_PORT=3306
    MYSQL_USER=aplicacionAcreditaciones
    MYSQL_PASSWORD=1234
    MYSQL_ROOT_PASSWORD=root
    MYSQL_DATABASE=DBAcreditaciones
    ```
5. **Levantar la aplicación
   ```
   ##con Docker
   docker-compose up --build -d
   ##con Podman
   podman-compose up --build -d
   ```
