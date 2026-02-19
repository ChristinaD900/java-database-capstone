package com.project.back_end.mvc;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService; // Service to validate tokens

    /**
     * Admin dashboard access
     * @param token JWT token from request path
     * @return Thymeleaf view name
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, Object> validation = tokenService.validateToken(token, "admin");

        if (validation.isEmpty()) {
            // Token valid → render admin dashboard
            return "admin/adminDashboard";
        } else {
            // Invalid token → redirect to login page
            return "redirect:http://localhost:8080";
        }
    }

    /**
     * Doctor dashboard access
     * @param token JWT token from request path
     * @return Thymeleaf view name
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, Object> validation = tokenService.validateToken(token, "doctor");

        if (validation.isEmpty()) {
            // Token valid → render doctor dashboard
            return "doctor/doctorDashboard";
        } else {
            // Invalid token → redirect to login page
            return "redirect:http://localhost:8080";
        }
    }
}
