Section 1: Architecture Summary

This Spring Boot application follows a hybrid architecture combining MVC and REST paradigms. The Admin and Doctor dashboards are implemented using Thymeleaf templates, providing server-side rendered pages for interactive management. All other modules—such as patient registration, appointment scheduling, and prescription retrieval—are exposed through REST APIs for easier integration with front-end clients.

The application interacts with two different databases. MySQL is used for structured data including patient profiles, doctor information, appointments, and admin accounts, utilizing JPA entities for object-relational mapping. MongoDB stores prescription data as flexible documents, allowing rapid retrieval and modification of medical records. All requests first pass through controllers, which delegate business logic to a shared service layer. The service layer then interacts with the appropriate repositories depending on whether the data resides in MySQL or MongoDB, ensuring a clear separation of concerns and maintainable code structure.

Section 2: Numbered Flow of Data and Control

User initiates an action — The user accesses a dashboard (Admin or Doctor) or performs an action such as scheduling an appointment, registering a patient, or requesting a prescription.

Request routing — The action is routed to the appropriate controller: a Thymeleaf controller for dashboards or a REST controller for API calls.

Controller processing — The controller validates the input and calls the service layer to handle business logic.

Service layer delegation — The service layer decides which repository to use based on the type of data (MySQL for structured data, MongoDB for prescriptions).

Database interaction — The repository layer communicates with the database using JPA for MySQL entities or document models for MongoDB.

Data retrieval or update — The requested data is fetched, updated, or persisted, depending on the operation.

Response returned — The service layer passes the data back to the controller, which then renders a Thymeleaf page or returns a REST response to the client.
