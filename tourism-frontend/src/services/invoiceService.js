import api from './api'

export const invoiceService = {
  getById: (id) => api.get(`/api/invoices/${id}`).then((r) => r.data),
  getByBooking: (bookingId) => api.get(`/api/invoices/by-booking/${bookingId}`).then((r) => r.data),
}
