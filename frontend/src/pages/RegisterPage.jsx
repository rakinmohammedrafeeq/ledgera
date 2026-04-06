import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import BackendLoadingOverlay from '../components/BackendLoadingOverlay';
import useBackendReady from '../hooks/useBackendReady';
import Logo from '../components/Logo';
import AuthBrandingFeatures from '../components/AuthBrandingFeatures';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'ANALYST',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();
  const { showLoading, showFallback, runWithBackendReady } = useBackendReady();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await runWithBackendReady(async () => {
        await register(formData.name, formData.email, formData.password, formData.role);
        navigate('/');
      });
    } catch (err) {
      const data = err.response?.data;
      if (data?.errors) {
        const messages = Object.values(data.errors).join(', ');
        setError(messages);
      } else {
        setError(data?.message || 'Registration failed');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <BackendLoadingOverlay show={showLoading} fallback={showFallback} />
      <div className="auth-split">
        <section className="auth-branding">
          <div className="auth-branding-content">
            <Logo size="large" />
            <div>
              <div className="auth-branding-title">Ledgera</div>
              <div className="auth-tagline">Control your money. Build your future.</div>
            </div>
            <AuthBrandingFeatures />
          </div>
        </section>
        <section className="auth-panel">
          <div className="auth-card">
            <div className="auth-logo">
              <Logo size="large" />
            </div>
            <div className="auth-logo-text">Create Account</div>
            <div className="auth-subtitle">Join Ledgera as an Analyst or Viewer</div>

            {error && <div className="alert alert-error">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="register-name">Full Name</label>
                <input
                  id="register-name"
                  type="text"
                  name="name"
                  className="form-input"
                  placeholder="Enter your full name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label htmlFor="register-email">Email Address</label>
                <input
                  id="register-email"
                  type="email"
                  name="email"
                  className="form-input"
                  placeholder="Enter your email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="register-password">Password</label>
                <div className="password-input-wrap">
                  <input
                    id="register-password"
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    className="form-input form-input--password"
                    placeholder="Min 6 characters"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    minLength={6}
                    autoComplete="new-password"
                  />
                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() => setShowPassword((prev) => !prev)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                    aria-pressed={showPassword}
                  >
                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="register-role">Role</label>
                <select
                  id="register-role"
                  name="role"
                  className="form-select"
                  value={formData.role}
                  onChange={handleChange}
                >
                  <option value="ANALYST">Analyst</option>
                  <option value="VIEWER">Viewer</option>
                </select>
              </div>

              <button
                id="register-submit"
                type="submit"
                className="btn btn-primary btn-full"
                disabled={loading || showLoading}
              >
                {loading ? 'Creating Account...' : 'Create Account'}
              </button>
            </form>

            <div className="auth-links">
              Already have an account? <Link to="/login">Sign in</Link>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
