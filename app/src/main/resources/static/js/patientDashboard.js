// patientDashboard.js
// app/src/main/resources/static/js/patientDashboard.js

import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

// Load doctor cards on page load
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Modal triggers
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

  // Search & filter listeners
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/**
 * Load all doctors and render on page
 */
async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";
  try {
    const doctors = await getDoctors();
    if (!doctors.length) {
      contentDiv.innerHTML = "<p>No doctors available.</p>";
      return;
    }
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
    contentDiv.innerHTML = "<p>Error loading doctors. Please try again later.</p>";
  }
}

/**
 * Render a given list of doctors
 * @param {Array} doctors
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Filter doctors based on input/search values
 */
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value || "";
  const time = document.getElementById("filterTime")?.value || "";
  const specialty = document.getElementById("filterSpecialty")?.value || "";

  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  try {
    const doctors = await filterDoctors(name, time, specialty);

    if (!doctors.length) {
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error filtering doctors:", error);
    contentDiv.innerHTML = "<p>Error filtering doctors. Please try again later.</p>";
  }
}

/**
 * Handle patient signup form submission
 */
window.signupPatient = async function () {
  const name = document.getElementById("signupName")?.value;
  const email = document.getElementById("signupEmail")?.value;
  const password = document.getElementById("signupPassword")?.value;
  const phone = document.getElementById("signupPhone")?.value;
  const address = document.getElementById("signupAddress")?.value;

  try {
    const result = await patientSignup({ name, email, password, phone, address });
    if (result.success) {
      alert(result.message);
      document.getElementById("patientSignup")?.style.display = "none";
      loadDoctorCards();
    } else {
      alert("Signup failed: " + result.message);
    }
  } catch (error) {
    console.error("Error signing up patient:", error);
    alert("Error signing up. Please try again.");
  }
};

/**
 * Handle patient login form submission
 */
window.loginPatient = async function () {
  const email = document.getElementById("loginEmail")?.value;
  const password = document.getElementById("loginPassword")?.value;

  try {
    const response = await patientLogin({ email, password });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      window.location.href = "loggedPatientDashboard.html";
    } else {
      alert("Login failed: Invalid credentials");
    }
  } catch (error) {
    console.error("Error logging in patient:", error);
    alert("Error logging in. Please try again.");
  }
};
