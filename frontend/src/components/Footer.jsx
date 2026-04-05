import { Link } from 'react-router-dom';
import { Coffee } from 'lucide-react';
import Logo from './Logo';
import { IconGithub, IconLinkedin } from './SocialOutlineIcons';
import './Footer.css';

const socialLinks = [
  {
    href: 'https://github.com/rakinmohammedrafeeq',
    label: 'GitHub',
    title: 'View my projects',
    Icon: IconGithub,
  },
  {
    href: 'https://www.linkedin.com/in/rakinmohammedrafeeq/',
    label: 'LinkedIn',
    title: 'Connect with me',
    Icon: IconLinkedin,
  },
  {
    href: 'https://buymeacoffee.com/rakinmohammedrafeeq',
    label: 'Buy Me a Coffee',
    title: 'Support my work',
    Icon: Coffee,
  },
];

export default function Footer() {
  return (
    <footer className="app-footer">
      <div className="app-footer__inner">
        <div className="app-footer__brand-block">
          <Link to="/" className="app-footer__logo-row">
            <Logo size="medium" />
            <span className="app-footer__name">Ledgera</span>
          </Link>
          <p className="app-footer__desc">
            A powerful platform to track, manage, and optimize your financial life with precision.
          </p>
          <div className="app-footer__social">
            {socialLinks.map(({ href, label, title, Icon }) => (
              <a
                key={label}
                href={href}
                className="app-footer__social-link"
                aria-label={label}
                title={title}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Icon strokeWidth={1.5} size={19} aria-hidden />
              </a>
            ))}
          </div>
        </div>
      </div>

      <div className="app-footer__bar">
        <span>© 2026 Ledgera</span>
        <span className="app-footer__bar-sep" aria-hidden>
          ·
        </span>
        <Link to="/privacy">Privacy</Link>
        <span className="app-footer__bar-sep" aria-hidden>
          ·
        </span>
        <Link to="/terms">Terms</Link>
        <span className="app-footer__bar-sep" aria-hidden>
          ·
        </span>
        <Link to="/contact">Contact</Link>
      </div>
    </footer>
  );
}
