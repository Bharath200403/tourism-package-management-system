import { useEffect, useState } from 'react'
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

export default function AdminBookings() {
  const toast = useToast()
  const [bookings, setBookings] = useState(null)
  const [statusFilter, setStatusFilter] = useState('ALL')

  const load = () => bookingService.all().then(setBookings).catch(() => setBookings([]))
  useEffect(() => { load() }, [])

  const handleStatusChange = async (id, status) => {
    try {
      await bookingService.changeStatus(id, status)
      toast.success(`Booking marked as ${status.toLowerCase()}.`)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!bookings) return <Loading />
  const filtered = statusFilter === 'ALL' ? bookings : bookings.filter((b) => b.bookingStatus === statusFilter)

  return (
    <div className="page">
      <div className="container">
        <h1>All bookings</h1>
        <div className="chip-group" style={{ marginBottom: 20 }}>
          {['ALL', 'PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'].map((s) => (
            <button key={s} className={`chip ${statusFilter === s ? 'active' : ''}`} onClick={() => setStatusFilter(s)}>{s}</button>
          ))}
        </div>
        {filtered.length === 0 ? <EmptyState title="No bookings found" /> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Reference</th><th>Customer</th><th>Package</th><th>Dates</th><th>Amount</th><th>Status</th><th>Actions</th></tr></thead>
              <tbody>
                {filtered.map((b) => (
                  <tr key={b.id}>
                    <td>{b.bookingReference}</td>
                    <td>{b.customerName}</td>
                    <td>{b.packageName}</td>
                    <td>{formatDate(b.scheduleStartDate)}</td>
                    <td>{formatCurrency(b.totalAmount)}</td>
                    <td><span className={statusBadgeClass(b.bookingStatus)}>{b.bookingStatus}</span></td>
                    <td className="row" style={{ gap: 6 }}>
                      {NEXT_STATUS[b.bookingStatus]?.map((s) => (
                        <button key={s} className="btn btn-outline btn-sm" onClick={() => handleStatusChange(b.id, s)}>{s.toLowerCase()}</button>
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
