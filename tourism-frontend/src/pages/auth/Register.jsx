import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'
import { apiErrorMessage } from '../../services/api'
import { validateRegisterForm } from '../../utils/validators'
import ErrorAlert from '../../components/ErrorAlert.jsx'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', username: '', email: '', password: '', phone: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validateRegisterForm(form)
    setFieldErrors(errs)
    if (Object.keys(errs).length) return

    setSubmitting(true)
    setError('')
    try {
      await register(form)
      navigate('/customer/dashboard', { replace: true })
    } catch (err) {
      setError(apiErrorMessage(err, 'Registration failed. Please check your details.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container form-card">
        <h1>Create your account</h1>
        <p className="muted">Already have one? <Link to="/login" className="btn-link">Log in</Link></p>
        <div className="card">
          <ErrorAlert message={error} />
          <form onSubmit={handleSubmit} noValidate>
            <div className="field">
              <label htmlFor="fullName">Full name</label>
              <input id="fullName" className={fieldErrors.fullName ? 'invalid' : ''}
                     value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
              {fieldErrors.fullName && <span className="error-text">{fieldErrors.fullName}</span>}
            </div>
            <div className="field">
              <label htmlFor="username">Username</label>
              <input id="username" className={fieldErrors.username ? 'invalid' : ''}
                     value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
              {fieldErrors.username && <span className="error-text">{fieldErrors.username}</span>}
            </div>
            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" type="email" className={fieldErrors.email ? 'invalid' : ''}
                     value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
              {fieldErrors.email && <span className="error-text">{fieldErrors.email}</span>}
            </div>
            <div className="field">
              <label htmlFor="phone">Phone (optional)</label>
              <input id="phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            </div>
            <div className="field">
              <label htmlFor="password">Password</label>
              <input id="password" type="password" className={fieldErrors.password ? 'invalid' : ''}
                     value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
              {fieldErrors.password && <span className="error-text">{fieldErrors.password}</span>}
              <span className="hint">At least 6 characters.</span>
            </div>
            <button className="btn btn-primary btn-block" disabled={submitting}>{submitting ? 'Creating account…' : 'Create account'}</button>
          </form>
        </div>
      </div>
    </div>
  )
}
