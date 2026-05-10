import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { ArrowLeft, Eye, EyeOff, Loader2 } from 'lucide-react'
import { APP_LOGO_SRC } from '@/config/brandAssets'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { LedgeraSpinner } from '@/components/ui/fintrix-spinner'
import { useAuth } from '@/contexts/AuthContext'
import { useRegisterMutation } from '@/hooks'

const registerSchema = z
  .object({
    name: z.string().min(2, 'Name must be at least 2 characters'),
    email: z.string().email('Please enter a valid email address'),
    password: z.string().min(6, 'Password must be at least 6 characters'),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  })

type RegisterFormData = z.infer<typeof registerSchema>

export function RegisterPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [showPassword, setShowPassword] = useState(false)

  const {
    register: registerField,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  const mutation = useRegisterMutation()

  const onSubmit = (data: RegisterFormData) => {
    mutation.mutate(
      {
        name: data.name,
        email: data.email,
        password: data.password,
      },
      {
        onSuccess: (authResponse) => {
          login(authResponse)
          toast.success('Account created successfully!')
          navigate('/app/dashboard', { replace: true })
        },
        onError: () => {
          toast.error('Failed to create account. Please try again.')
        },
      },
    )
  }

  return (
    <div className="relative mx-auto w-full max-w-md space-y-7 animate-in fade-in slide-in-from-bottom-4 duration-500">
      {mutation.isPending && (
        <div className="absolute inset-0 z-50 grid place-items-center rounded-2xl bg-black/50 p-6 backdrop-blur">
          <div className="w-full max-w-sm rounded-2xl border border-white/[0.08] bg-black/70 p-6 text-center shadow-2xl">
            <LedgeraSpinner size={48} className="mx-auto" alt="Creating your account" />
            <div className="mt-4 space-y-1.5">
              <p className="text-sm font-medium text-white">
                Creating your account…
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
        className="inline-flex items-center gap-1.5 text-xs font-medium text-white/35 transition-colors hover:text-white/60"
      >
        <ArrowLeft className="h-3.5 w-3.5" />
        Back to home
      </Link>

      {/* ── Header ───────────────────────────────────────────── */}
      <div className="space-y-3 text-center">
        <img src={APP_LOGO_SRC} alt="Ledgera" className="mx-auto h-12 w-12" loading="eager" />
        <div className="space-y-1.5">
          <h1 className="text-2xl font-bold tracking-tight text-white">Create your account</h1>
          <p className="text-sm text-white/40">
            Get started with your personal workspace.
          </p>
        </div>
      </div>

      {/* ── Form ─────────────────────────────────────────────── */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
        <div className="space-y-3.5">
          <div className="space-y-1.5">
            <Label htmlFor="name" className="text-sm font-medium text-white/60">Full name</Label>
            <Input
              id="name"
              type="text"
              placeholder="Your name"
              autoComplete="name"
              className="h-11 border-white/[0.08] bg-white/[0.03] text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30"
              {...registerField('name')}
            />
            {errors.name && (
              <p className="text-xs text-red-400">{errors.name.message}</p>
            )}
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="email" className="text-sm font-medium text-white/60">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="you@example.com"
              autoComplete="email"
              className="h-11 border-white/[0.08] bg-white/[0.03] text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30"
              {...registerField('email')}
            />
            {errors.email && (
              <p className="text-xs text-red-400">{errors.email.message}</p>
            )}
          </div>

          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <Label htmlFor="password" className="text-sm font-medium text-white/60">Password</Label>
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="inline-flex items-center gap-1 text-xs text-white/35 hover:text-white/60"
              >
                {showPassword ? <EyeOff className="h-3 w-3" /> : <Eye className="h-3 w-3" />}
                {showPassword ? 'Hide' : 'Show'}
              </button>
            </div>
            <Input
              id="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="At least 6 characters"
              autoComplete="new-password"
              className="h-11 border-white/[0.08] bg-white/[0.03] text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30"
              {...registerField('password')}
            />
            {errors.password && (
              <p className="text-xs text-red-400">{errors.password.message}</p>
            )}
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="confirmPassword" className="text-sm font-medium text-white/60">Confirm password</Label>
            <Input
              id="confirmPassword"
              type={showPassword ? 'text' : 'password'}
              placeholder="Confirm your password"
              autoComplete="new-password"
              className="h-11 border-white/[0.08] bg-white/[0.03] text-white placeholder:text-white/20 focus-visible:border-white/15 focus-visible:ring-primary/30"
              {...registerField('confirmPassword')}
            />
            {errors.confirmPassword && (
              <p className="text-xs text-red-400">{errors.confirmPassword.message}</p>
            )}
          </div>
        </div>

        <Button
          type="submit"
          className="h-11 w-full rounded-lg bg-primary text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90 hover:shadow-md hover:shadow-primary/15"
          disabled={mutation.isPending}
        >
          {mutation.isPending && (
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
          )}
          Create account
        </Button>
      </form>

      <p className="text-center text-sm text-white/35">
        Already have an account?{' '}
        <Link
          to="/login"
          className="font-medium text-primary/80 transition-colors hover:text-primary"
        >
          Sign in
        </Link>
      </p>
    </div>
  )
}
