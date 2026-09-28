import api from './api'

export const dashboardService = {
  customer: () => api.get('/api/customer/dashboard').then((r) => r.data),
  operator: () => api.get('/api/operator/dashboard').then((r) => r.data),
  admin: () => api.get('/api/admin/dashboard').then((r) => r.data),
}
