import { useEffect, useState } from 'react'
import { reportService } from '../../services/reportService'
import { formatDateTime } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import Pagination from '../../components/Pagination.jsx'

export default function AuditLogs() {
  const [result, setResult] = useState(null)
  const [page, setPage] = useState(0)

  useEffect(() => {
    reportService.auditLogs({ page, size: 25 }).then(setResult).catch(() => setResult({ content: [], totalPages: 0 }))
  }, [page])

  if (!result) return <Loading />

  return (
    <div className="page">
      <div className="container">
        <h1>Audit logs</h1>
        <div className="table-wrap">
          <table>
            <thead><tr><th>When</th><th>Actor</th><th>Action</th><th>Entity</th><th>Details</th></tr></thead>
            <tbody>
              {result.content.map((log) => (
                <tr key={log.id}>
                  <td>{formatDateTime(log.createdAt)}</td>
                  <td>{log.actorUsername}</td>
                  <td>{log.action}</td>
                  <td>{log.entityType} {log.entityId ? `#${log.entityId}` : ''}</td>
                  <td>{log.details}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Pagination page={result.page} totalPages={result.totalPages} onChange={setPage} />
      </div>
    </div>
  )
}
