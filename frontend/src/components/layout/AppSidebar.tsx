import { Link, useLocation } from 'react-router-dom'
import { LayoutDashboard, Receipt, Users, LogOut, ChevronLeft, ChevronRight } from 'lucide-react'
import { APP_LOGO_SRC } from '@/config/brandAssets'
import { cn } from '@/lib/utils'
import { useAuth } from '@/contexts/AuthContext'
import { useSidebar } from '@/contexts/SidebarContext'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Separator } from '@/components/ui/separator'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { getInitials } from '@/lib/utils'
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
  const { isCollapsed, toggleSidebar } = useSidebar()
  const showTeam = user?.role === 'ANALYST' || user?.role === 'ADMIN'

  const handleNavClick = () => {
    onClose?.()
  }

  const isActive = (href: string) =>
    location.pathname === href || (href !== '/app/dashboard' && location.pathname.startsWith(href))

  return (
    <TooltipProvider delayDuration={0}>
      <div className="flex h-full flex-col bg-sidebar text-sidebar-foreground">
        <ScrollArea className="h-full" viewportClassName="scrollbar-none" scrollbarClassName="hidden">
          <div className="flex min-h-full flex-col">
            {/* ── Logo & Toggle ────────────────────────────────── */}
            <div className="flex h-16 items-center px-4 gap-3">
              <div className="flex items-center gap-2.5 min-w-0 overflow-hidden flex-1">
                <img src={APP_LOGO_SRC} alt="Ledgera" className="h-8 w-8 flex-shrink-0" loading="eager" />
                {!isCollapsed && (
                  <span className="text-lg font-semibold tracking-tight whitespace-nowrap overflow-hidden text-ellipsis">
                    Ledgera
                  </span>
                )}
              </div>
              {!isCollapsed && (
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={toggleSidebar}
                  className="h-7 w-7 flex-shrink-0 text-sidebar-foreground/60 hover:text-sidebar-foreground hover:bg-sidebar-accent"
                >
                  <ChevronLeft className="h-4 w-4" />
                </Button>
              )}
            </div>

            <Separator className="bg-sidebar-border" />

            {/* ── Navigation ───────────────────────────────────── */}
            <div className="px-3 py-5">
              <div className="space-y-6">
                <div className="space-y-1">
                  {!isCollapsed && (
                    <p className="px-3 pb-1 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/40">
                      Main
                    </p>
                  )}
                  <nav className="space-y-0.5">
                    {mainNav.map((item) => {
                      const active = isActive(item.href)
                      const navItem = (
                        <Link
                          key={item.href}
                          to={item.href}
                          onClick={handleNavClick}
                          className={cn(
                            'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-150',
                            active
                              ? 'bg-sidebar-accent text-sidebar-foreground'
                              : 'text-sidebar-foreground/60 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground',
                            isCollapsed && 'justify-center',
                          )}
                        >
                          <item.icon
                            className={cn(
                              'h-[18px] w-[18px] flex-shrink-0 transition-colors duration-150',
                              active ? 'text-primary' : 'text-sidebar-foreground/50 group-hover:text-primary/70',
                            )}
                          />
                          {!isCollapsed && (
                            <>
                              <span className="whitespace-nowrap">{item.title}</span>
                              {active && (
                                <div className="ml-auto h-1.5 w-1.5 rounded-full bg-primary flex-shrink-0" />
                              )}
                            </>
                          )}
                        </Link>
                      )

                      return isCollapsed ? (
                        <Tooltip key={item.href}>
                          <TooltipTrigger asChild>{navItem}</TooltipTrigger>
                          <TooltipContent side="right">{item.title}</TooltipContent>
                        </Tooltip>
                      ) : (
                        navItem
                      )
                    })}
                  </nav>
                </div>

                {showTeam ? (
                  <div className="space-y-1">
                    {!isCollapsed && (
                      <p className="px-3 pb-1 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/40">
                        Organization
                      </p>
                    )}
                    <nav className="space-y-0.5">
                      {teamNav.map((item) => {
                        const active = isActive(item.href)
                        const navItem = (
                          <Link
                            key={item.href}
                            to={item.href}
                            onClick={handleNavClick}
                            className={cn(
                              'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-150',
                              active
                                ? 'bg-sidebar-accent text-sidebar-foreground'
                                : 'text-sidebar-foreground/60 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground',
                              isCollapsed && 'justify-center',
                            )}
                          >
                            <item.icon
                              className={cn(
                                'h-[18px] w-[18px] flex-shrink-0 transition-colors duration-150',
                                active ? 'text-primary' : 'text-sidebar-foreground/50 group-hover:text-primary/70',
                              )}
                            />
                            {!isCollapsed && (
                              <>
                                <span className="whitespace-nowrap">{item.title}</span>
                                {active && (
                                  <div className="ml-auto h-1.5 w-1.5 rounded-full bg-primary flex-shrink-0" />
                                )}
                              </>
                            )}
                          </Link>
                        )

                        return isCollapsed ? (
                          <Tooltip key={item.href}>
                            <TooltipTrigger asChild>{navItem}</TooltipTrigger>
                            <TooltipContent side="right">{item.title}</TooltipContent>
                          </Tooltip>
                        ) : (
                          navItem
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
                {isCollapsed ? (
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <div className="flex justify-center">
                        <Avatar className="h-9 w-9 flex-shrink-0">
                          <AvatarFallback className="bg-primary/15 text-primary text-sm font-semibold">
                            {user?.name ? getInitials(user.name) : 'U'}
                          </AvatarFallback>
                        </Avatar>
                      </div>
                    </TooltipTrigger>
                    <TooltipContent side="right">
                      <div className="text-left">
                        <p className="font-medium">{user?.name || 'User'}</p>
                        <p className="text-xs text-muted-foreground">{user?.email}</p>
                      </div>
                    </TooltipContent>
                  </Tooltip>
                ) : (
                  <div className="flex items-start gap-3 min-w-0 overflow-hidden pr-2">
                    <Avatar className="h-9 w-9 flex-shrink-0 mt-0.5">
                      <AvatarFallback className="bg-primary/15 text-primary text-sm font-semibold">
                        {user?.name ? getInitials(user.name) : 'U'}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 min-w-0 overflow-hidden">
                      <p className="text-sm font-medium leading-snug break-words">
                        {user?.name || 'User'}
                      </p>
                      <p className="text-xs text-sidebar-foreground/45 leading-snug break-all">
                        {user?.email}
                      </p>
                    </div>
                  </div>
                )}

                <div className="mt-3">
                  <LogoutConfirm
                    onLoggedOut={onClose}
                    trigger={
                      isCollapsed ? (
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <Button
                              variant="ghost"
                              size="icon"
                              className={cn(
                                'w-full h-9 rounded-lg',
                                'text-sidebar-foreground/50',
                                'transition-all duration-150',
                                'hover:bg-red-500/10 hover:text-red-400',
                              )}
                            >
                              <LogOut className="h-4 w-4" />
                            </Button>
                          </TooltipTrigger>
                          <TooltipContent side="right">Sign out</TooltipContent>
                        </Tooltip>
                      ) : (
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
                          <LogOut className="h-4 w-4 flex-shrink-0" />
                          <span>Sign out</span>
                        </Button>
                      )
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
        
        {/* ── Collapse Toggle (Collapsed State) ──────────────────────── */}
        {isCollapsed && (
          <Button
            variant="ghost"
            size="icon"
            onClick={toggleSidebar}
            className={cn(
              'absolute -right-3 top-4 z-50',
              'h-6 w-6 rounded-full',
              'bg-sidebar-accent border border-sidebar-border',
              'text-sidebar-foreground/60 hover:text-sidebar-foreground',
              'hover:bg-sidebar-accent/80',
              'shadow-md hover:shadow-lg',
              'transition-all duration-200',
            )}
          >
            <ChevronRight className="h-3.5 w-3.5" />
          </Button>
        )}
      </div>
    </TooltipProvider>
  )
}
