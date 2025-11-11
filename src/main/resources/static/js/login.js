document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  const message = document.getElementById("message");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const userName = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userName, password }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.accessToken);
        localStorage.setItem("usuarioNombre", data.userName);
        localStorage.setItem("usuarioRole", data.role);

        message.textContent = "¡Inicio de sesión exitoso!";
        message.style.color = "green";

        setTimeout(() => (window.location.href = "/"), 1000);
      } else {
        message.textContent = "Usuario o contraseña incorrectos.";
        message.style.color = "red";
      }
    } catch (error) {
      console.error("Error en login:", error);
      message.textContent = "Error de conexión con el servidor.";
      message.style.color = "red";
    }
  });
});
