# 🔐 KeyNest

**KeyNest** es un gestor de contraseñas sencillo y funcional desarrollado en Java. Utiliza el algoritmo de cifrado **AES** para proteger tus contraseñas y organiza los datos mediante un modelo básico de servicios y usuarios. Ideal para aprender conceptos de criptografía, diseño modular y buenas prácticas de desarrollo en Java.

---

## 🧩 Tecnologías utilizadas

- ☕ Java (JDK 8+)
- 🔐 AES (Advanced Encryption Standard)
- 🧠 Arquitectura modular (Crypto, Model, etc.)
- 🛠️ IntelliJ IDEA (entorno de desarrollo sugerido)

---

## 📁 Estructura del proyecto

KeyNest

- README
- Main
- Crypto
	- PasswordUtils
- Manager
	- PasswordGenerator
	- PasswordManager
- Model
	- PasswordEntry
- Storage
	- StorageService


---

## ✨ Funcionalidades

- Cifrado de contraseñas con AES usando una clave secreta definida por el usuario
- Descifrado de contraseñas para su uso en tiempo de ejecución
- Gestión de múltiples entradas de contraseña (servicio, usuario, clave cifrada)
- Modularidad: separación clara entre lógica de cifrado y representación de datos

---

Creado en IntelliJ con la tecnología de Java por Samuel Martin, martinsamudev en GitHub