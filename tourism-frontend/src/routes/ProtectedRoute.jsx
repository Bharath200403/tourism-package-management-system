import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

/**
 * Guards a route by authentication and, optionally, by role. Frontend
 * gating is a UX convenience only - every protected action is re-checked
 * and enforced by Spring Security on the backend regardless.
 */
export default function ProtectedRoute({ children, roles }) {
  const { user, isAuthenticated } = useAuth()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/" replace />
  }
  return children
}
