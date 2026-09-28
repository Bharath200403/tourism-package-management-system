export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <span>© {new Date().getFullYear()} Wayfarer Tourism Package Management System — a local demo application.</span>
        <span>Payments shown are simulated. No real transactions occur.</span>
      </div>
    </footer>
  )
}
