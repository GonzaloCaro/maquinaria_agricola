document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("forgotForm");
  const message = document.getElementById("message");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value;

    try {
      const response = await fetch("/api/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        message.textContent =
          "Revisa tu correo para restablecer la contraseña.";
        message.style.color = "green";
      } else {
        message.textContent = "No se encontró una cuenta con ese correo.";
        message.style.color = "red";
      }
    } catch (error) {
      console.error("Error en la solicitud:", error);
      message.textContent = "Error al conectar con el servidor.";
      message.style.color = "red";
    }
  });
});
