import { createContext, useContext, useEffect, useState, useCallback } from 'react'
import { authService } from '../services/authService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('tpms_user')
    return raw ? JSON.parse(raw) : null
  })
  const [loading, setLoading] = useState(false)

  const persist = (authResponse) => {
    localStorage.setItem('tpms_token', authResponse.token)
    const userInfo = {
      id: authResponse.userId,
      username: authResponse.username,
      role: authResponse.role,
      fullName: authResponse.fullName,
    }
    localStorage.setItem('tpms_user', JSON.stringify(userInfo))
    setUser(userInfo)
    return userInfo
  }

  const login = useCallback(async (credentials) => {
    setLoading(true)
    try {
      const res = await authService.login(credentials)
      return persist(res)
    } finally {
      setLoading(false)
    }
  }, [])

  const register = useCallback(async (payload) => {
    setLoading(true)
    try {
      const res = await authService.register(payload)
      return persist(res)
    } finally {
      setLoading(false)
    }
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('tpms_token')
    localStorage.removeItem('tpms_user')
    setUser(null)
  }, [])

  useEffect(() => {
    // Keep multiple tabs roughly in sync.
    const handler = (e) => {
      if (e.key === 'tpms_user') {
        setUser(e.newValue ? JSON.parse(e.newValue) : null)
      }
    }
    window.addEventListener('storage', handler)
    return () => window.removeEventListener('storage', handler)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
