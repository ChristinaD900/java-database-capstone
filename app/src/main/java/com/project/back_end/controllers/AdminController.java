
package com.project.back_end.controllers;

@RestController
@RequestMapping("${api.path}admin")  // Base URL: e.g., /api/admin
public class AdminController {

    private final Service service;

    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    /** 
     * Endpoint for admin login
     * Accepts Admin credentials and returns a JWT token if valid
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Delegates the validation and token generation to Service
        return service.validateAdmin(admin);
    }
}
