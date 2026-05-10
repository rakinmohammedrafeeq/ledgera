import { Menu, Search, Moon, Sun } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { useAuth } from '@/contexts/AuthContext'
import { useTheme } from '@/contexts/ThemeContext'
import { getInitials } from '@/lib/utils'
import { getRoleLabel } from '@/lib/roleUtils'
import { cn } from '@/lib/utils'
import { LogoutConfirm } from '@/components/auth/LogoutConfirm'

interface NavbarProps {
  onMenuClick: () => void
}

export function Navbar({ onMenuClick }: NavbarProps) {
  const { user } = useAuth()
  const { toggleTheme, isDark } = useTheme()

  return (
    <header className="sticky top-0 z-40 flex h-14 items-center gap-4 border-b border-border/50 bg-background/80 px-4 backdrop-blur-lg md:px-6">
      {/* Mobile Menu Button */}
      <Button
        variant="ghost"
        size="icon"
        className="md:hidden"
        onClick={onMenuClick}
      >
        <Menu className="h-5 w-5" />
        <span className="sr-only">Toggle menu</span>
      </Button>

      {/* Search */}
      <div className="hidden flex-1 md:flex">
        <div className="relative w-full max-w-sm">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground/60" />
          <Input
            type="search"
            placeholder="Search…"
            className="h-9 w-full rounded-lg border-border/50 bg-muted/40 pl-9 text-sm placeholder:text-muted-foreground/50 focus-visible:ring-primary/30"
          />
        </div>
      </div>

      {/* Mobile spacer */}
      <div className="flex-1 md:hidden" />

      {/* Right Side */}
      <div className="flex items-center gap-1">
        {/* Mobile Search */}
        <Button variant="ghost" size="icon" className="h-9 w-9 md:hidden">
          <Search className="h-4 w-4" />
          <span className="sr-only">Search</span>
        </Button>

        {/* Theme Toggle */}
        <Button
          id="theme-toggle"
          variant="ghost"
          size="icon"
          onClick={toggleTheme}
          aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
          className="relative h-9 w-9 overflow-hidden transition-colors duration-150 hover:bg-muted"
        >
          <Sun
            className={cn(
              'absolute h-4 w-4 transition-all duration-300',
              isDark
                ? 'rotate-0 scale-100 opacity-100'
                : 'rotate-90 scale-0 opacity-0',
            )}
          />
          <Moon
            className={cn(
              'absolute h-4 w-4 transition-all duration-300',
              isDark
                ? '-rotate-90 scale-0 opacity-0'
                : 'rotate-0 scale-100 opacity-100',
            )}
          />
        </Button>

        {/* User Menu */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              type="button"
              variant="ghost"
              className="relative h-9 w-9 rounded-full ring-1 ring-border/50 transition-all hover:ring-primary/30"
            >
              <Avatar className="h-8 w-8">
                <AvatarFallback className="bg-primary/12 text-primary text-xs font-semibold">
                  {user?.name ? getInitials(user.name) : 'U'}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel className="font-normal">
              <div className="flex flex-col space-y-1 min-w-0 overflow-hidden">
                <p className="text-sm font-medium truncate">{user?.name || 'User'}</p>
                <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
                <p className="text-[11px] font-medium text-primary/80 truncate">{getRoleLabel(user?.role)}</p>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <LogoutConfirm
              trigger={
                <DropdownMenuItem onSelect={(e) => e.preventDefault()} className="text-destructive focus:text-destructive cursor-pointer">
                  Sign out
                </DropdownMenuItem>
              }
              title="Sign out?"
              description="Are you sure you want to sign out?"
              confirmText="Sign out"
            />
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  )
}
