# 🧠 accenture-contents

> Proyecto desarrollado con Java y Spring Boot, dockerizado y con test coverage superior al 70%.  
> Permite la ejecución local mediante Docker y expone una API documentada con Swagger.

---

## 📦 Tecnologías Utilizadas

- ⚙️ **Java 17**
- 🌱 **Spring Boot**
- 🐳 **Docker / Docker Compose**
- ✅ **JUnit y Mockito para testing**
- 🧪 **Cobertura de test: ~70%**
- 🧾 **Swagger para documentación**

---

## 📸 Vista previa del proyecto

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
   git clone https://github.com/tuusuario/accenture-contents.git
   cd accenture-contents
