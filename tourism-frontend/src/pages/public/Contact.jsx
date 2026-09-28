import { useState } from 'react'
import { useAuth } from '../../context/AuthContext.jsx'
import { useToast } from '../../context/ToastContext.jsx'
import { feedbackService } from '../../services/feedbackService'
import { apiErrorMessage } from '../../services/api'

export default function Contact() {
  const { isAuthenticated } = useAuth()
  const toast = useToast()
  const [form, setForm] = useState({ subject: '', message: '' })
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!isAuthenticated) {
      toast.info('Please log in to send feedback or contact support.')
      return
    }
    setSubmitting(true)
    try {
      await feedbackService.submit(form)
      toast.success('Thanks — your message has been sent.')
      setForm({ subject: '', message: '' })
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container form-card">
        <h1>Contact us</h1>
        <p className="muted">Have a question about a booking or a package? Send us a note.</p>
        <form onSubmit={handleSubmit} className="card">
          <div className="field">
            <label htmlFor="subject">Subject</label>
            <input id="subject" value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} required />
          </div>
          <div className="field">
            <label htmlFor="message">Message</label>
            <textarea id="message" value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} required />
          </div>
          <button className="btn btn-primary btn-block" disabled={submitting}>{submitting ? 'Sending…' : 'Send message'}</button>
        </form>
      </div>
    </div>
  )
}
