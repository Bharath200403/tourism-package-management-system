import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { bookingService } from '../../services/bookingService'
import { formatCurrency, formatDate, statusBadgeClass } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import Pagination from '../../components/Pagination.jsx'

const STATUS_FILTERS = ['ALL', 'PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED']

export default function MyBookings() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [result, setResult] = useState(null)
  const [statusFilter, setStatusFilter] = useState('ALL')
  const page = parseInt(searchParams.get('page') || '0', 10)

  useEffect(() => {
    bookingService.myBookings({ page, size: 8 }).then(setResult).catch(() => setResult({ content: [], totalPages: 0 }))
  }, [page])

  if (!result) return <Loading />

  const filtered = statusFilter === 'ALL' ? result.content : result.content.filter((b) => b.bookingStatus === statusFilter)

  return (
    <div className="page">
      <div className="container">
        <h1>My bookings</h1>
        <div className="chip-group" style={{ marginBottom: 20 }}>
          {STATUS_FILTERS.map((s) => (
            <button key={s} className={`chip ${statusFilter === s ? 'active' : ''}`} onClick={() => setStatusFilter(s)}>{s}</button>
          ))}
        </div>

        {filtered.length === 0 ? (
          <EmptyState title="No bookings found" message="Try a different filter, or browse packages to make your first booking."
            action={<Link to="/packages" className="btn btn-primary btn-sm">Browse packages</Link>} />
        ) : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Reference</th><th>Package</th><th>Travel dates</th><th>Travelers</th><th>Amount</th><th>Status</th><th></th></tr></thead>
              <tbody>
                {filtered.map((b) => (
                  <tr key={b.id}>
                    <td>{b.bookingReference}</td>
                    <td>{b.packageName}</td>
                    <td>{formatDate(b.scheduleStartDate)}</td>
                    <td>{b.travelerCount}</td>
                    <td>{formatCurrency(b.totalAmount)}</td>
                    <td><span className={statusBadgeClass(b.bookingStatus)}>{b.bookingStatus}</span></td>
                    <td><Link to={`/customer/bookings/${b.id}`} className="btn-link">View</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Pagination page={result.page} totalPages={result.totalPages} onChange={(p) => setSearchParams({ page: p })} />
      </div>
    </div>
  )
}
