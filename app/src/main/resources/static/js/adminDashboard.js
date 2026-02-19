// adminDashboard.js
// app/src/main/resources/static/js/adminDashboard.js

import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Bind Add Doctor button to modal
document.getElementById('addDocBtn').addEventListener('click', () => {
  openModal('addDoctor');
});

// Load doctor cards on page load
window.addEventListener('DOMContentLoaded', () => {
  loadDoctorCards();
  bindFilters();
});

/**
 * Fetch all doctors and render cards
 */
async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  const doctors = await getDoctors();

  if (!doctors.length) {
    contentDiv.textContent = "No doctors found";
    return;
  }

  renderDoctorCards(doctors);
}

/**
 * Render doctor cards from a list
 * @param {Array} doctors
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Bind search and filter inputs
 */
function bindFilters() {
  const searchInput = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  searchInput.addEventListener("input", filterDoctorsOnChange);
  filterTime.addEventListener("change", filterDoctorsOnChange);
  filterSpecialty.addEventListener("change", filterDoctorsOnChange);
}

/**
 * Handle filtering of doctors
 */
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar").value;
  const time = document.getElementById("filterTime").value;
  const specialty = document.getElementById("filterSpecialty").value;

  const filteredDoctors = await filterDoctors(name, time, specialty);

  if (!filteredDoctors.length) {
    document.getElementById("content").textContent = "No doctors found";
    return;
  }

  renderDoctorCards(filteredDoctors);
}

/**
 * Handle Add Doctor modal submission
 */
export async function adminAddDoctor(event) {
  event.preventDefault();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Admin not authenticated!");
    return;
  }

  // Collect form inputs
  const name = document.getElementById("docName").value;
  const email = document.getElementById("docEmail").value;
  const password = document.getElementById("docPassword").value;
  const mobile = document.getElementById("docMobile").value;
  const specialty = document.getElementById("docSpecialty").value;

  // Collect availability from checkboxes
  const availabilityNodes = document.querySelectorAll("input[name='docAvailability']:checked");
  const availability = Array.from(availabilityNodes).map(input => input.value);

  const doctor = { name, email, password, mobile, specialty, availability };

  const result = await saveDoctor(doctor, token);

  if (result.success) {
    alert("Doctor added successfully!");
    loadDoctorCards();
    // Close modal (assumes modal has id 'addDoctor')
    document.getElementById('addDoctor').style.display = "none";
  } else {
    alert("Failed to add doctor: " + result.message);
  }
}

// Bind the Add Doctor form submission to handler
const addDoctorForm = document.getElementById("addDoctorForm");
if (addDoctorForm) {
  addDoctorForm.addEventListener("submit", adminAddDoctor);
}
