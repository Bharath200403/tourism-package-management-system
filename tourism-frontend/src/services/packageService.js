import api from './api'

export const packageService = {
  search: (params = {}) => api.get('/api/packages', { params }).then((r) => r.data),
  get: (id) => api.get(`/api/packages/${id}`).then((r) => r.data),
  schedules: (id) => api.get(`/api/packages/${id}/schedules`).then((r) => r.data),
  create: (payload) => api.post('/api/packages', payload).then((r) => r.data),
  update: (id, payload) => api.put(`/api/packages/${id}`, payload).then((r) => r.data),
  deactivate: (id) => api.delete(`/api/packages/${id}`).then((r) => r.data),
  addItineraryDay: (id, payload) => api.post(`/api/packages/${id}/itinerary`, payload).then((r) => r.data),
  removeItineraryDay: (id, itineraryId) => api.delete(`/api/packages/${id}/itinerary/${itineraryId}`).then((r) => r.data),
  addSchedule: (id, payload) => api.post(`/api/packages/${id}/schedules`, payload).then((r) => r.data),
  updateSchedule: (scheduleId, payload) => api.put(`/api/schedules/${scheduleId}`, payload).then((r) => r.data),
  setScheduleStatus: (scheduleId, status) => api.patch(`/api/schedules/${scheduleId}/status`, null, { params: { status } }).then((r) => r.data),
  reviews: (id, params = {}) => api.get(`/api/packages/${id}/reviews`, { params }).then((r) => r.data),
}
