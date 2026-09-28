import { useEffect, useState } from 'react'
import { reportService } from '../../services/reportService'
import { formatCurrency, formatDate } from '../../utils/format'
import Loading from '../../components/Loading.jsx'

const TABS = ['Bookings', 'Revenue', 'Packages', 'Customers', 'Cancellations', 'Occupancy']

export default function Reports() {
  const [tab, setTab] = useState('Bookings')
  const [data, setData] = useState({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    const loaders = {
      Bookings: reportService.bookings,
      Revenue: reportService.revenue,
      Packages: reportService.packages,
      Customers: reportService.customers,
      Cancellations: reportService.cancellations,
      Occupancy: reportService.occupancy,
    }
    loaders[tab]().then((res) => setData((d) => ({ ...d, [tab]: res }))).finally(() => setLoading(false))
  }, [tab])

  const current = data[tab]

  return (
    <div className="page">
      <div className="container">
        <h1>Reports</h1>
        <div className="chip-group" style={{ marginBottom: 24 }}>
          {TABS.map((t) => <button key={t} className={`chip ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>{t}</button>)}
        </div>

        {loading || !current ? <Loading /> : (
          <>
            {tab === 'Bookings' && (
              <div className="dashboard-grid">
                <div className="stat-card"><div className="value">{current.totalBookings}</div><div className="label">Total</div></div>
                <div className="stat-card"><div className="value">{current.pending}</div><div className="label">Pending</div></div>
                <div className="stat-card"><div className="value">{current.confirmed}</div><div className="label">Confirmed</div></div>
                <div className="stat-card"><div className="value">{current.completed}</div><div className="label">Completed</div></div>
                <div className="stat-card"><div className="value">{current.cancelled}</div><div className="label">Cancelled</div></div>
              </div>
            )}

            {tab === 'Revenue' && (
              <div className="dashboard-grid">
                <div className="stat-card"><div className="value">{formatCurrency(current.totalRevenue)}</div><div className="label">Gross revenue</div></div>
                <div className="stat-card"><div className="value">{formatCurrency(current.refundedAmount)}</div><div className="label">Refunded</div></div>
                <div className="stat-card"><div className="value">{formatCurrency(current.netRevenue)}</div><div className="label">Net revenue</div></div>
              </div>
            )}

            {tab === 'Packages' && (
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Package</th><th>Bookings</th><th>Revenue</th><th>Occupancy</th></tr></thead>
                  <tbody>
                    {current.map((p) => (
                      <tr key={p.packageId}><td>{p.packageName}</td><td>{p.totalBookings}</td><td>{formatCurrency(p.revenue)}</td><td>{p.occupancyPercentage.toFixed(1)}%</td></tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {tab === 'Customers' && (
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Customer</th><th>Bookings</th><th>Total spend</th></tr></thead>
                  <tbody>
                    {current.map((c) => (
                      <tr key={c.customerId}><td>{c.customerName}</td><td>{c.totalBookings}</td><td>{formatCurrency(c.totalSpend)}</td></tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {tab === 'Cancellations' && (
              <div className="dashboard-grid">
                <div className="stat-card"><div className="value">{current.totalCancellations}</div><div className="label">Total cancellations</div></div>
                <div className="stat-card"><div className="value">{formatCurrency(current.totalRefundAmount)}</div><div className="label">Total refunded</div></div>
              </div>
            )}

            {tab === 'Occupancy' && (
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Package</th><th>Start date</th><th>Capacity</th><th>Booked</th><th>Occupancy</th></tr></thead>
                  <tbody>
                    {current.map((o) => (
                      <tr key={o.scheduleId}><td>{o.packageName}</td><td>{formatDate(o.startDate)}</td><td>{o.capacity}</td><td>{o.booked}</td><td>{o.occupancyPercentage.toFixed(1)}%</td></tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
