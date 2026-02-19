package com.project.back_end.services;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    private void init() {
        // Generate signing key from secret
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Generate JWT token for a given identifier (username/email) */
    public String generateToken(String identifier, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7 days
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("role", role)
                .signWith(signingKey)
                .compact();
    }

    /** Extract user identifier (subject) from JWT */
    public String extractIdentifier(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /** Validate JWT token for a given user type */
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            switch (userType.toLowerCase()) {
                case "admin":
                    Optional<Admin> adminOpt = Optional.ofNullable(adminRepository.findByUsername(identifier));
                    return adminOpt.isPresent();
                case "doctor":
                    Optional<Doctor> doctorOpt = Optional.ofNullable(doctorRepository.findByEmail(identifier));
                    return doctorOpt.isPresent();
                case "patient":
                    Optional<Patient> patientOpt = Optional.ofNullable(patientRepository.findByEmail(identifier));
                    return patientOpt.isPresent();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false; // invalid or expired token
        }
    }

    /** Get the signing key used for JWT */
    public SecretKey getSigningKey() {
        return signingKey;
    }
}
