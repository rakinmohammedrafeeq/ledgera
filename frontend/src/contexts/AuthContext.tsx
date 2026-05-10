import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import type { AuthResponse, User } from '@/types'
import {
  clearAuthState,
  getStoredAuthState,
  onAuthStateChange,
  persistAuthState,
  toUser,
} from '@/store/authStore'

interface AuthContextType {
  user: User | null
  token: string | null
  login: (authResponse: AuthResponse) => void
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
  // Member access (non-admin). Kept as isUser to avoid breaking existing consumers.
  isUser: boolean
  isReady: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    const hydrate = () => {
      const authState = getStoredAuthState()
      setUser(authState?.user ?? null)
      setToken(authState?.token ?? null)
      setIsReady(true)
    }

    hydrate()
    return onAuthStateChange(hydrate)
  }, [])

  const login = (authResponse: AuthResponse) => {
    const nextUser = toUser(authResponse)
    persistAuthState(authResponse.token, nextUser)
    setToken(authResponse.token)
    setUser(nextUser)
  }

  const logout = () => {
    clearAuthState()
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        logout,
        isAuthenticated: !!token,
        isAdmin: user?.role === 'ADMIN',
        isUser: user?.role === 'VIEWER' || user?.role === 'ANALYST',
        isReady,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
