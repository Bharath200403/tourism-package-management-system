import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { useAuth } from '../../context/AuthContext.jsx'
import { formatCurrency, formatDate } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import StarRating from '../../components/StarRating.jsx'

export default function PackageDetails() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth()
  const [pkg, setPkg] = useState(null)
  const [reviews, setReviews] = useState(null)
  const [selectedScheduleId, setSelectedScheduleId] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    packageService.get(id).then((data) => {
      setPkg(data)
      const firstOpen = data.schedules?.find((s) => s.status === 'OPEN' && s.availableSeats > 0)
      setSelectedScheduleId(firstOpen?.id || data.schedules?.[0]?.id || null)
    }).catch(() => setError('This package could not be found.'))
    packageService.reviews(id, { size: 10 }).then(setReviews).catch(() => setReviews({ content: [] }))
  }, [id])

  if (error) return <div className="page container"><div className="alert alert-error">{error}</div></div>
  if (!pkg) return <Loading />

  const selectedSchedule = pkg.schedules?.find((s) => s.id === selectedScheduleId)

  const handleBook = () => {
    if (!selectedScheduleId) return
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: `/book/${selectedScheduleId}` } } })
      return
    }
    if (user.role !== 'CUSTOMER') {
      return
    }
    navigate(`/book/${selectedScheduleId}`, { state: { pkg, schedule: selectedSchedule } })
  }

  return (
    <div className="page">
      <div className="container">
        <div className="row between">
          <div>
            <p className="muted" style={{ marginBottom: 4 }}>{pkg.destinationName}</p>
            <h1>{pkg.name}</h1>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className="pkg-card__price">{formatCurrency(pkg.basePrice)}</div>
            <span className="muted" style={{ fontSize: '0.82rem' }}>per person</span>
          </div>
        </div>

        <div className="row" style={{ marginBottom: 20 }}>
          <span className="badge badge-confirmed">{pkg.durationDays} days</span>
          {pkg.travelType && <span className="badge badge-active">{pkg.travelType}</span>}
          {pkg.averageRating && (
            <span className="row" style={{ gap: 4 }}>
              <StarRating value={pkg.averageRating} readOnly /> <span className="muted">({pkg.reviewCount} reviews)</span>
            </span>
          )}
        </div>

        <p>{pkg.description}</p>

        <div className="grid cols-2" style={{ marginTop: 24 }}>
          <div className="card">
            <h3>What's included</h3>
            {pkg.inclusions?.length ? (
              <ul>{pkg.inclusions.map((i, idx) => <li key={idx}>{i}</li>)}</ul>
            ) : <p className="muted">Not specified.</p>}
          </div>
          <div className="card">
            <h3>Not included</h3>
            {pkg.exclusions?.length ? (
              <ul>{pkg.exclusions.map((i, idx) => <li key={idx}>{i}</li>)}</ul>
            ) : <p className="muted">Not specified.</p>}
          </div>
        </div>

        {pkg.itinerary?.length > 0 && (
          <div style={{ marginTop: 28 }}>
            <h3>Itinerary</h3>
            <div className="stack">
              {pkg.itinerary.map((day) => (
                <div className="card" key={day.id}>
                  <h4>Day {day.dayNumber}: {day.title}</h4>
                  {day.description && <p className="muted" style={{ marginBottom: 6 }}>{day.description}</p>}
                  {day.activities && <p style={{ fontSize: '0.9rem' }}><strong>Activities:</strong> {day.activities}</p>}
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="card" style={{ marginTop: 28 }}>
          <h3>Choose a schedule</h3>
          {!pkg.schedules || pkg.schedules.length === 0 ? (
            <p className="muted">No schedules are currently open for this package.</p>
          ) : (
            <>
              <div className="stack">
                {pkg.schedules.map((s) => (
                  <label key={s.id} className="row" style={{
                    border: '1px solid var(--border-soft)', borderRadius: 6, padding: '10px 14px',
                    cursor: s.status === 'OPEN' && s.availableSeats > 0 ? 'pointer' : 'not-allowed',
                    opacity: s.status === 'OPEN' && s.availableSeats > 0 ? 1 : 0.55,
                  }}>
                    <input
                      type="radio"
                      name="schedule"
                      disabled={!(s.status === 'OPEN' && s.availableSeats > 0)}
                      checked={selectedScheduleId === s.id}
                      onChange={() => setSelectedScheduleId(s.id)}
                    />
                    <span>{formatDate(s.startDate)} → {formatDate(s.endDate)}</span>
                    <span className={`badge ${s.availableSeats > 0 ? 'badge-active' : 'badge-cancelled'}`} style={{ marginLeft: 'auto' }}>
                      {s.status === 'OPEN' ? `${s.availableSeats} seat(s) left` : s.status}
                    </span>
                  </label>
                ))}
              </div>
              <button className="btn btn-gold" style={{ marginTop: 16 }} disabled={!selectedSchedule || selectedSchedule.availableSeats <= 0} onClick={handleBook}>
                {isAuthenticated && user.role !== 'CUSTOMER' ? 'Only customer accounts can book' : 'Book this schedule'}
              </button>
            </>
          )}
        </div>

        <div style={{ marginTop: 32 }}>
          <h3>Reviews</h3>
          {!reviews || reviews.content.length === 0 ? (
            <p className="muted">No reviews yet for this package.</p>
          ) : (
            <div className="stack">
              {reviews.content.map((r) => (
                <div className="card" key={r.id}>
                  <div className="row between">
                    <strong>{r.customerName}</strong>
                    <StarRating value={r.rating} readOnly />
                  </div>
                  {r.reviewText && <p style={{ marginTop: 8 }}>{r.reviewText}</p>}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
