import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="page container" style={{ textAlign: 'center' }}>
      <h1>Page not found</h1>
      <p className="muted">The page you're looking for doesn't exist or may have moved.</p>
      <Link to="/" className="btn btn-primary">Back to home</Link>
    </div>
  )
}
