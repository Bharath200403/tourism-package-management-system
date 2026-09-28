import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { destinationService } from '../../services/destinationService'
import { formatCurrency } from '../../utils/format'
import Loading from '../../components/Loading.jsx'

export default function Home() {
  const [packages, setPackages] = useState(null)
  const [destinations, setDestinations] = useState(null)

  useEffect(() => {
    packageService.search({ page: 0, size: 3 }).then((res) => setPackages(res.content)).catch(() => setPackages([]))
    destinationService.list({ page: 0, size: 4 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  }, [])

  return (
    <div>
      <section style={{ background: 'linear-gradient(160deg, #1b2a4a 0%, #0f6b66 130%)', color: '#faf7f0', padding: '80px 0' }}>
        <div className="container" style={{ maxWidth: 760 }}>
          <p style={{ color: '#d9c988', fontFamily: 'var(--font-display)', fontStyle: 'italic', fontSize: '1.1rem', marginBottom: 8 }}>
            Plan less. Wander more.
          </p>
          <h1 style={{ color: '#faf7f0' }}>Tour packages, planned and booked without leaving your desk.</h1>
          <p style={{ color: '#e3ded0', fontSize: '1.05rem', maxWidth: '58ch' }}>
            Browse curated destinations, compare itineraries and lock in your seats — with real-time
            availability, transparent pricing and instant confirmation.
          </p>
          <div className="row" style={{ marginTop: 24 }}>
            <Link to="/packages" className="btn btn-gold">Browse packages</Link>
            <Link to="/trip-planner" className="btn btn-outline" style={{ borderColor: '#faf7f0', color: '#faf7f0' }}>Plan my trip</Link>
          </div>
        </div>
      </section>

      <section className="page">
        <div className="container">
          <div className="section-title">
            <h2>Featured packages</h2>
            <Link to="/packages" className="btn-link">View all →</Link>
          </div>
          {!packages ? <Loading /> : packages.length === 0 ? (
            <p className="muted">No packages available right now — please check back soon.</p>
          ) : (
            <div className="grid">
              {packages.map((p) => (
                <Link to={`/packages/${p.id}`} className="pkg-card" key={p.id}>
                  <div className="pkg-card__media"><span>{p.destinationName}</span></div>
                  <div className="pkg-card__body">
                    <h4>{p.name}</h4>
                    <div className="pkg-card__meta">{p.durationDays} days · {p.travelType || 'Tour'}</div>
                    <div className="pkg-card__price">{formatCurrency(p.basePrice)}<span className="muted" style={{ fontSize: '0.75rem' }}> / person</span></div>
                  </div>
                </Link>
              ))}
            </div>
          )}

          <hr className="hr" />

          <div className="section-title">
            <h2>Popular destinations</h2>
            <Link to="/destinations" className="btn-link">View all →</Link>
          </div>
          {!destinations ? <Loading /> : (
            <div className="grid cols-2">
              {destinations.map((d) => (
                <div className="card" key={d.id}>
                  <h4>{d.name}</h4>
                  <p className="muted" style={{ marginBottom: 4 }}>{d.state}{d.state && d.country ? ', ' : ''}{d.country}</p>
                  <p style={{ fontSize: '0.9rem' }}>{d.description}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>
    </div>
  )
}
