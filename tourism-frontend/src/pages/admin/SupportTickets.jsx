import { useEffect, useState } from 'react'
import { supportTicketService } from '../../services/supportTicketService'
import { formatDateTime, statusBadgeClass } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function AdminSupportTickets() {
  const toast = useToast()
  const [tickets, setTickets] = useState(null)
  const [notes, setNotes] = useState({})

  const load = () => supportTicketService.all().then(setTickets).catch(() => setTickets([]))
  useEffect(() => { load() }, [])

  const handleUpdate = async (id, status) => {
    try {
      await supportTicketService.update(id, { status, resolutionNotes: notes[id] || undefined })
      toast.success('Ticket updated.')
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!tickets) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Support tickets</h1>
        {tickets.length === 0 ? <EmptyState title="No support tickets" /> : (
          <div className="stack">
            {tickets.map((t) => (
              <div className="card" key={t.id}>
                <div className="row between">
                  <strong>{t.subject}</strong>
                  <span className={statusBadgeClass(t.status === 'OPEN' ? 'pending' : t.status === 'IN_PROGRESS' ? 'confirmed' : 'completed')}>{t.status.replace('_', ' ')}</span>
                </div>
                <p className="muted">By {t.customerName} · {formatDateTime(t.createdAt)}</p>
                <p>{t.description}</p>
                <div className="field"><label>Resolution notes</label>
                  <input defaultValue={t.resolutionNotes || ''} onChange={(e) => setNotes({ ...notes, [t.id]: e.target.value })} />
                </div>
                <div className="row">
                  <button className="btn btn-outline btn-sm" onClick={() => handleUpdate(t.id, 'IN_PROGRESS')}>Mark in progress</button>
                  <button className="btn btn-outline btn-sm" onClick={() => handleUpdate(t.id, 'RESOLVED')}>Mark resolved</button>
                  <button className="btn btn-outline btn-sm" onClick={() => handleUpdate(t.id, 'CLOSED')}>Close</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
