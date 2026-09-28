import { useEffect, useState } from 'react'
import { adminUserService } from '../../services/adminUserService'
import { statusBadgeClass } from '../../utils/format'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import { validateRegisterForm } from '../../utils/validators'
import Loading from '../../components/Loading.jsx'

export default function Operators() {
  const toast = useToast()
  const [operators, setOperators] = useState(null)
  const [form, setForm] = useState({ fullName: '', username: '', email: '', password: '', phone: '' })
  const [errors, setErrors] = useState({})
  const [creating, setCreating] = useState(false)

  const load = () => adminUserService.operators().then(setOperators).catch(() => setOperators([]))
  useEffect(() => { load() }, [])

  const toggleStatus = async (o) => {
    const newStatus = o.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'
    try {
      await adminUserService.setStatus(o.id, newStatus)
      toast.success(`${o.username} is now ${newStatus.toLowerCase()}.`)
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  const handleCreate = async (e) => {
    e.preventDefault()
    const errs = validateRegisterForm(form)
    setErrors(errs)
    if (Object.keys(errs).length) return
    setCreating(true)
    try {
      await adminUserService.createOperator(form)
      toast.success('Operator account created.')
      setForm({ fullName: '', username: '', email: '', password: '', phone: '' })
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setCreating(false)
    }
  }

  if (!operators) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Tour operators</h1>

        <div className="grid cols-2">
          <div>
            <h3>Create operator account</h3>
            <form onSubmit={handleCreate} className="card">
              <div className="field"><label>Full name</label>
                <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} className={errors.fullName ? 'invalid' : ''} />
                {errors.fullName && <span className="error-text">{errors.fullName}</span>}
              </div>
              <div className="field"><label>Username</label>
                <input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} className={errors.username ? 'invalid' : ''} />
                {errors.username && <span className="error-text">{errors.username}</span>}
              </div>
              <div className="field"><label>Email</label>
                <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className={errors.email ? 'invalid' : ''} />
                {errors.email && <span className="error-text">{errors.email}</span>}
              </div>
              <div className="field"><label>Temporary password</label>
                <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} className={errors.password ? 'invalid' : ''} />
                {errors.password && <span className="error-text">{errors.password}</span>}
              </div>
              <button className="btn btn-primary btn-block" disabled={creating}>{creating ? 'Creating…' : 'Create operator'}</button>
            </form>
          </div>
          <div>
            <h3>Existing operators</h3>
            <div className="table-wrap">
              <table>
                <thead><tr><th>Username</th><th>Name</th><th>Status</th><th></th></tr></thead>
                <tbody>
                  {operators.map((o) => (
                    <tr key={o.id}>
                      <td>{o.username}</td>
                      <td>{o.fullName}</td>
                      <td><span className={statusBadgeClass(o.status === 'ACTIVE' ? 'active' : 'inactive')}>{o.status}</span></td>
                      <td><button className="btn-link" onClick={() => toggleStatus(o)}>{o.status === 'ACTIVE' ? 'Suspend' : 'Reactivate'}</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
