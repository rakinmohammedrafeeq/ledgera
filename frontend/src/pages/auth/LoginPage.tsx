import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import {
  ArrowLeft,
  ArrowRight,
  Eye,
  EyeOff,
  Loader2,
  Lock,
  Mail,
} from 'lucide-react'
import { APP_LOGO_SRC } from '@/config/brandAssets'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { LedgeraSpinner } from '@/components/ui/fintrix-spinner'
import { useAuth } from '@/contexts/AuthContext'
import { useLoginMutation } from '@/hooks'
import { buildAuthRoute } from '@/lib/authRoleFlow'
import { getDefaultRouteByRole } from '@/lib/routeUtils'
import { getRoleLabel } from '@/lib/roleUtils'

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
})

type LoginFormData = z.infer<typeof loginSchema>

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login } = useAuth()
  const [showPassword, setShowPassword] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const mutation = useLoginMutation()

  const onSubmit = (data: LoginFormData) => {
    mutation.mutate(data, {
      onSuccess: (authResponse) => {
        login(authResponse)
        toast.success(`Welcome back, ${getRoleLabel(authResponse.role)}!`)

        const fromPath = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname
        const safeReturnPath = fromPath && fromPath.startsWith('/app/') ? fromPath : null

        navigate(safeReturnPath ?? getDefaultRouteByRole(), { replace: true })
      },
      onError: () => {
        toast.error('Invalid email or password')
      },
    })
  }

  return (
    <div className="relative mx-auto w-full max-w-md animate-in fade-in slide-in-from-bottom-4 duration-500">
      {mutation.isPending && (
        <div className="absolute inset-0 z-50 grid place-items-center rounded-2xl bg-black/50 p-6 backdrop-blur">
          <div className="w-full max-w-sm rounded-2xl border border-white/[0.08] bg-black/70 p-6 text-center shadow-2xl">
            <LedgeraSpinner size={48} className="mx-auto" alt="Signing you in" />
            <div className="mt-4 space-y-1.5">
              <p className="text-sm font-medium text-white">
                Signing you in…
              </p>
              <p className="text-xs leading-relaxed text-white/50">
                If the backend is cold-starting, this may take a moment.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Back link */}
      <Link
        to="/"
        className="mb-6 inline-flex items-center gap-1.5 text-xs font-medium text-white/35 transition-colors hover:text-white/60"
      >
        <ArrowLeft className="h-3.5 w-3.5" />
        Back to home
      </Link>

      {/* ── Header ───────────────────────────────────────────── */}
      <div className="mb-8 space-y-4 text-center">
        <img src={APP_LOGO_SRC} alt="Ledgera" className="mx-auto h-14 w-14" loading="eager" />
        <div className="space-y-1.5">
          <h1 className="text-2xl font-bold tracking-tight text-white">
            Welcome back
          </h1>
          <p className="text-sm text-white/40">
            Sign in to your workspace
          </p>
        </div>
      </div>

      {/* ── Form ─────────────────────────────────────────────── */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
        {/* Email */}
        <div className="space-y-1.5">
          <Label htmlFor="email" className="text-sm font-medium text-white/60">
            Email
          </Label>
          <div className="relative">
            <Mail className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/25" />
            <Input
              id="email"
              type="email"
              placeholder="you@example.com"
              autoComplete="email"
              className="h-11 border-white/[0.08] bg-white/[0.03] pl-10 text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30 transition-colors"
              {...register('email')}
            />
          </div>
          {errors.email && (
            <p className="flex items-center gap-1 text-xs text-red-400">
              <span className="inline-block h-1 w-1 rounded-full bg-red-400" />
              {errors.email.message}
            </p>
          )}
        </div>

        {/* Password */}
        <div className="space-y-1.5">
          <div className="flex items-center justify-between">
            <Label htmlFor="password" className="text-sm font-medium text-white/60">
              Password
            </Label>
            <Link
              to="/forgot-password"
              className="text-xs font-medium text-white/35 transition-colors hover:text-white/60"
            >
              Forgot password?
            </Link>
          </div>
          <div className="relative">
            <Lock className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/25" />
            <Input
              id="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="Enter your password"
              autoComplete="current-password"
              className="h-11 border-white/[0.08] bg-white/[0.03] pl-10 pr-11 text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30 transition-colors"
              {...register('password')}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 rounded p-0.5 text-white/25 transition-colors hover:text-white/60"
              aria-label={showPassword ? 'Hide password' : 'Show password'}
            >
              {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </button>
          </div>
          {errors.password && (
            <p className="flex items-center gap-1 text-xs text-red-400">
              <span className="inline-block h-1 w-1 rounded-full bg-red-400" />
              {errors.password.message}
            </p>
          )}
        </div>

        {/* Submit */}
        <Button
          type="submit"
          disabled={mutation.isPending}
          className="mt-2 h-11 w-full rounded-lg bg-primary text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90 hover:shadow-md hover:shadow-primary/15"
        >
          {mutation.isPending ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <span className="inline-flex items-center gap-2">
              Sign in
              <ArrowRight className="h-4 w-4" />
            </span>
          )}
        </Button>
      </form>

      {/* ── Footer ───────────────────────────────────────────── */}
      <p className="mt-6 text-center text-sm text-white/35">
        Don&apos;t have an account?{' '}
        <Link
          to={buildAuthRoute('/register', 'VIEWER')}
          className="font-medium text-primary/80 transition-colors hover:text-primary"
        >
          Create account
        </Link>
      </p>
    </div>
  )
}
