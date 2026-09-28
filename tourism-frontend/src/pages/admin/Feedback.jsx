import { useEffect, useState } from 'react'
import { feedbackService } from '../../services/feedbackService'
import { formatDateTime } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function AdminFeedback() {
  const [feedback, setFeedback] = useState(null)

  useEffect(() => {
    feedbackService.all().then(setFeedback).catch(() => setFeedback([]))
  }, [])

  if (!feedback) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Customer feedback</h1>
        {feedback.length === 0 ? <EmptyState title="No feedback yet" /> : (
          <div className="stack">
            {feedback.map((f) => (
              <div className="card" key={f.id}>
                <div className="row between"><strong>{f.subject}</strong><span className="muted">{formatDateTime(f.createdAt)}</span></div>
                <p className="muted">From {f.customerName}</p>
                <p>{f.message}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
