import { useMemo } from 'react';

/**
 * Centralized Recharts tooltip theming.
 *
 * Pass `isDark` from ThemeContext to keep tooltips consistent in both themes.
 */
export function useThemedRechartsTooltip(isDark) {
  return useMemo(() => {
    const contentStyle = {
      backgroundColor: isDark ? '#111111' : '#ffffff',
      color: isDark ? '#f5f5f5' : '#18181b',
      border: isDark
        ? '1px solid rgba(255,255,255,0.1)'
        : '1px solid rgba(0,0,0,0.1)',
      borderRadius: '8px',
      boxShadow: isDark
        ? '0 8px 24px rgba(0,0,0,0.5)'
        : '0 8px 24px rgba(0,0,0,0.15)',
      fontSize: '0.82rem',
      transition:
        'background-color 0.25s ease, color 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease',
    };

    const labelStyle = {
      color: isDark ? '#f5f5f5' : '#18181b',
    };

    const itemStyle = {
      color: isDark ? '#f5f5f5' : '#18181b',
    };

    return { contentStyle, labelStyle, itemStyle };
  }, [isDark]);
}
