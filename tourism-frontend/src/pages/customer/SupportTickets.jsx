import { useEffect, useState } from 'react'
import { supportTicketService } from '../../services/supportTicketService'
import { formatDateTime, statusBadgeClass } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function SupportTickets() {
  const toast = useToast()
  const [tickets, setTickets] = useState(null)
  const [form, setForm] = useState({ subject: '', description: '' })
  const [submitting, setSubmitting] = useState(false)

  const load = () => supportTicketService.mine().then(setTickets).catch(() => setTickets([]))
  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      await supportTicketService.create(form)
      toast.success('Support ticket submitted.')
      setForm({ subject: '', description: '' })
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1>Support tickets</h1>
        <div className="grid cols-2">
          <div>
            <h3>Raise a new ticket</h3>
            <form onSubmit={handleSubmit} className="card">
              <div className="field">
                <label>Subject</label>
                <input value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} required />
              </div>
              <div className="field">
                <label>Description</label>
                <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required />
              </div>
              <button className="btn btn-primary btn-block" disabled={submitting}>{submitting ? 'Submitting…' : 'Submit ticket'}</button>
            </form>
          </div>
          <div>
            <h3>Your tickets</h3>
            {!tickets ? <Loading /> : tickets.length === 0 ? <EmptyState title="No tickets yet" /> : (
              <div className="stack">
                {tickets.map((t) => (
                  <div className="card" key={t.id}>
                    <div className="row between">
                      <strong>{t.subject}</strong>
                      <span className={statusBadgeClass(t.status === 'OPEN' ? 'pending' : t.status === 'RESOLVED' || t.status === 'CLOSED' ? 'completed' : 'confirmed')}>{t.status.replace('_', ' ')}</span>
                    </div>
                    <p style={{ marginTop: 6 }}>{t.description}</p>
                    {t.resolutionNotes && <p className="muted"><strong>Resolution:</strong> {t.resolutionNotes}</p>}
                    <p className="muted" style={{ fontSize: '0.8rem' }}>{formatDateTime(t.createdAt)}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
