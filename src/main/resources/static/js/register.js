document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("registerForm");
  const message = document.getElementById("message");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const userName = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userName, email, password }),
      });

      if (response.ok) {
        message.textContent = "Registro exitoso. Puedes iniciar sesión ahora.";
        message.style.color = "green";
        setTimeout(() => (window.location.href = "/login"), 1500);
      } else {
        message.textContent = "Error al registrarse. Verifica los datos.";
        message.style.color = "red";
      }
    } catch (error) {
      console.error("Error al registrar:", error);
      message.textContent = "Error de conexión con el servidor.";
      message.style.color = "red";
    }
  });
});
