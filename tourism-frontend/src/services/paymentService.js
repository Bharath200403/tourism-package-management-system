import api from './api'

export const paymentService = {
  simulate: (payload) => api.post('/api/payments/simulate', payload).then((r) => r.data),
}
