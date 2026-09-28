import api from './api'

export const bookingService = {
  create: (payload) => api.post('/api/bookings', payload).then((r) => r.data),
  getById: (id) => api.get(`/api/bookings/${id}`).then((r) => r.data),
  getByReference: (ref) => api.get(`/api/bookings/reference/${ref}`).then((r) => r.data),
  myBookings: (params = {}) => api.get('/api/customer/bookings', { params }).then((r) => r.data),
  cancel: (id, payload) => api.put(`/api/bookings/${id}/cancel`, payload || {}).then((r) => r.data),
  changeStatus: (id, status) => api.patch(`/api/bookings/${id}/status`, { status }).then((r) => r.data),
  forPackage: (packageId) => api.get(`/api/operator/packages/${packageId}/bookings`).then((r) => r.data),
  all: () => api.get('/api/admin/bookings').then((r) => r.data),
  travelers: (bookingId) => api.get(`/api/operator/bookings/${bookingId}/travelers`).then((r) => r.data),
}
