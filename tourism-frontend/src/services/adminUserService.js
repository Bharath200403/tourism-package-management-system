import api from './api'

export const adminUserService = {
  customers: () => api.get('/api/admin/customers').then((r) => r.data),
  operators: () => api.get('/api/admin/operators').then((r) => r.data),
  createOperator: (payload) => api.post('/api/admin/operators', payload).then((r) => r.data),
  createAdmin: (payload) => api.post('/api/admin/admins', payload).then((r) => r.data),
  setStatus: (id, status) => api.patch(`/api/admin/users/${id}/status`, null, { params: { status } }).then((r) => r.data),
}
