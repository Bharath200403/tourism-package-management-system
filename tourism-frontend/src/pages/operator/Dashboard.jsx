import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { dashboardService } from '../../services/dashboardService'
import { formatCurrency, statusBadgeClass } from '../../utils/format'
import Loading from '../../components/Loading.jsx'

export default function OperatorDashboard() {
  const [data, setData] = useState(null)

  useEffect(() => {
    dashboardService.operator().then(setData).catch(() => setData(null))
  }, [])

  if (!data) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <div className="row between">
          <h1>Operator dashboard</h1>
          <Link to="/operator/packages/new" className="btn btn-gold">+ New package</Link>
        </div>

        <div className="dashboard-grid">
          <div className="stat-card"><div className="value">{data.totalPackages}</div><div className="label">Total packages</div></div>
          <div className="stat-card"><div className="value">{data.activePackages}</div><div className="label">Active packages</div></div>
          <div className="stat-card"><div className="value">{data.totalBookings}</div><div className="label">Total bookings</div></div>
          <div className="stat-card"><div className="value">{data.pendingBookings}</div><div className="label">Pending bookings</div></div>
        </div>

        <div className="section-title">
          <h2>My packages</h2>
          <Link to="/operator/packages" className="btn-link">Manage all →</Link>
        </div>
        {data.myPackages?.length === 0 ? (
          <p className="muted">You haven't created any packages yet.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Code</th><th>Name</th><th>Price</th><th>Status</th><th></th></tr></thead>
              <tbody>
                {data.myPackages?.map((p) => (
                  <tr key={p.id}>
                    <td>{p.packageCode}</td>
                    <td>{p.name}</td>
                    <td>{formatCurrency(p.basePrice)}</td>
                    <td><span className={statusBadgeClass(p.status)}>{p.status}</span></td>
                    <td><Link to={`/operator/packages/${p.id}/edit`} className="btn-link">Manage</Link></td>
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
