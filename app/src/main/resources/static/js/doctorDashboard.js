// doctorDashboard.js
// app/src/main/resources/static/js/doctorDashboard.js

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Global Variables
const patientTableBody = document.getElementById('patientTableBody');
let selectedDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
const token = localStorage.getItem('token');
let patientName = null;

// Search Bar Functionality
const searchBar = document.getElementById('searchBar');
if (searchBar) {
  searchBar.addEventListener('input', () => {
    const value = searchBar.value.trim();
    patientName = value || null;
    loadAppointments();
  });
}

// Filter Controls
const todayButton = document.getElementById('todayButton');
if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    const datePicker = document.getElementById('datePicker');
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

const datePicker = document.getElementById('datePicker');
if (datePicker) {
  datePicker.value = selectedDate; // Initialize picker to today
  datePicker.addEventListener('change', () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

/**
 * Fetch and render appointments
 */
async function loadAppointments() {
  try {
    if (!patientTableBody) return;

    // Clear existing rows
    patientTableBody.innerHTML = "";

    // Fetch appointments from backend
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || !appointments.length) {
      const tr = document.createElement('tr');
      const td = document.createElement('td');
      td.setAttribute('colspan', '6'); // Adjust to table column count
      td.textContent = "No Appointments found for the selected date.";
      td.classList.add('text-center', 'text-muted');
      tr.appendChild(td);
      patientTableBody.appendChild(tr);
      return;
    }

    // Render each appointment row
    appointments.forEach(appointment => {
      const row = createPatientRow(appointment);
      patientTableBody.appendChild(row);
    });

  } catch (error) {
    console.error("Error loading appointments:", error);
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.setAttribute('colspan', '6');
    td.textContent = "Error loading appointments. Please try again later.";
    td.classList.add('text-center', 'text-danger');
    tr.appendChild(td);
    if (patientTableBody) patientTableBody.appendChild(tr);
  }
}

// Initial render on page load
window.addEventListener('DOMContentLoaded', () => {
  loadAppointments();
});
