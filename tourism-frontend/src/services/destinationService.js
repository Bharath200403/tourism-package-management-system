import api from './api'

export const destinationService = {
  list: (params = {}) => api.get('/api/destinations', { params }).then((r) => r.data),
  get: (id) => api.get(`/api/destinations/${id}`).then((r) => r.data),
  create: (payload) => api.post('/api/destinations', payload).then((r) => r.data),
  update: (id, payload) => api.put(`/api/destinations/${id}`, payload).then((r) => r.data),
  deactivate: (id) => api.delete(`/api/destinations/${id}`).then((r) => r.data),
}
