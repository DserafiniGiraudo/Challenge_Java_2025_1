# 🧠 Challenge_Java_2025_1

> Proyecto desarrollado con **Java 17** y **Spring Boot**, dockerizado, con autenticación mediante **JWT**, cache en memoria y cobertura de tests.  
> Permite ejecución local vía **Docker** y expone una API REST documentada con **Swagger**.

---

## 📦 Tecnologías Utilizadas

- ⚙️ **Java 17**
- 🌱 **Spring Boot**
- 🔐 **JWT** – Autenticación basada en tokens
- 🐬 **MySQL** – Base de datos relacional
- 🍃 **MongoDB** – Base de datos NoSQL para auditorías o lecturas específicas
- 🟥 **Redis** – Cache en memoria
- 🧪 **JUnit & Mockito** – Testing unitario
- 📈 **Cobertura de tests** – Aproximadamente 70%
- 🧾 **Swagger** – Documentación de endpoints
- 🐳 **Docker / Docker Compose** – Contenerización de la aplicación

---

## ☕️ Características de Java 17

Este proyecto aprovecha características modernas del lenguaje para mejorar la claridad y reducir el código repetitivo.

### 🔹 Uso de `record`

Para estructuras de datos inmutables como requests/responses:

```java
public record AcreditacionResponse(
    String id,
    int identificadorPuntoVenta,
    String nombrePuntoventa,
    double importe,
    LocalDate fechaPedido) {}
```

---

## 🧩 Patrones de Diseño Aplicados

- 🟢 **Singleton**  
  Servicios y controladores gestionados como instancias únicas (`@Service`, `@RestController`).

- 🧱 **Builder**  
  Para objetos con múltiples parámetros opcionales o configuraciones detalladas:

```java
PuntoVenta puntoVenta = PuntoVenta.builder()
        .id(1)
        .puntoVenta("CABA")
        .build();
```

---

## 🧱 Patrones de Microservicios

### 🔍 Service Discovery
- Implementado con **Spring Cloud Eureka**.
- Cada microservicio se registra dinámicamente.

> 📌 Patrón: *Service Registry & Discovery*

### 🧭 Load Balancing
- Balanceo de carga mediante **Spring Cloud LoadBalancer**.

> 📌 Patrón: *Client-Side Load Balancing*

### ⚡ Circuit Breaker (Resiliencia)
- Protege ante errores en cascada usando **Resilience4j**.

> 📌 Patrón: *Circuit Breaker (Resilience Pattern)*

---

## ✨ Uso de Lombok

[Lombok](https://projectlombok.org/) permite eliminar código repetitivo:

- `@Getter`, `@Setter`
- `@Builder`, `@Data`
- `@AllArgsConstructor`, `@NoArgsConstructor`
- `@Value`

### Ejemplo:

```java
@Data
@AllArgsConstructor
@Builder
public class CaminoPK {
    private PuntoVenta puntoA;
    private PuntoVenta puntoB;
}
```

🧼 Promueve un enfoque de código limpio.

---

## 📘 Swagger UI

_Interfaz para explorar y probar los endpoints:_

📍 Accedé a la documentación:  
[`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

---

## ✅ Cobertura de Tests

![Coverage](https://github.com/user-attachments/assets/adc47106-5ce5-40ec-9ae6-d5d5ceba3759)

> ℹ️ *Nota: `msvc-acreditaciones` tiene menor cobertura debido a métodos privados o configuraciones de inicialización que no son directamente testeables.*

---

## 📂 Estructura del Proyecto

![Structure](https://github.com/user-attachments/assets/1dc52c8e-eedd-4829-ac07-fb7654c318c0)

---

## ⚙️ Requisitos

- 🐳 [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- 🧪 [Podman](https://podman.io/) (alternativa rootless)
- ☕ [Java](https://adoptium.net/) y 🧰 [Maven](https://maven.apache.org/) (si no usás contenedores)

---

## 🚀 Ejecución en Entorno de Desarrollo

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/DserafiniGiraudo/accenture-contents.git
   ```

2. **Ir a la raíz del proyecto**
   ```bash
   cd Challenge1
   ```

3. **Copiar `.env.template` a `.env` y personalizar los valores**

   ```env
   ENVIRONMENT=dev
   EUREKA_SERVER_PORT=8761
   CONFIG_SERVER_PORT=8888
   REDIS_PORT=6379

   MSVC_PUNTOS_VENTAS_PORT=0
   MSVC_PUNTOS_COSTOS_PORT=0
   MSVC_ACREDITACIONES_PORT=0
   GATEWAY_PORT=8080

   MYSQL_PORT=3306
   MYSQL_USER=aplicacionAcreditaciones
   MYSQL_PASSWORD=1234
   MYSQL_ROOT_PASSWORD=root
   MYSQL_DATABASE=DBAcreditaciones
   ```

4. **Levantar la aplicación**

   ```bash
   # Con Docker
   docker-compose up --build -d

   # O con Podman
   podman-compose up --build -d
   ```

---
