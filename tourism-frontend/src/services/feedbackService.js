import api from './api'

export const feedbackService = {
  submit: (payload) => api.post('/api/feedback', payload).then((r) => r.data),
  mine: () => api.get('/api/customer/feedback').then((r) => r.data),
  all: () => api.get('/api/admin/feedback').then((r) => r.data),
}
