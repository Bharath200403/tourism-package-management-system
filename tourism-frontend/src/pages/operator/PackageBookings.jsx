import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { bookingService } from '../../services/bookingService'
import { formatCurrency, formatDate, statusBadgeClass } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'

const NEXT_STATUS = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['COMPLETED', 'CANCELLED'],
  COMPLETED: [],
  CANCELLED: [],
}

export default function PackageBookings() {
  const { id } = useParams()
  const toast = useToast()
  const [bookings, setBookings] = useState(null)

  const load = () => bookingService.forPackage(id).then(setBookings).catch(() => setBookings([]))
  useEffect(() => { load() }, [id]) // eslint-disable-line react-hooks/exhaustive-deps

  const handleStatusChange = async (bookingId, status) => {
    try {
      await bookingService.changeStatus(bookingId, status)
      toast.success(`Booking marked as ${status.toLowerCase()}.`)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!bookings) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Bookings for this package</h1>
        {bookings.length === 0 ? <EmptyState title="No bookings yet" /> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Reference</th><th>Customer</th><th>Travel dates</th><th>Travelers</th><th>Amount</th><th>Status</th><th>Actions</th></tr></thead>
              <tbody>
                {bookings.map((b) => (
                  <tr key={b.id}>
                    <td>{b.bookingReference}</td>
                    <td>{b.customerName}</td>
                    <td>{formatDate(b.scheduleStartDate)}</td>
                    <td>{b.travelerCount}</td>
                    <td>{formatCurrency(b.totalAmount)}</td>
                    <td><span className={statusBadgeClass(b.bookingStatus)}>{b.bookingStatus}</span></td>
                    <td className="row" style={{ gap: 6 }}>
                      {NEXT_STATUS[b.bookingStatus]?.map((s) => (
                        <button key={s} className="btn btn-outline btn-sm" onClick={() => handleStatusChange(b.id, s)}>Mark {s.toLowerCase()}</button>
                      ))}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
