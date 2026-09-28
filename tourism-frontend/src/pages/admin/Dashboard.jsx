import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { dashboardService } from '../../services/dashboardService'
import { formatCurrency } from '../../utils/format'
import Loading from '../../components/Loading.jsx'

export default function AdminDashboard() {
  const [data, setData] = useState(null)

  useEffect(() => {
    dashboardService.admin().then(setData).catch(() => setData(null))
  }, [])

  if (!data) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Admin dashboard</h1>

        <div className="dashboard-grid">
          <div className="stat-card"><div className="value">{data.totalCustomers}</div><div className="label">Customers</div></div>
          <div className="stat-card"><div className="value">{data.totalOperators}</div><div className="label">Operators</div></div>
          <div className="stat-card"><div className="value">{data.activePackages}/{data.totalPackages}</div><div className="label">Active packages</div></div>
          <div className="stat-card"><div className="value">{data.upcomingTours}</div><div className="label">Upcoming tours</div></div>
        </div>
        <div className="dashboard-grid">
          <div className="stat-card"><div className="value">{data.totalBookings}</div><div className="label">Total bookings</div></div>
          <div className="stat-card"><div className="value">{data.pendingBookings}</div><div className="label">Pending</div></div>
          <div className="stat-card"><div className="value">{data.confirmedBookings}</div><div className="label">Confirmed</div></div>
          <div className="stat-card"><div className="value">{data.cancelledBookings}</div><div className="label">Cancelled</div></div>
        </div>
        <div className="dashboard-grid">
          <div className="stat-card"><div className="value">{formatCurrency(data.revenue)}</div><div className="label">Revenue (successful payments)</div></div>
          <div className="stat-card"><div className="value">{data.occupancyPercentage.toFixed(1)}%</div><div className="label">Overall occupancy</div></div>
        </div>

        <div className="section-title">
          <h2>Most booked packages</h2>
          <Link to="/admin/reports" className="btn-link">Full reports →</Link>
        </div>
        {data.popularPackages?.length === 0 ? <p className="muted">No bookings yet.</p> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Package</th><th>Bookings</th></tr></thead>
              <tbody>
                {data.popularPackages?.map((p) => <tr key={p.packageId}><td>{p.packageName}</td><td>{p.bookingCount}</td></tr>)}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
