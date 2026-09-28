import api from './api'

export const authService = {
  register: (payload) => api.post('/api/auth/register', payload).then((r) => r.data),
  login: (payload) => api.post('/api/auth/login', payload).then((r) => r.data),
  logout: () => api.post('/api/auth/logout').then((r) => r.data),
  myProfile: () => api.get('/api/profile').then((r) => r.data),
  updateProfile: (payload) => api.put('/api/profile', payload).then((r) => r.data),
}
