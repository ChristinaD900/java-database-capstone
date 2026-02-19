// patientServices.js
// app/src/main/resources/static/js/services/patientServices.js

import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = `${API_BASE_URL}/patient`;

/**
 * Patient Signup
 * @param {Object} data - Patient details (name, email, password, etc.)
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const resData = await response.json();
    return {
      success: response.ok,
      message: resData.message || (response.ok ? "Signup successful" : "Signup failed"),
    };
  } catch (error) {
    console.error("Error during patient signup:", error);
    return { success: false, message: "Network or server error" };
  }
}

/**
 * Patient Login
 * @param {Object} data - Login credentials (email, password)
 * @returns {Promise<Response>}
 */
export async function patientLogin(data) {
  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    return response; // Frontend can extract token, check status
  } catch (error) {
    console.error("Error during patient login:", error);
    throw error;
  }
}

/**
 * Get logged-in patient data
 * @param {string} token - Authentication token
 * @returns {Promise<Object|null>} - Patient object or null if failed
 */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/me`, {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) throw new Error("Failed to fetch patient data");
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error fetching patient data:", error);
    return null;
  }
}

/**
 * Get patient appointments (works for both patient & doctor dashboards)
 * @param {string} id - Patient ID
 * @param {string} token - Auth token
 * @param {string} user - Role requesting ("patient" or "doctor")
 * @returns {Promise<Array|null>} - Array of appointments or null
 */
export async function getPatientAppointments(id, token, user) {
  try {
    const url = `${PATIENT_API}/${id}/appointments?role=${user}`;
    const response = await fetch(url, {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) throw new Error("Failed to fetch appointments");
    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("Error fetching appointments:", error);
    return null;
  }
}

/**
 * Filter patient appointments
 * @param {string} condition - e.g., "pending" or "consulted"
 * @param {string} name - Patient or doctor name to filter
 * @param {string} token - Auth token
 * @returns {Promise<Array>} - Filtered appointments or empty array
 */
export async function filterAppointments(condition = "", name = "", token) {
  try {
    const params = new URLSearchParams();
    if (condition) params.append("condition", condition);
    if (name) params.append("name", name);

    const url = `${PATIENT_API}/appointments/filter?${params.toString()}`;
    const response = await fetch(url, {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) throw new Error("Failed to filter appointments");
    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Failed to filter appointments.");
    return [];
  }
}
