// doctorServices.js
// app/src/main/resources/static/js/services/doctorServices.js

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

/**
 * Fetch all doctors
 * @returns {Promise<Array>} - Array of doctor objects
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) throw new Error("Failed to fetch doctors");

    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Delete a doctor by ID (Admin only)
 * @param {string} id - Doctor's unique ID
 * @param {string} token - Auth token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}`, {
      method: "DELETE",
      headers: {
        "Authorization": `Bearer ${token}`,
      },
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor deleted" : "Failed to delete doctor"),
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "Error deleting doctor" };
  }
}

/**
 * Add a new doctor (Admin only)
 * @param {Object} doctor - Doctor object containing name, email, specialty, availability, etc.
 * @param {string} token - Auth token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor added successfully" : "Failed to add doctor"),
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "Error saving doctor" };
  }
}

/**
 * Filter doctors by name, time, or specialty
 * @param {string} name
 * @param {string} time
 * @param {string} specialty
 * @returns {Promise<Array>} - Array of filtered doctors
 */
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    // Build query string dynamically
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (time) params.append("time", time);
    if (specialty) params.append("specialty", specialty);

    const url = `${DOCTOR_API}/filter?${params.toString()}`;

    const response = await fetch(url);
    if (!response.ok) throw new Error("Failed to filter doctors");

    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("Error filtering doctors:", error);
    return [];
  }
}
