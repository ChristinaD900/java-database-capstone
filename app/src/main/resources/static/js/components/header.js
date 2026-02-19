// ===============================
// Render Header Based on Role
// ===============================
export function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // 🔹 If user is on homepage, clear session
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // 🔹 Invalid session check
  if (
    (role === "loggedPatient" || role === "admin" || role === "doctor") &&
    !token
  ) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = `
    <div class="nav-wrapper">
      <h2 class="logo">HealthCare</h2>
      <nav class="nav-links">
  `;

  // ===============================
  // Role-based header content
  // ===============================

  // 🔹 Admin
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  // 🔹 Doctor
  else if (role === "doctor") {
    headerContent += `
      <a href="/doctorDashboard.html" id="homeBtn">Home</a>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  }

  // 🔹 Patient (not logged in)
  else if (role === "patient") {
    headerContent += `
      <a href="/login.html" id="loginBtn">Login</a>
      <a href="/signup.html" id="signupBtn">Sign Up</a>
    `;
  }

  // 🔹 Logged Patient
  else if (role === "loggedPatient") {
    headerContent += `
      <a href="/patientDashboard.html" id="homeBtn">Home</a>
      <a href="/appointments.html" id="appointmentsBtn">Appointments</a>
      <a href="#" id="logoutPatientBtn">Logout</a>
    `;
  }

  // 🔹 Default fallback
  else {
    headerContent += `
      <a href="/login.html">Login</a>
      <a href="/signup.html">Sign Up</a>
    `;
  }

  headerContent += `
      </nav>
    </div>
  `;

  // ===============================
  // Inject into DOM
  // ===============================
  headerDiv.innerHTML = headerContent;

  // Attach listeners AFTER render
  attachHeaderButtonListeners();
}

---

## ✅ Attach Event Listeners

```javascript
function attachHeaderButtonListeners() {
  // Admin → Add Doctor
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      if (typeof openModal === "function") {
        openModal("addDoctor");
      }
    });
  }

  // Admin/Doctor logout
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }

  // Patient logout (special behavior)
  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", logoutPatient);
  }
}
