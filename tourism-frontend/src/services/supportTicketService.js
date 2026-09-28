import api from './api'

export const supportTicketService = {
  create: (payload) => api.post('/api/support-tickets', payload).then((r) => r.data),
  mine: () => api.get('/api/customer/support-tickets').then((r) => r.data),
  all: () => api.get('/api/admin/support-tickets').then((r) => r.data),
  update: (id, payload) => api.patch(`/api/admin/support-tickets/${id}`, payload).then((r) => r.data),
}
