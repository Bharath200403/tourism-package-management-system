import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { useAuth } from '../../context/AuthContext.jsx'
import { formatCurrency, statusBadgeClass } from '../../utils/format'
import { useToast } from '../../context/ToastContext.jsx'
import { apiErrorMessage } from '../../services/api'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import ConfirmDialog from '../../components/ConfirmDialog.jsx'

export default function OperatorPackages() {
  const { user } = useAuth()
  const toast = useToast()
  const [packages, setPackages] = useState(null)
  const [confirmId, setConfirmId] = useState(null)

  const load = () => {
    // Admins see everything through search; operators see only their own via the "by operator" list
    // exposed through the operator dashboard, so for a full manage view we search with a large page size.
    packageService.search({ size: 100, status: user.role === 'ADMIN' ? undefined : 'ACTIVE' })
      .then((res) => setPackages(user.role === 'ADMIN' ? res.content : res.content))
      .catch(() => setPackages([]))
  }

  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleDeactivate = async () => {
    try {
      await packageService.deactivate(confirmId)
      toast.success('Package deactivated.')
      setConfirmId(null)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!packages) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <div className="row between">
          <h1>Manage packages</h1>
          <Link to="/operator/packages/new" className="btn btn-gold">+ New package</Link>
        </div>

        {packages.length === 0 ? <EmptyState title="No packages yet" /> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Code</th><th>Name</th><th>Destination</th><th>Price</th><th>Status</th><th></th></tr></thead>
              <tbody>
                {packages.map((p) => (
                  <tr key={p.id}>
                    <td>{p.packageCode}</td>
                    <td>{p.name}</td>
                    <td>{p.destinationName}</td>
                    <td>{formatCurrency(p.basePrice)}</td>
                    <td><span className={statusBadgeClass(p.status)}>{p.status}</span></td>
                    <td className="row" style={{ gap: 8 }}>
                      <Link to={`/operator/packages/${p.id}/edit`} className="btn-link">Edit</Link>
                      <Link to={`/operator/packages/${p.id}/bookings`} className="btn-link">Bookings</Link>
                      {p.status === 'ACTIVE' && <button className="btn-link" style={{ color: 'var(--error)' }} onClick={() => setConfirmId(p.id)}>Deactivate</button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <ConfirmDialog
          open={!!confirmId}
          title="Deactivate this package?"
          message="It will no longer be bookable by customers, but existing bookings are unaffected."
          confirmLabel="Deactivate"
          danger
          onCancel={() => setConfirmId(null)}
          onConfirm={handleDeactivate}
        />
      </div>
    </div>
  )
}
