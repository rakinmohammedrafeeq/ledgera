import { Link, useLocation } from 'react-router-dom'
import { LayoutDashboard, Receipt, Users, LogOut } from 'lucide-react'
import { APP_LOGO_SRC } from '@/config/brandAssets'
import { cn } from '@/lib/utils'
import { useAuth } from '@/contexts/AuthContext'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Separator } from '@/components/ui/separator'
import { getInitials } from '@/lib/utils'
import { getRoleLabel } from '@/lib/roleUtils'
import { LogoutConfirm } from '@/components/auth/LogoutConfirm'

const mainNav = [
  { title: 'Dashboard', href: '/app/dashboard', icon: LayoutDashboard },
  { title: 'Records', href: '/app/records', icon: Receipt },
] as const

const teamNav = [{ title: 'Team', href: '/app/team', icon: Users }] as const

interface AppSidebarProps {
  onClose?: () => void
}

export function AppSidebar({ onClose }: AppSidebarProps) {
  const location = useLocation()
  const { user } = useAuth()
  const showTeam = user?.role === 'ANALYST' || user?.role === 'ADMIN'

  const handleNavClick = () => {
    onClose?.()
  }

  const isActive = (href: string) =>
    location.pathname === href || (href !== '/app/dashboard' && location.pathname.startsWith(href))

  return (
    <div className="flex h-full flex-col bg-sidebar text-sidebar-foreground">
      <ScrollArea className="h-full" viewportClassName="scrollbar-none" scrollbarClassName="hidden">
        <div className="flex min-h-full flex-col">
          {/* ── Logo ─────────────────────────────────────────── */}
          <div className="flex h-16 items-center gap-2.5 px-5">
            <img src={APP_LOGO_SRC} alt="Ledgera" className="h-8 w-8" loading="eager" />
            <span className="text-lg font-semibold tracking-tight">Ledgera</span>
          </div>

          <Separator className="bg-sidebar-border" />

          {/* ── Navigation ───────────────────────────────────── */}
          <div className="px-3 py-5">
            <div className="space-y-6">
              <div className="space-y-1">
                <p className="px-3 pb-1 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/40">
                  Main
                </p>
                <nav className="space-y-0.5">
                  {mainNav.map((item) => {
                    const active = isActive(item.href)
                    return (
                      <Link
                        key={item.href}
                        to={item.href}
                        onClick={handleNavClick}
                        className={cn(
                          'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-150',
                          active
                            ? 'bg-sidebar-accent text-sidebar-foreground'
                            : 'text-sidebar-foreground/60 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground',
                        )}
                      >
                        <item.icon
                          className={cn(
                            'h-[18px] w-[18px] transition-colors duration-150',
                            active ? 'text-primary' : 'text-sidebar-foreground/50 group-hover:text-primary/70',
                          )}
                        />
                        {item.title}
                        {active && (
                          <div className="ml-auto h-1.5 w-1.5 rounded-full bg-primary" />
                        )}
                      </Link>
                    )
                  })}
                </nav>
              </div>

              {showTeam ? (
                <div className="space-y-1">
                  <p className="px-3 pb-1 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/40">
                    Organization
                  </p>
                  <nav className="space-y-0.5">
                    {teamNav.map((item) => {
                      const active = isActive(item.href)
                      return (
                        <Link
                          key={item.href}
                          to={item.href}
                          onClick={handleNavClick}
                          className={cn(
                            'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-150',
                            active
                              ? 'bg-sidebar-accent text-sidebar-foreground'
                              : 'text-sidebar-foreground/60 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground',
                          )}
                        >
                          <item.icon
                            className={cn(
                              'h-[18px] w-[18px] transition-colors duration-150',
                              active ? 'text-primary' : 'text-sidebar-foreground/50 group-hover:text-primary/70',
                            )}
                          />
                          {item.title}
                          {active && (
                            <div className="ml-auto h-1.5 w-1.5 rounded-full bg-primary" />
                          )}
                        </Link>
                      )
                    })}
                  </nav>
                </div>
              ) : null}
            </div>
          </div>

          {/* ── User card ────────────────────────────────────── */}
          <div className="mt-auto">
            <Separator className="bg-sidebar-border" />

            <div className="p-4">
              <div className="flex items-center gap-3">
                <Avatar className="h-9 w-9">
                  <AvatarFallback className="bg-primary/15 text-primary text-sm font-semibold">
                    {user?.name ? getInitials(user.name) : 'U'}
                  </AvatarFallback>
                </Avatar>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">{user?.name || 'User'}</p>
                  <p className="truncate text-xs text-sidebar-foreground/45">{user?.email}</p>
                </div>
              </div>

              <div className="mt-3">
                <LogoutConfirm
                  onLoggedOut={onClose}
                  trigger={
                    <Button
                      variant="ghost"
                      className={cn(
                        'w-full justify-start gap-2 rounded-lg px-3 py-2',
                        'text-[13px] font-medium',
                        'text-sidebar-foreground/50',
                        'transition-all duration-150',
                        'hover:bg-red-500/10 hover:text-red-400',
                      )}
                    >
                      <LogOut className="h-4 w-4" />
                      <span>Sign out</span>
                    </Button>
                  }
                  title="Sign out?"
                  description="Are you sure you want to sign out?"
                  confirmText="Sign out"
                />
              </div>
            </div>
          </div>
        </div>
      </ScrollArea>
    </div>
  )
}
