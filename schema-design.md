## MySQL Database Design
Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- phone: VARCHAR(20), NULL
- password_hash: VARCHAR(255), NOT NULL
- date_of_birth: DATE, NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- updated_at: DATETIME, NULL

Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(100), NOT NULL
- last_name: VARCHAR(100), NOT NULL
- email: VARCHAR(255), NOT NULL, UNIQUE
- phone: VARCHAR(20), NULL
- specialization: VARCHAR(150), NOT NULL
- profile_bio: TEXT, NULL
- is_active: TINYINT(1), NOT NULL, DEFAULT 1
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

Table: admin
- id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(100), NOT NULL, UNIQUE
- email: VARCHAR(255), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- last_login: DATETIME, NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- patient_id: INT, NOT NULL, Foreign Key → patients(id)
- appointment_time: DATETIME, NOT NULL
- duration_minutes: INT, NOT NULL, DEFAULT 60
- status: TINYINT, NOT NULL, DEFAULT 0   (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- notes: TEXT, NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
Constraints:
- FOREIGN KEY (doctor_id) → doctors(id) ON DELETE RESTRICT
- FOREIGN KEY (patient_id) → patients(id) ON DELETE CASCADE
- UNIQUE (doctor_id, appointment_time) → prevents double booking

Table: doctor_availability
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- available_date: DATE, NOT NULL
- start_time: TIME, NOT NULL
- end_time: TIME, NOT NULL
- is_available: TINYINT(1), NOT NULL, DEFAULT 1
Constraints:
- FOREIGN KEY (doctor_id) → doctors(id) ON DELETE CASCADE
- CHECK (end_time > start_time)

Table: prescriptions
- id: INT, Primary Key, AUTO_INCREMENT
- appointment_id: INT, NOT NULL, Foreign Key → appointments(id)
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- patient_id: INT, NOT NULL, Foreign Key → patients(id)
- medication_details: TEXT, NOT NULL
- instructions: TEXT, NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
Constraints:
- FOREIGN KEY (appointment_id) → appointments(id) ON DELETE CASCADE
- FOREIGN KEY (doctor_id) → doctors(id) ON DELETE RESTRICT
- FOREIGN KEY (patient_id) → patients(id) ON DELETE CASCADE


## MongoDB Collection Design
{
  "_id": { "$oid": "65f1a9c2e8b3a41234567890" },
  "conversationId": "conv_982341",
  "appointmentId": 51,
  "participants": {
    "patientId": 1203,
    "doctorId": 45
  },
  "senderRole": "patient",
  "messageText": "Hello doctor, I have a question about my prescription.",
  "attachments": [
    {
      "fileName": "lab_report.pdf",
      "fileType": "application/pdf",
      "fileUrl": "https://cdn.clinic.com/uploads/lab_report.pdf",
      "uploadedAt": { "$date": "2026-02-18T10:15:00Z" }
    }
  ],
  "metadata": {
    "tags": ["prescription", "follow-up"],
    "priority": "normal",
    "isRead": false,
    "readAt": null
  },
  "audit": {
    "createdAt": { "$date": "2026-02-18T10:14:22Z" },
    "updatedAt": { "$date": "2026-02-18T10:14:22Z" },
    "ipAddress": "192.168.1.10"
  },
  "reactions": [
    {
      "userRole": "doctor",
      "type": "acknowledged",
      "timestamp": { "$date": "2026-02-18T10:20:00Z" }
    }
  ]
}
