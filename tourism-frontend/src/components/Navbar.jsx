import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const roleHome = user?.role === 'ADMIN' ? '/admin/dashboard'
    : user?.role === 'TOUR_OPERATOR' ? '/operator/dashboard'
    : '/customer/dashboard'

  return (
    <header className="topnav">
      <div className="topnav__inner">
        <Link to="/" className="brand">Way<em>farer</em></Link>
        <nav className="navlinks">
          <NavLink to="/packages">Packages</NavLink>
          <NavLink to="/destinations">Destinations</NavLink>
          <NavLink to="/trip-planner">Trip Planner</NavLink>

          {isAuthenticated && user.role === 'ADMIN' && (
            <>
              <NavLink to="/admin/dashboard">Admin</NavLink>
              <NavLink to="/admin/customers">Customers</NavLink>
              <NavLink to="/admin/operators">Operators</NavLink>
              <NavLink to="/admin/destinations">Manage destinations</NavLink>
              <NavLink to="/admin/bookings">Bookings</NavLink>
              <NavLink to="/admin/reviews">Reviews</NavLink>
              <NavLink to="/admin/support-tickets">Support</NavLink>
              <NavLink to="/admin/feedback">Feedback</NavLink>
              <NavLink to="/admin/reports">Reports</NavLink>
              <NavLink to="/admin/audit-logs">Audit log</NavLink>
            </>
          )}

          {isAuthenticated && user.role === 'TOUR_OPERATOR' && (
            <>
              <NavLink to="/operator/dashboard">Operator</NavLink>
              <NavLink to="/operator/packages">My packages</NavLink>
            </>
          )}

          {isAuthenticated && user.role === 'CUSTOMER' && (
            <>
              <NavLink to="/customer/bookings">My bookings</NavLink>
              <NavLink to="/customer/support-tickets">Support</NavLink>
            </>
          )}

          {!isAuthenticated && (
            <>
              <NavLink to="/about">About</NavLink>
              <NavLink to="/contact">Contact</NavLink>
            </>
          )}

          {isAuthenticated ? (
            <>
              <NavLink to={roleHome}>Dashboard</NavLink>
              <NavLink to="/profile">Profile</NavLink>
              <button className="btn btn-outline btn-sm" onClick={handleLogout} style={{ borderColor: '#faf7f0', color: '#faf7f0' }}>
                Log out
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login">Log in</NavLink>
              <Link to="/register" className="btn btn-gold btn-sm">Sign up</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
