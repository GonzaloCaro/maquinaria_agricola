document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const authDiv = document.getElementById("auth-buttons");
  const userDiv = document.getElementById("user-info");
  const nombreSpan = document.getElementById("usuarioNombre");
  const adminButtons = document.getElementById("admin-buttons");

  userDiv.style.display = "none";
  adminButtons.style.display = "none";

  if (token) {
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      const nombre =
        payload.username || localStorage.getItem("usuarioNombre") || "Usuario";
      const roles = localStorage.getItem("usuarioRole") || [];
      nombreSpan.textContent = nombre;

      userDiv.style.display = "inline";
      authDiv.style.display = "none";

      if (roles.includes("admin")) {
        adminButtons.style.display = "inline";
      }
    } catch (e) {
      console.error("JWT inválido", e);
    }
  }

  // Logout
  document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioNombre");
    localStorage.removeItem("usuarioRole");
    window.location.href = "/";
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const botones = document.querySelectorAll(".ver-detalles-btn");

  botones.forEach((btn) => {
    const maquinaId = btn.dataset.id;

    if (token) {
      // Usuario autenticado -> puede acceder a detalles
      btn.addEventListener("click", () => {
        window.location.href = `/arriendo/${maquinaId}`;
      });
    } else {
      // Usuario no autenticado -> alerta
      btn.addEventListener("click", () => {
        alert(
          "Debes iniciar sesión o registrarte para ver detalles de una máquina"
        );
      });
    }
  });
});
