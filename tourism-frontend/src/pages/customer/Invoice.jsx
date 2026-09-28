import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { invoiceService } from '../../services/invoiceService'
import { formatCurrency, formatDate, formatDateTime } from '../../utils/format'
import Loading from '../../components/Loading.jsx'
import ErrorAlert from '../../components/ErrorAlert.jsx'
import { apiErrorMessage } from '../../services/api'

export default function Invoice() {
  const { bookingId } = useParams()
  const [invoice, setInvoice] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    invoiceService.getByBooking(bookingId).then(setInvoice).catch((err) => setError(apiErrorMessage(err, 'Invoice not available yet.')))
  }, [bookingId])

  if (error) return <div className="page container"><ErrorAlert message={error} /></div>
  if (!invoice) return <Loading />

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 620 }}>
        <div className="card" id="invoice-print">
          <div className="row between">
            <h2 style={{ margin: 0 }}>Invoice</h2>
            <span className="muted">{invoice.invoiceNumber}</span>
          </div>
          <p className="muted">{formatDateTime(invoice.invoiceDate)}</p>
          <hr className="hr" />
          <p><strong>Billed to:</strong> {invoice.customerName}</p>
          <p><strong>Booking:</strong> {invoice.bookingReference}</p>
          <p><strong>Package:</strong> {invoice.packageName}</p>
          <p><strong>Travel date:</strong> {formatDate(invoice.scheduleStartDate)}</p>
          <p><strong>Travelers:</strong> {invoice.travelerCount}</p>
          <hr className="hr" />
          <div className="row between"><span>Subtotal</span><span>{formatCurrency(invoice.subtotal)}</span></div>
          <div className="row between"><span>Discount</span><span>-{formatCurrency(invoice.discount)}</span></div>
          <div className="row between"><span>Charges</span><span>{formatCurrency(invoice.charges)}</span></div>
          <hr className="hr" />
          <div className="row between"><strong>Total</strong><strong style={{ fontSize: '1.2rem' }}>{formatCurrency(invoice.total)}</strong></div>
          <p className="muted" style={{ marginTop: 10 }}>Payment status: {invoice.paymentStatus}</p>
        </div>
        <button className="btn btn-outline" style={{ marginTop: 16 }} onClick={() => window.print()}>Print / Save as PDF</button>
      </div>
    </div>
  )
}
