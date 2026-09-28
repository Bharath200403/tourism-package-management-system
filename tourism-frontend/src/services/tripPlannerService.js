import api from './api'

export const tripPlannerService = {
  plan: (payload) => api.post('/api/trip-planner/plan', payload).then((r) => r.data),
}
