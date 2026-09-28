import { useEffect, useState } from 'react'
import { destinationService } from '../../services/destinationService'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import { statusBadgeClass } from '../../utils/format'
import Loading from '../../components/Loading.jsx'

const emptyForm = { name: '', state: '', country: '', description: '', bestSeason: '', estimatedDuration: '' }

export default function AdminDestinations() {
  const toast = useToast()
  const [destinations, setDestinations] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = () => destinationService.list({ size: 100 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  useEffect(() => { load() }, [])

  const startEdit = (d) => {
    setEditingId(d.id)
    setForm({ name: d.name, state: d.state || '', country: d.country || '', description: d.description || '', bestSeason: d.bestSeason || '', estimatedDuration: d.estimatedDuration || '' })
  }

  const resetForm = () => { setEditingId(null); setForm(emptyForm) }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      if (editingId) {
        await destinationService.update(editingId, form)
        toast.success('Destination updated.')
      } else {
        await destinationService.create(form)
        toast.success('Destination created.')
      }
      resetForm()
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handleDeactivate = async (id) => {
    try {
      await destinationService.deactivate(id)
      toast.success('Destination deactivated.')
      load()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (!destinations) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Destinations</h1>
        <div className="grid cols-2">
          <div>
            <h3>{editingId ? 'Edit destination' : 'Add destination'}</h3>
            <form onSubmit={handleSubmit} className="card">
              <div className="field"><label>Name</label>
                <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div className="grid cols-2">
                <div className="field" style={{ marginBottom: 0 }}><label>State</label>
                  <input value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} />
                </div>
                <div className="field" style={{ marginBottom: 0 }}><label>Country</label>
                  <input value={form.country} onChange={(e) => setForm({ ...form, country: e.target.value })} />
                </div>
              </div>
              <div className="field"><label>Description</label>
                <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
              </div>
              <div className="grid cols-2">
                <div className="field" style={{ marginBottom: 0 }}><label>Best season</label>
                  <input value={form.bestSeason} onChange={(e) => setForm({ ...form, bestSeason: e.target.value })} />
                </div>
                <div className="field" style={{ marginBottom: 0 }}><label>Typical duration</label>
                  <input value={form.estimatedDuration} onChange={(e) => setForm({ ...form, estimatedDuration: e.target.value })} />
                </div>
              </div>
              <div className="row" style={{ marginTop: 12 }}>
                <button className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : editingId ? 'Save changes' : 'Add destination'}</button>
                {editingId && <button type="button" className="btn btn-outline" onClick={resetForm}>Cancel</button>}
              </div>
            </form>
          </div>
          <div>
            <h3>All destinations</h3>
            <div className="stack">
              {destinations.map((d) => (
                <div className="card" key={d.id}>
                  <div className="row between">
                    <strong>{d.name}</strong>
                    <span className={statusBadgeClass(d.status === 'ACTIVE' ? 'active' : 'inactive')}>{d.status}</span>
                  </div>
                  <p className="muted" style={{ margin: '4px 0' }}>{[d.state, d.country].filter(Boolean).join(', ')}</p>
                  <div className="row">
                    <button className="btn-link" onClick={() => startEdit(d)}>Edit</button>
                    {d.status === 'ACTIVE' && <button className="btn-link" style={{ color: 'var(--error)' }} onClick={() => handleDeactivate(d.id)}>Deactivate</button>}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
