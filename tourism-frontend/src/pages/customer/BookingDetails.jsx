import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { bookingService } from '../../services/bookingService'
import { paymentService } from '../../services/paymentService'
import { apiErrorMessage } from '../../services/api'
import { formatCurrency, formatDate, formatDateTime, statusBadgeClass } from '../../utils/format'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'
import ConfirmDialog from '../../components/ConfirmDialog.jsx'

export default function BookingDetails() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const toast = useToast()

  const [booking, setBooking] = useState(null)
  const [paymentMode, setPaymentMode] = useState('CARD_SIMULATION')
  const [paying, setPaying] = useState(false)
  const [cancelling, setCancelling] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [cancelReason, setCancelReason] = useState('')

  const load = () => bookingService.getById(id).then(setBooking).catch(() => setBooking(null))

  useEffect(() => { load() }, [id]) // eslint-disable-line react-hooks/exhaustive-deps

  if (!booking) return <Loading />

  const handlePay = async () => {
    setPaying(true)
    try {
      await paymentService.simulate({ bookingId: booking.id, paymentMode })
      toast.success('Payment simulated successfully — booking confirmed!')
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setPaying(false)
    }
  }

  const handleCancel = async () => {
    setCancelling(true)
    try {
      const result = await bookingService.cancel(booking.id, { reason: cancelReason })
      toast.success(`Booking cancelled. Refund status: ${result.refundStatus.replace('_', ' ')}.`)
      setShowCancelConfirm(false)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setCancelling(false)
    }
  }

  const canCancel = booking.bookingStatus === 'PENDING' || booking.bookingStatus === 'CONFIRMED'
  const canReview = booking.bookingStatus === 'COMPLETED'

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 760 }}>
        {location.state?.justBooked && (
          <div className="alert alert-success">Your seats are reserved! Complete payment below to confirm your booking.</div>
        )}

        <div className="row between">
          <h1>Booking {booking.bookingReference}</h1>
          <span className={statusBadgeClass(booking.bookingStatus)} style={{ fontSize: '0.85rem' }}>{booking.bookingStatus}</span>
        </div>

        <div className="card">
          <h3>{booking.packageName}</h3>
          <p className="muted">{formatDate(booking.scheduleStartDate)} → {formatDate(booking.scheduleEndDate)}</p>
          <div className="row between">
            <span>{booking.travelerCount} traveler(s)</span>
            <strong>{formatCurrency(booking.totalAmount)}</strong>
          </div>
          <p className="muted" style={{ fontSize: '0.82rem', marginTop: 6 }}>Booked on {formatDateTime(booking.bookingDate)}</p>
        </div>

        <div className="card" style={{ marginTop: 16 }}>
          <h4>Travelers</h4>
          <div className="table-wrap">
            <table>
              <thead><tr><th>Name</th><th>Age</th><th>Gender</th><th>Contact</th></tr></thead>
              <tbody>
                {booking.travelers.map((t) => (
                  <tr key={t.id}><td>{t.fullName}</td><td>{t.age}</td><td>{t.gender || '—'}</td><td>{t.contact || '—'}</td></tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {booking.bookingStatus === 'PENDING' && !booking.payment && (
          <div className="card" style={{ marginTop: 16 }}>
            <h4>Complete payment (simulation)</h4>
            <p className="muted" style={{ fontSize: '0.85rem' }}>This is a local payment simulation — no real money moves.</p>
            <div className="field">
              <label>Payment mode</label>
              <select value={paymentMode} onChange={(e) => setPaymentMode(e.target.value)}>
                <option value="CARD_SIMULATION">Card (simulated)</option>
                <option value="UPI_SIMULATION">UPI (simulated)</option>
                <option value="CASH">Cash</option>
              </select>
            </div>
            <button className="btn btn-gold" disabled={paying} onClick={handlePay}>{paying ? 'Processing…' : 'Pay now'}</button>
          </div>
        )}

        {booking.payment && (
          <div className="card" style={{ marginTop: 16 }}>
            <h4>Payment</h4>
            <p className="muted" style={{ fontSize: '0.85rem' }}>{booking.payment.note}</p>
            <div className="row between">
              <span>{booking.payment.paymentMode.replace('_', ' ')} · {booking.payment.transactionReference}</span>
              <span className={statusBadgeClass(booking.payment.paymentStatus === 'SUCCESS' ? 'confirmed' : booking.payment.paymentStatus)}>{booking.payment.paymentStatus}</span>
            </div>
            <Link to={`/customer/bookings/${booking.id}/invoice`} className="btn-link">View invoice →</Link>
          </div>
        )}

        <div className="row" style={{ marginTop: 20 }}>
          {canCancel && (
            <>
              <input
                placeholder="Cancellation reason (optional)"
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                style={{ maxWidth: 260 }}
              />
              <button className="btn btn-danger" onClick={() => setShowCancelConfirm(true)}>Cancel booking</button>
            </>
          )}
          {canReview && (
            <button className="btn btn-outline" onClick={() => navigate(`/customer/bookings/${booking.id}/review`)}>Write a review</button>
          )}
        </div>

        <ConfirmDialog
          open={showCancelConfirm}
          title="Cancel this booking?"
          message="Refund eligibility depends on how close we are to the travel date. This cannot be undone."
          confirmLabel={cancelling ? 'Cancelling…' : 'Yes, cancel'}
          danger
          onCancel={() => setShowCancelConfirm(false)}
          onConfirm={handleCancel}
        />
      </div>
    </div>
  )
}
