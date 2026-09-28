import { useEffect, useState } from 'react'
import { adminUserService } from '../../services/adminUserService'
import { formatDate, statusBadgeClass } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import Loading from '../../components/Loading.jsx'

export default function Customers() {
  const toast = useToast()
  const [customers, setCustomers] = useState(null)

  const load = () => adminUserService.customers().then(setCustomers).catch(() => setCustomers([]))
  useEffect(() => { load() }, [])

  const toggleStatus = async (c) => {
    const newStatus = c.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'
    try {
      await adminUserService.setStatus(c.id, newStatus)
      toast.success(`${c.username} is now ${newStatus.toLowerCase()}.`)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!customers) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Customers</h1>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Username</th><th>Full name</th><th>Email</th><th>Status</th><th>Joined</th><th></th></tr></thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.id}>
                  <td>{c.username}</td>
                  <td>{c.fullName}</td>
                  <td>{c.email}</td>
                  <td><span className={statusBadgeClass(c.status === 'ACTIVE' ? 'active' : 'inactive')}>{c.status}</span></td>
                  <td>{formatDate(c.createdAt)}</td>
                  <td><button className="btn-link" onClick={() => toggleStatus(c)}>{c.status === 'ACTIVE' ? 'Suspend' : 'Reactivate'}</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
