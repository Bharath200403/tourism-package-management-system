import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { reviewService } from '../../services/reviewService'
import { apiErrorMessage } from '../../services/api'
import { useToast } from '../../context/ToastContext.jsx'
import StarRating from '../../components/StarRating.jsx'
import ErrorAlert from '../../components/ErrorAlert.jsx'

export default function SubmitReview() {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const [rating, setRating] = useState(5)
  const [reviewText, setReviewText] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      await reviewService.submit({ bookingId: parseInt(id, 10), rating, reviewText })
      toast.success('Thanks for your review!')
      navigate(`/customer/bookings/${id}`)
    } catch (err) {
      setError(apiErrorMessage(err, 'Could not submit your review.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="container form-card">
        <h1>Write a review</h1>
        <div className="card">
          <ErrorAlert message={error} />
          <form onSubmit={handleSubmit}>
            <div className="field">
              <label>Your rating</label>
              <StarRating value={rating} onChange={setRating} />
            </div>
            <div className="field">
              <label>Your experience (optional)</label>
              <textarea value={reviewText} onChange={(e) => setReviewText(e.target.value)} placeholder="What stood out about the trip?" />
            </div>
            <button className="btn btn-primary btn-block" disabled={submitting}>{submitting ? 'Submitting…' : 'Submit review'}</button>
          </form>
        </div>
      </div>
    </div>
  )
}
