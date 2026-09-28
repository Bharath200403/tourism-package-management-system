import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import { apiErrorMessage } from '../../services/api'
import { validateLoginForm } from '../../utils/validators'
import ErrorAlert from '../../components/ErrorAlert.jsx'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ username: '', password: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const roleHome = (role) => role === 'ADMIN' ? '/admin/dashboard' : role === 'TOUR_OPERATOR' ? '/operator/dashboard' : '/customer/dashboard'

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validateLoginForm(form)
    setFieldErrors(errs)
    if (Object.keys(errs).length) return

    setSubmitting(true)
    setError('')
    try {
      const user = await login(form)
      const redirectTo = location.state?.from?.pathname || roleHome(user.role)
      navigate(redirectTo, { replace: true })
    } catch (err) {
      setError(apiErrorMessage(err, 'Invalid username or password.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container form-card">
        <h1>Log in</h1>
        <p className="muted">New here? <Link to="/register" className="btn-link">Create an account</Link></p>
        <div className="card">
          <ErrorAlert message={error} />
          <form onSubmit={handleSubmit} noValidate>
            <div className="field">
              <label htmlFor="username">Username</label>
              <input id="username" className={fieldErrors.username ? 'invalid' : ''}
                     value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
              {fieldErrors.username && <span className="error-text">{fieldErrors.username}</span>}
            </div>
            <div className="field">
              <label htmlFor="password">Password</label>
              <input id="password" type="password" className={fieldErrors.password ? 'invalid' : ''}
                     value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
              {fieldErrors.password && <span className="error-text">{fieldErrors.password}</span>}
            </div>
            <button className="btn btn-primary btn-block" disabled={submitting}>{submitting ? 'Logging in…' : 'Log in'}</button>
          </form>
          <p className="muted" style={{ marginTop: 16, fontSize: '0.82rem' }}>
            Demo accounts: <code>admin / Admin@123</code>, <code>operator1 / Operator@123</code>, <code>customer1 / Customer@123</code>
          </p>
        </div>
      </div>
    </div>
  )
}
