import { useEffect, useState } from 'react'
import { authService } from '../../services/authService'
import { useToast } from '../../context/ToastContext.jsx'
import { apiErrorMessage } from '../../services/api'
import Loading from '../../components/Loading.jsx'

export default function Profile() {
  const toast = useToast()
  const [profile, setProfile] = useState(null)
  const [form, setForm] = useState({ fullName: '', phone: '', address: '', city: '', state: '', country: '', preferredTravelType: '', preferredBudget: '' })
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    authService.myProfile().then((p) => {
      setProfile(p)
      setForm((f) => ({ ...f, fullName: p.fullName || '', phone: p.phone || '' }))
    })
  }, [])

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await authService.updateProfile(form)
      toast.success('Profile updated.')
    } catch (err) {
      toast.error(apiErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  if (!profile) return <Loading />

  return (
    <div className="page">
      <div className="container form-card">
        <h1>My profile</h1>
        <div className="card">
          <p className="muted">Username: <strong>{profile.username}</strong> · Email: <strong>{profile.email}</strong></p>
          <hr className="hr" />
          <form onSubmit={handleSave}>
            <div className="field">
              <label>Full name</label>
              <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
            </div>
            <div className="field">
              <label>Phone</label>
              <input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            </div>
            <div className="field">
              <label>City</label>
              <input value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
            </div>
            <div className="field">
              <label>State</label>
              <input value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} />
            </div>
            <div className="field">
              <label>Country</label>
              <input value={form.country} onChange={(e) => setForm({ ...form, country: e.target.value })} />
            </div>
            <div className="field">
              <label>Preferred travel type</label>
              <input placeholder="e.g. Adventure, Beach, Heritage" value={form.preferredTravelType} onChange={(e) => setForm({ ...form, preferredTravelType: e.target.value })} />
            </div>
            <div className="field">
              <label>Preferred budget (₹)</label>
              <input type="number" value={form.preferredBudget} onChange={(e) => setForm({ ...form, preferredBudget: e.target.value })} />
              <span className="hint">Used by the recommendation engine to suggest packages.</span>
            </div>
            <button className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : 'Save changes'}</button>
          </form>
        </div>
      </div>
    </div>
  )
}
