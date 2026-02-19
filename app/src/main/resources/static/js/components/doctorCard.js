// doctorCard.js

// Assume these are imported from other modules if needed
// import { getPatientData } from './patientAPI';
// import { showBookingOverlay } from './bookingOverlay';

export function createDoctorCard(doctor) {
  // Main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get current user role from localStorage
  const role = localStorage.getItem("userRole");

  // Doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = doctor.specialty;

  const email = document.createElement("p");
  email.textContent = doctor.email;

  const availability = document.createElement("p");
  availability.textContent = doctor.availability.join(", ");

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Button container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Role-based buttons
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      const token = localStorage.getItem("token");
      try {
        const response = await fetch(`/api/doctors/${doctor.id}`, {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
          card.remove(); // Remove card from DOM
        } else {
          alert("Failed to delete doctor.");
        }
      } catch (err) {
        console.error(err);
        alert("Error deleting doctor.");
      }
    });
    actionsDiv.appendChild(removeBtn);
  } else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Patient needs to login first.");
    });
    actionsDiv.appendChild(bookNow);
  } else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      const patientData = await getPatientData(token);
      showBookingOverlay(e, doctor, patientData);
    });
    actionsDiv.appendChild(bookNow);
  }

  // Assemble final card
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
