import api from './api'

export const reviewService = {
  submit: (payload) => api.post('/api/reviews', payload).then((r) => r.data),
  forPackage: (packageId, params = {}) => api.get(`/api/packages/${packageId}/reviews`, { params }).then((r) => r.data),
  moderate: (id, status) => api.patch(`/api/admin/reviews/${id}/status`, null, { params: { status } }).then((r) => r.data),
}
