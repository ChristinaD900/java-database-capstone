// ===============================
// Render Footer (Static Component)
// ===============================
export function renderFooter() {
  const footer = document.getElementById("footer");
  if (!footer) return;

  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">

        <!-- Branding -->
        <div class="footer-logo">
          <img src="/assets/images/logo.png" alt="HealthCare Logo" />
          <p>© ${new Date().getFullYear()} HealthCare. All rights reserved.</p>
        </div>

        <!-- Company -->
        <div class="footer-column">
          <h4>Company</h4>
          <a href="#">About</a>
          <a href="#">Careers</a>
          <a href="#">Press</a>
        </div>

        <!-- Support -->
        <div class="footer-column">
          <h4>Support</h4>
          <a href="#">Account</a>
          <a href="#">Help Center</a>
          <a href="#">Contact</a>
        </div>

        <!-- Legals -->
        <div class="footer-column">
          <h4>Legals</h4>
          <a href="#">Terms</a>
          <a href="#">Privacy Policy</a>
          <a href="#">Licensing</a>
        </div>

      </div>
    </footer>
  `;
}

// ===============================
// Auto-run on load
// ===============================
renderFooter();
