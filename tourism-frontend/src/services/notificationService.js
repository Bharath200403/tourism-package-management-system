import api from './api'

export const notificationService = {
  mine: () => api.get('/api/notifications').then((r) => r.data),
  markRead: (id) => api.patch(`/api/notifications/${id}/read`).then((r) => r.data),
}
