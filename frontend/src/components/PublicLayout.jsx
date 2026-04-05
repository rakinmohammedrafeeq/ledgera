import { Outlet, Link, useLocation } from 'react-router-dom';
import ThemeToggle from './ThemeToggle';
import './PublicLayout.css';

export default function PublicLayout() {
  const { pathname } = useLocation();
  const showLoginFooter = pathname === '/login';

  return (
    <div className="public-layout">
      <div className="public-layout__theme-corner">
        <ThemeToggle />
      </div>
      <div className="public-layout__main">
        <Outlet />
      </div>
      {showLoginFooter && (
        <footer className="public-layout__footer">
          <span className="public-layout__footer-copy">© 2026 Ledgera</span>
          <span className="public-layout__footer-sep" aria-hidden>
            •
          </span>
          <Link to="/privacy">Privacy</Link>
          <span className="public-layout__footer-sep" aria-hidden>
            •
          </span>
          <Link to="/terms">Terms</Link>
          <span className="public-layout__footer-sep" aria-hidden>
            •
          </span>
          <Link to="/contact">Contact</Link>
        </footer>
      )}
    </div>
  );
}
