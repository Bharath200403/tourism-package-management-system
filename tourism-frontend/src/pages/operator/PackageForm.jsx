import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { destinationService } from '../../services/destinationService'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import { formatDate } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import ErrorAlert from '../../components/ErrorAlert.jsx'

const emptyForm = {
  packageCode: '', name: '', destinationId: '', description: '', durationDays: '',
  basePrice: '', travelType: '', packageType: '', status: 'ACTIVE',
  inclusions: '', exclusions: '',
}

export default function PackageForm() {
  const { id } = useParams()
  const isEdit = !!id
  const navigate = useNavigate()
  const toast = useToast()

  const [destinations, setDestinations] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [pkg, setPkg] = useState(null)
  const [loading, setLoading] = useState(isEdit)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const [dayForm, setDayForm] = useState({ dayNumber: '', title: '', description: '', activities: '' })
  const [scheduleForm, setScheduleForm] = useState({ startDate: '', endDate: '', capacity: '' })

  useEffect(() => {
    destinationService.list({ size: 100 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  }, [])

  const loadPackage = () => {
    if (!isEdit) return
    packageService.get(id).then((data) => {
      setPkg(data)
      setForm({
        packageCode: data.packageCode, name: data.name, destinationId: data.destinationId,
        description: data.description || '', durationDays: data.durationDays, basePrice: data.basePrice,
        travelType: data.travelType || '', packageType: data.packageType || '', status: data.status,
        inclusions: (data.inclusions || []).join('\n'), exclusions: (data.exclusions || []).join('\n'),
      })
    }).finally(() => setLoading(false))
  }

  useEffect(() => { loadPackage() }, [id]) // eslint-disable-line react-hooks/exhaustive-deps

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)
    const payload = {
      packageCode: form.packageCode,
      name: form.name,
      destinationId: parseInt(form.destinationId, 10),
      description: form.description,
      durationDays: parseInt(form.durationDays, 10),
      basePrice: parseFloat(form.basePrice),
      travelType: form.travelType,
      packageType: form.packageType,
      status: form.status,
      inclusions: form.inclusions.split('\n').map((s) => s.trim()).filter(Boolean),
      exclusions: form.exclusions.split('\n').map((s) => s.trim()).filter(Boolean),
    }
    try {
      if (isEdit) {
        await packageService.update(id, payload)
        toast.success('Package updated.')
        loadPackage()
      } else {
        const created = await packageService.create(payload)
        toast.success('Package created — now add itinerary days and schedules below.')
        navigate(`/operator/packages/${created.id}/edit`, { replace: true })
      }
    } catch (err) {
      setError(apiErrorMessage(err, 'Could not save this package.'))
    } finally {
      setSaving(false)
    }
  }

  const handleAddDay = async (e) => {
    e.preventDefault()
    try {
      await packageService.addItineraryDay(id, { ...dayForm, dayNumber: parseInt(dayForm.dayNumber, 10), displayOrder: parseInt(dayForm.dayNumber, 10) })
      toast.success('Itinerary day added.')
      setDayForm({ dayNumber: '', title: '', description: '', activities: '' })
      loadPackage()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  const handleRemoveDay = async (dayId) => {
    try {
      await packageService.removeItineraryDay(id, dayId)
      loadPackage()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  const handleAddSchedule = async (e) => {
    e.preventDefault()
    try {
      await packageService.addSchedule(id, { ...scheduleForm, capacity: parseInt(scheduleForm.capacity, 10) })
      toast.success('Schedule added.')
      setScheduleForm({ startDate: '', endDate: '', capacity: '' })
      loadPackage()
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  if (loading) return <Loading />

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 760 }}>
        <h1>{isEdit ? `Edit: ${pkg?.name || ''}` : 'New package'}</h1>

        <ErrorAlert message={error} />
        <form onSubmit={handleSubmit} className="card">
          <div className="grid cols-2">
            <div className="field"><label>Package code</label>
              <input value={form.packageCode} onChange={(e) => setForm({ ...form, packageCode: e.target.value })} required />
            </div>
            <div className="field"><label>Name</label>
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="field"><label>Destination</label>
              <select value={form.destinationId} onChange={(e) => setForm({ ...form, destinationId: e.target.value })} required>
                <option value="">Select…</option>
                {destinations.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
              </select>
            </div>
            <div className="field"><label>Duration (days)</label>
              <input type="number" min="1" value={form.durationDays} onChange={(e) => setForm({ ...form, durationDays: e.target.value })} required />
            </div>
            <div className="field"><label>Base price per person (₹)</label>
              <input type="number" min="0" step="0.01" value={form.basePrice} onChange={(e) => setForm({ ...form, basePrice: e.target.value })} required />
            </div>
            <div className="field"><label>Travel type</label>
              <input placeholder="Adventure, Beach, Heritage…" value={form.travelType} onChange={(e) => setForm({ ...form, travelType: e.target.value })} />
            </div>
            <div className="field"><label>Package type</label>
              <input placeholder="Leisure, Trekking, Cultural…" value={form.packageType} onChange={(e) => setForm({ ...form, packageType: e.target.value })} />
            </div>
            <div className="field"><label>Status</label>
              <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                <option value="ACTIVE">Active</option>
                <option value="DRAFT">Draft</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
          </div>
          <div className="field"><label>Description</label>
            <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </div>
          <div className="grid cols-2">
            <div className="field"><label>Inclusions (one per line)</label>
              <textarea value={form.inclusions} onChange={(e) => setForm({ ...form, inclusions: e.target.value })} />
            </div>
            <div className="field"><label>Exclusions (one per line)</label>
              <textarea value={form.exclusions} onChange={(e) => setForm({ ...form, exclusions: e.target.value })} />
            </div>
          </div>
          <button className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : isEdit ? 'Save changes' : 'Create package'}</button>
        </form>

        {isEdit && (
          <>
            <div className="card" style={{ marginTop: 24 }}>
              <h3>Itinerary</h3>
              <div className="stack" style={{ marginBottom: 16 }}>
                {pkg?.itinerary?.map((d) => (
                  <div className="row between" key={d.id} style={{ borderBottom: '1px solid var(--border-soft)', paddingBottom: 8 }}>
                    <span>Day {d.dayNumber}: {d.title}</span>
                    <button className="btn-link" style={{ color: 'var(--error)' }} onClick={() => handleRemoveDay(d.id)}>Remove</button>
                  </div>
                ))}
                {!pkg?.itinerary?.length && <p className="muted">No itinerary days yet.</p>}
              </div>
              <form onSubmit={handleAddDay} className="grid cols-2">
                <div className="field" style={{ marginBottom: 0 }}><label>Day number</label>
                  <input type="number" min="1" value={dayForm.dayNumber} onChange={(e) => setDayForm({ ...dayForm, dayNumber: e.target.value })} required />
                </div>
                <div className="field" style={{ marginBottom: 0 }}><label>Title</label>
                  <input value={dayForm.title} onChange={(e) => setDayForm({ ...dayForm, title: e.target.value })} required />
                </div>
                <div className="field" style={{ marginBottom: 0, gridColumn: '1 / -1' }}><label>Description</label>
                  <input value={dayForm.description} onChange={(e) => setDayForm({ ...dayForm, description: e.target.value })} />
                </div>
                <div className="field" style={{ marginBottom: 0, gridColumn: '1 / -1' }}><label>Activities</label>
                  <input value={dayForm.activities} onChange={(e) => setDayForm({ ...dayForm, activities: e.target.value })} />
                </div>
                <button className="btn btn-outline btn-sm" style={{ gridColumn: '1 / -1' }}>+ Add day</button>
              </form>
            </div>

            <div className="card" style={{ marginTop: 24 }}>
              <h3>Schedules</h3>
              <div className="stack" style={{ marginBottom: 16 }}>
                {pkg?.schedules?.map((s) => (
                  <div className="row between" key={s.id} style={{ borderBottom: '1px solid var(--border-soft)', paddingBottom: 8 }}>
                    <span>{formatDate(s.startDate)} → {formatDate(s.endDate)} · {s.availableSeats}/{s.capacity} seats</span>
                    <span className="badge badge-active">{s.status}</span>
                  </div>
                ))}
                {!pkg?.schedules?.length && <p className="muted">No schedules yet — customers can't book without one.</p>}
              </div>
              <form onSubmit={handleAddSchedule} className="grid cols-2">
                <div className="field" style={{ marginBottom: 0 }}><label>Start date</label>
                  <input type="date" value={scheduleForm.startDate} onChange={(e) => setScheduleForm({ ...scheduleForm, startDate: e.target.value })} required />
                </div>
                <div className="field" style={{ marginBottom: 0 }}><label>End date</label>
                  <input type="date" value={scheduleForm.endDate} onChange={(e) => setScheduleForm({ ...scheduleForm, endDate: e.target.value })} required />
                </div>
                <div className="field" style={{ marginBottom: 0 }}><label>Capacity</label>
                  <input type="number" min="1" value={scheduleForm.capacity} onChange={(e) => setScheduleForm({ ...scheduleForm, capacity: e.target.value })} required />
                </div>
                <button className="btn btn-outline btn-sm" style={{ alignSelf: 'end' }}>+ Add schedule</button>
              </form>
            </div>
          </>
        )}
      </div>
    </div>
  )
}
