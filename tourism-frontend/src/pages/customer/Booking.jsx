import { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { bookingService } from '../../services/bookingService'
import { formatCurrency, formatDate } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'
import ErrorAlert from '../../components/ErrorAlert.jsx'

const emptyTraveler = () => ({ fullName: '', age: '', gender: '', contact: '', specialRequirement: '' })

export default function Booking() {
  const { scheduleId } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const toast = useToast()

  const [pkg, setPkg] = useState(location.state?.pkg || null)
  const [schedule, setSchedule] = useState(location.state?.schedule || null)
  const [travelers, setTravelers] = useState([emptyTraveler()])
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [loadingPkg, setLoadingPkg] = useState(!location.state?.pkg)

  useEffect(() => {
    if (pkg && schedule) return
    // Direct navigation (e.g. page refresh) without router state: search
    // every package's schedules for this scheduleId as a simple fallback.
    setLoadingPkg(true)
    packageService.search({ size: 100 }).then(async (res) => {
      for (const p of res.content) {
        const full = await packageService.get(p.id)
        const found = full.schedules?.find((s) => String(s.id) === String(scheduleId))
        if (found) {
          setPkg(full)
          setSchedule(found)
          break
        }
      }
    }).finally(() => setLoadingPkg(false))
  }, [scheduleId]) // eslint-disable-line react-hooks/exhaustive-deps

  if (loadingPkg) return <Loading label="Loading schedule details…" />
  if (!pkg || !schedule) {
    return (
      <div className="page container">
        <ErrorAlert message="We couldn't find that schedule. Please go back and pick a package again." />
      </div>
    )
  }

  const updateTraveler = (idx, field, value) => {
    setTravelers((prev) => prev.map((t, i) => (i === idx ? { ...t, [field]: value } : t)))
  }

  const addTraveler = () => {
    if (travelers.length >= schedule.availableSeats) {
      toast.info(`Only ${schedule.availableSeats} seat(s) are available for this schedule.`)
      return
    }
    setTravelers((prev) => [...prev, emptyTraveler()])
  }

  const removeTraveler = (idx) => {
    if (travelers.length === 1) return
    setTravelers((prev) => prev.filter((_, i) => i !== idx))
  }

  const totalAmount = pkg.basePrice * travelers.length

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    for (const t of travelers) {
      if (!t.fullName || !t.age) {
        setError('Please fill in the full name and age for every traveler.')
        return
      }
    }
    setSubmitting(true)
    try {
      const booking = await bookingService.create({
        scheduleId: schedule.id,
        travelers: travelers.map((t) => ({ ...t, age: parseInt(t.age, 10) })),
      })
      toast.success(`Booking ${booking.bookingReference} created — proceed to payment.`)
      navigate(`/customer/bookings/${booking.id}`, { state: { justBooked: true } })
    } catch (err) {
      setError(apiErrorMessage(err, 'Could not complete this booking.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 720 }}>
        <h1>Book: {pkg.name}</h1>
        <div className="card" style={{ marginBottom: 20 }}>
          <div className="row between">
            <span>{formatDate(schedule.startDate)} → {formatDate(schedule.endDate)}</span>
            <span className="badge badge-active">{schedule.availableSeats} seat(s) left</span>
          </div>
          <p className="muted" style={{ marginTop: 6, marginBottom: 0 }}>{formatCurrency(pkg.basePrice)} per person</p>
        </div>

        <ErrorAlert message={error} />

        <form onSubmit={handleSubmit}>
          <div className="row between" style={{ marginBottom: 12 }}>
            <h3 style={{ margin: 0 }}>Traveler details</h3>
            <button type="button" className="btn btn-outline btn-sm" onClick={addTraveler}>+ Add traveler</button>
          </div>

          <div className="stack">
            {travelers.map((t, idx) => (
              <div className="card" key={idx}>
                <div className="row between">
                  <h4 style={{ margin: 0 }}>Traveler {idx + 1}</h4>
                  {travelers.length > 1 && (
                    <button type="button" className="btn-link" onClick={() => removeTraveler(idx)}>Remove</button>
                  )}
                </div>
                <div className="grid cols-2" style={{ marginTop: 10 }}>
                  <div className="field" style={{ marginBottom: 0 }}>
                    <label>Full name</label>
                    <input value={t.fullName} onChange={(e) => updateTraveler(idx, 'fullName', e.target.value)} required />
                  </div>
                  <div className="field" style={{ marginBottom: 0 }}>
                    <label>Age</label>
                    <input type="number" min="0" value={t.age} onChange={(e) => updateTraveler(idx, 'age', e.target.value)} required />
                  </div>
                  <div className="field" style={{ marginBottom: 0 }}>
                    <label>Gender</label>
                    <select value={t.gender} onChange={(e) => updateTraveler(idx, 'gender', e.target.value)}>
                      <option value="">Prefer not to say</option>
                      <option value="Male">Male</option>
                      <option value="Female">Female</option>
                      <option value="Other">Other</option>
                    </select>
                  </div>
                  <div className="field" style={{ marginBottom: 0 }}>
                    <label>Contact</label>
                    <input value={t.contact} onChange={(e) => updateTraveler(idx, 'contact', e.target.value)} />
                  </div>
                </div>
                <div className="field" style={{ marginTop: 10, marginBottom: 0 }}>
                  <label>Special requirement (optional)</label>
                  <input value={t.specialRequirement} onChange={(e) => updateTraveler(idx, 'specialRequirement', e.target.value)} />
                </div>
              </div>
            ))}
          </div>

          <div className="card" style={{ marginTop: 20 }}>
            <div className="row between">
              <span>Total ({travelers.length} traveler{travelers.length > 1 ? 's' : ''})</span>
              <strong style={{ fontSize: '1.2rem' }}>{formatCurrency(totalAmount)}</strong>
            </div>
            <span className="muted" style={{ fontSize: '0.8rem' }}>Final pricing is always calculated and verified by the server.</span>
          </div>

          <button className="btn btn-gold btn-block" style={{ marginTop: 20 }} disabled={submitting}>
            {submitting ? 'Reserving your seats…' : 'Reserve seats & continue'}
          </button>
        </form>
      </div>
    </div>
  )
}
