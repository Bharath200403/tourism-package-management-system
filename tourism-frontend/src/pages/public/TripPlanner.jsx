import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { tripPlannerService } from '../../services/tripPlannerService'
import { destinationService } from '../../services/destinationService'
import { formatCurrency } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function TripPlanner() {
  const [destinations, setDestinations] = useState([])
  const [form, setForm] = useState({ budget: '', days: '', destinationId: '', travelType: '', preferredActivities: '' })
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    destinationService.list({ size: 100 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const payload = {
        budget: form.budget || null,
        days: form.days ? parseInt(form.days, 10) : null,
        destinationId: form.destinationId || null,
        travelType: form.travelType || null,
        preferredActivities: form.preferredActivities || null,
      }
      const res = await tripPlannerService.plan(payload)
      setResults(res)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1>Trip planner</h1>
        <p className="muted" style={{ maxWidth: '60ch' }}>
          Tell us your budget, trip length and travel style — we'll rank available packages using a simple,
          transparent scoring rule (not a black-box algorithm).
        </p>

        <form onSubmit={handleSubmit} className="card" style={{ marginBottom: 28 }}>
          <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))' }}>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Budget per person (₹)</label>
              <input type="number" min="0" value={form.budget} onChange={(e) => setForm({ ...form, budget: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Trip length (days)</label>
              <input type="number" min="1" value={form.days} onChange={(e) => setForm({ ...form, days: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Destination</label>
              <select value={form.destinationId} onChange={(e) => setForm({ ...form, destinationId: e.target.value })}>
                <option value="">Any</option>
                {destinations.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
              </select>
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Travel style</label>
              <input placeholder="Adventure, Beach, Heritage…" value={form.travelType} onChange={(e) => setForm({ ...form, travelType: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Interests (optional)</label>
              <input placeholder="e.g. trekking, backwaters" value={form.preferredActivities} onChange={(e) => setForm({ ...form, preferredActivities: e.target.value })} />
            </div>
          </div>
          <button className="btn btn-gold" style={{ marginTop: 16 }} disabled={loading}>{loading ? 'Planning…' : 'Plan my trip'}</button>
        </form>

        {loading ? <Loading /> : results && (
          results.length === 0 ? (
            <EmptyState title="No matches yet" message="Try a wider budget or a different travel style." />
          ) : (
            <div className="grid">
              {results.map((p) => (
                <Link to={`/packages/${p.id}`} className="pkg-card" key={p.id}>
                  <div className="pkg-card__media"><span>{p.destinationName}</span></div>
                  <div className="pkg-card__body">
                    <h4>{p.name}</h4>
                    <div className="pkg-card__meta">{p.durationDays} days · {p.travelType || 'Tour'}</div>
                    <div className="pkg-card__price">{formatCurrency(p.basePrice)}</div>
                  </div>
                </Link>
              ))}
            </div>
          )
        )}
      </div>
    </div>
  )
}
