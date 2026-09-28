import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { dashboardService } from '../../services/dashboardService'
import { formatCurrency, formatDate, statusBadgeClass } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import { useAuth } from '../../context/AuthContext.jsx'

export default function CustomerDashboard() {
  const { user } = useAuth()
  const [data, setData] = useState(null)

  useEffect(() => {
    dashboardService.customer().then(setData).catch(() => setData(null))
  }, [])

  if (!data) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Welcome back, {user.fullName?.split(' ')[0] || user.username}</h1>

        <div className="dashboard-grid">
          <div className="stat-card"><div className="value">{data.upcomingTrips}</div><div className="label">Upcoming trips</div></div>
          <div className="stat-card"><div className="value">{data.activeBookings}</div><div className="label">Active bookings</div></div>
          <div className="stat-card"><div className="value">{data.completedTrips}</div><div className="label">Completed trips</div></div>
          <div className="stat-card"><div className="value">{data.cancelledBookings}</div><div className="label">Cancelled bookings</div></div>
        </div>

        <div className="section-title">
          <h2>Recent bookings</h2>
          <Link to="/customer/bookings" className="btn-link">View all →</Link>
        </div>
        {data.recentBookings?.length === 0 ? (
          <p className="muted">You haven't made any bookings yet. <Link to="/packages" className="btn-link">Browse packages →</Link></p>
        ) : (
          <div className="table-wrap" style={{ marginBottom: 32 }}>
            <table>
              <thead><tr><th>Reference</th><th>Package</th><th>Travel dates</th><th>Amount</th><th>Status</th><th></th></tr></thead>
              <tbody>
                {data.recentBookings?.map((b) => (
                  <tr key={b.id}>
                    <td>{b.bookingReference}</td>
                    <td>{b.packageName}</td>
                    <td>{formatDate(b.scheduleStartDate)}</td>
                    <td>{formatCurrency(b.totalAmount)}</td>
                    <td><span className={statusBadgeClass(b.bookingStatus)}>{b.bookingStatus}</span></td>
                    <td><Link to={`/customer/bookings/${b.id}`} className="btn-link">View</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {data.recommendedPackages?.length > 0 && (
          <>
            <h2>Recommended for you</h2>
            <div className="grid">
              {data.recommendedPackages.map((p) => (
                <Link to={`/packages/${p.id}`} className="pkg-card" key={p.id}>
                  <div className="pkg-card__media"><span>{p.destinationName}</span></div>
                  <div className="pkg-card__body">
                    <h4>{p.name}</h4>
                    <div className="pkg-card__meta">{p.durationDays} days</div>
                    <div className="pkg-card__price">{formatCurrency(p.basePrice)}</div>
                  </div>
                </Link>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  )
}
