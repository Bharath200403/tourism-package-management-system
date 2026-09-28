export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="row" style={{ justifyContent: 'center', marginTop: 24 }}>
      <button className="btn btn-outline btn-sm" disabled={page <= 0} onClick={() => onChange(page - 1)}>Previous</button>
      <span className="muted">Page {page + 1} of {totalPages}</span>
      <button className="btn btn-outline btn-sm" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>Next</button>
    </div>
  )
}
