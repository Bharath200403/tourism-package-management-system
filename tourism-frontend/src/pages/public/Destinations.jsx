import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { destinationService } from '../../services/destinationService'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function Destinations() {
  const [destinations, setDestinations] = useState(null)

  useEffect(() => {
    destinationService.list({ size: 100 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  }, [])

  return (
    <div className="page">
      <div className="container">
        <h1>Destinations</h1>
        {!destinations ? <Loading /> : destinations.length === 0 ? (
          <EmptyState title="No destinations yet" />
        ) : (
          <div className="grid cols-2">
            {destinations.map((d) => (
              <div className="card" key={d.id}>
                <h4>{d.name}</h4>
                <p className="muted" style={{ marginBottom: 6 }}>{[d.state, d.country].filter(Boolean).join(', ')}</p>
                <p style={{ fontSize: '0.92rem' }}>{d.description}</p>
                <div className="row" style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                  {d.bestSeason && <span>Best season: {d.bestSeason}</span>}
                  {d.estimatedDuration && <span>· Typical stay: {d.estimatedDuration}</span>}
                </div>
                <Link to={`/packages?destinationId=${d.id}`} className="btn-link">See packages →</Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
