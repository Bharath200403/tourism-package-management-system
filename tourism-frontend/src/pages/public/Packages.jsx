import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { packageService } from '../../services/packageService'
import { destinationService } from '../../services/destinationService'
import { formatCurrency } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import Pagination from '../../components/Pagination.jsx'

export default function Packages() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [destinations, setDestinations] = useState([])
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({
    name: searchParams.get('name') || '',
    destinationId: searchParams.get('destinationId') || '',
    minDuration: searchParams.get('minDuration') || '',
    maxDuration: searchParams.get('maxDuration') || '',
    minPrice: searchParams.get('minPrice') || '',
    maxPrice: searchParams.get('maxPrice') || '',
  })
  const page = parseInt(searchParams.get('page') || '0', 10)

  useEffect(() => {
    destinationService.list({ size: 100 }).then((res) => setDestinations(res.content)).catch(() => setDestinations([]))
  }, [])

  const runSearch = (params, pageNum = 0) => {
    setLoading(true)
    const query = Object.fromEntries(Object.entries(params).filter(([, v]) => v !== '' && v !== null))
    packageService.search({ ...query, page: pageNum, size: 9 })
      .then(setResult)
      .catch(() => setResult({ content: [], totalPages: 0 }))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    runSearch(filters, page)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams])

  const applyFilters = (e) => {
    e.preventDefault()
    const params = { ...filters, page: 0 }
    setSearchParams(Object.fromEntries(Object.entries(params).filter(([, v]) => v !== '')))
  }

  const goToPage = (p) => {
    setSearchParams({ ...Object.fromEntries(Object.entries(filters).filter(([, v]) => v !== '')), page: p })
  }

  return (
    <div className="page">
      <div className="container">
        <h1>Explore packages</h1>
        <form onSubmit={applyFilters} className="card" style={{ marginBottom: 28 }}>
          <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', marginBottom: 8 }}>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Package name</label>
              <input value={filters.name} onChange={(e) => setFilters({ ...filters, name: e.target.value })} placeholder="e.g. Kerala" />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Destination</label>
              <select value={filters.destinationId} onChange={(e) => setFilters({ ...filters, destinationId: e.target.value })}>
                <option value="">Any</option>
                {destinations.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
              </select>
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Min duration (days)</label>
              <input type="number" min="0" value={filters.minDuration} onChange={(e) => setFilters({ ...filters, minDuration: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Max duration (days)</label>
              <input type="number" min="0" value={filters.maxDuration} onChange={(e) => setFilters({ ...filters, maxDuration: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Min price (₹)</label>
              <input type="number" min="0" value={filters.minPrice} onChange={(e) => setFilters({ ...filters, minPrice: e.target.value })} />
            </div>
            <div className="field" style={{ marginBottom: 0 }}>
              <label>Max price (₹)</label>
              <input type="number" min="0" value={filters.maxPrice} onChange={(e) => setFilters({ ...filters, maxPrice: e.target.value })} />
            </div>
          </div>
          <button className="btn btn-primary btn-sm">Search</button>
        </form>

        {loading ? <Loading /> : !result || result.content.length === 0 ? (
          <EmptyState title="No packages match your filters" message="Try widening your price range or duration." />
        ) : (
          <>
            <div className="grid">
              {result.content.map((p) => (
                <Link to={`/packages/${p.id}`} className="pkg-card" key={p.id}>
                  <div className="pkg-card__media"><span>{p.destinationName}</span></div>
                  <div className="pkg-card__body">
                    <h4>{p.name}</h4>
                    <div className="pkg-card__meta">{p.durationDays} days · {p.travelType || 'Tour'}
                      {p.averageRating ? ` · ★ ${p.averageRating.toFixed(1)}` : ''}
                    </div>
                    <div className="pkg-card__price">{formatCurrency(p.basePrice)}<span className="muted" style={{ fontSize: '0.75rem' }}> / person</span></div>
                  </div>
                </Link>
              ))}
            </div>
            <Pagination page={result.page} totalPages={result.totalPages} onChange={goToPage} />
          </>
        )}
      </div>
    </div>
  )
}
