export default function Loading({ label = 'Loading…' }) {
  return (
    <div className="state-block">
      <div className="spinner" />
      <p className="muted">{label}</p>
    </div>
  )
}
