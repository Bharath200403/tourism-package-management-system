import { useEffect, useState } from 'react'
import { packageService } from '../../services/packageService'
import { reviewService } from '../../services/reviewService'
import StarRating from '../../components/StarRating.jsx'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import { useToast } from '../../context/ToastContext.jsx'
import { apiErrorMessage } from '../../services/api'

export default function AdminReviews() {
  const toast = useToast()
  const [packages, setPackages] = useState([])
  const [selectedPackage, setSelectedPackage] = useState('')
  const [reviews, setReviews] = useState(null)

  useEffect(() => {
    packageService.search({ size: 100, status: 'ACTIVE' }).then((res) => {
      setPackages(res.content)
      if (res.content.length) setSelectedPackage(res.content[0].id)
    })
  }, [])

  useEffect(() => {
    if (!selectedPackage) return
    reviewService.forPackage(selectedPackage, { size: 50 }).then(setReviews).catch(() => setReviews({ content: [] }))
  }, [selectedPackage])

  const handleModerate = async (id, status) => {
    try {
      await reviewService.moderate(id, status)
      toast.success(`Review ${status.toLowerCase()}.`)
      reviewService.forPackage(selectedPackage, { size: 50 }).then(setReviews)
    } catch (err) {
      toast.error(apiErrorMessage(err))
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1>Reviews moderation</h1>
        <div className="field" style={{ maxWidth: 320 }}>
          <label>Package</label>
          <select value={selectedPackage} onChange={(e) => setSelectedPackage(e.target.value)}>
            {packages.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
        </div>
        {!reviews ? <Loading /> : reviews.content.length === 0 ? <EmptyState title="No reviews for this package" /> : (
          <div className="stack">
            {reviews.content.map((r) => (
              <div className="card" key={r.id}>
                <div className="row between">
                  <strong>{r.customerName}</strong>
                  <StarRating value={r.rating} readOnly />
                </div>
                <p>{r.reviewText}</p>
                <div className="row">
                  <button className="btn btn-outline btn-sm" onClick={() => handleModerate(r.id, 'APPROVED')}>Approve</button>
                  <button className="btn btn-outline btn-sm" onClick={() => handleModerate(r.id, 'REJECTED')}>Reject</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
