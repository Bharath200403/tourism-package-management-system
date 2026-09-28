import api from './api'

export const reportService = {
  bookings: () => api.get('/api/admin/reports/bookings').then((r) => r.data),
  revenue: () => api.get('/api/admin/reports/revenue').then((r) => r.data),
  packages: () => api.get('/api/admin/reports/packages').then((r) => r.data),
  customers: () => api.get('/api/admin/reports/customers').then((r) => r.data),
  cancellations: () => api.get('/api/admin/reports/cancellations').then((r) => r.data),
  occupancy: () => api.get('/api/admin/reports/occupancy').then((r) => r.data),
  auditLogs: (params = {}) => api.get('/api/admin/audit-logs', { params }).then((r) => r.data),
}
