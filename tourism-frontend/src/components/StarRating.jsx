export default function StarRating({ value = 0, onChange, readOnly = false }) {
  const stars = [1, 2, 3, 4, 5]
  return (
    <span className="stars">
      {stars.map((s) => (
        <span
          key={s}
          role={readOnly ? undefined : 'button'}
          aria-label={readOnly ? undefined : `Rate ${s} star`}
          style={{ cursor: readOnly ? 'default' : 'pointer' }}
          onClick={() => !readOnly && onChange && onChange(s)}
        >
          {s <= Math.round(value) ? '★' : '☆'}
        </span>
      ))}
    </span>
  )
}
