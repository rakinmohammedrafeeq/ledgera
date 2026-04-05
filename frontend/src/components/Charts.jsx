import { useMemo } from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
} from 'recharts';
import { BarChart3, PieChart as PieChartIcon } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';

const PIE_COLORS_DARK = [
  '#FF4D4F',
  '#36CFC9',
  '#597EF7',
  '#9254DE',
  '#FADB14',
  '#FA8C16',
  '#73D13D',
  '#EB2F96',
];

const PIE_COLORS_LIGHT = [
  '#D9363E',
  '#13C2C2',
  '#2F54EB',
  '#722ED1',
  '#C9A227',
  '#D46B08',
  '#52C41A',
  '#C41D7F',
];

/**
 * Converts transaction amount input to a finite positive number.
 * Supports numbers and strings like "1200", "1,200", "₹1200".
 */
function toFiniteExpenseAmount(raw) {
  if (raw == null || raw === '') return 0;

  if (typeof raw === 'number' && Number.isFinite(raw)) {
    return Math.abs(raw);
  }

  const cleaned = String(raw).replace(/[^0-9.-]+/g, '');
  const n = Number.parseFloat(cleaned);

  return Number.isFinite(n) ? Math.abs(n) : 0;
}

function getTooltipStyle() {
  return {
    backgroundColor: 'var(--tooltip-bg)',
    border: '1px solid var(--border-color)',
    borderRadius: '8px',
    color: 'var(--tooltip-text)',
    fontSize: '0.82rem',
    boxShadow: 'var(--shadow-md)',
  };
}

function MonthlyTrendsTooltip({ active, payload, label, isDark }) {
  if (!active || !payload || payload.length === 0) return null;

  const containerStyle = {
    backgroundColor: isDark ? '#111111' : '#ffffff',
    color: isDark ? '#f5f5f5' : '#18181b',
    padding: '10px 14px',
    borderRadius: '8px',
    boxShadow: isDark
      ? '0 8px 24px rgba(0,0,0,0.5)'
      : '0 8px 24px rgba(0,0,0,0.15)',
    border: isDark
      ? '1px solid rgba(255,255,255,0.1)'
      : '1px solid rgba(0,0,0,0.1)',
    fontSize: '0.82rem',
  };

  return (
    <div style={containerStyle}>
      <p style={{ margin: 0, marginBottom: 6 }}>{label}</p>
      {payload.map((entry) => {
        const color =
          entry.name === 'Income'
            ? '#FFD700'
            : entry.name === 'Expense'
              ? '#FF4D4F'
              : isDark
                ? '#f5f5f5'
                : '#18181b';
        return (
          <p key={entry.dataKey || entry.name} style={{ margin: 0 }}>
            <span style={{ color }}>{entry.name}</span> :{' '}
            ₹{Number(entry.value).toLocaleString()}
          </p>
        );
      })}
    </div>
  );
}

/**
 * Expense breakdown by category.
 * Accepts either:
 *  - raw transactions array: [{ type, amount, category, ... }]
 *  - dashboard object with recentTransactions: { recentTransactions: [...] }
 *
 * Output MUST be: [{ name: string, value: number }]
 */
function buildExpenseCategoryPieData(input) {
  const dashboard = input && typeof input === 'object' && !Array.isArray(input) ? input : null;
  const transactions = Array.isArray(input)
    ? input
    : Array.isArray(dashboard?.transactions)
      ? dashboard.transactions
      : Array.isArray(dashboard?.recentTransactions)
        ? dashboard.recentTransactions
        : [];

  if (!Array.isArray(transactions) || transactions.length === 0) return [];

  const categoryTotals = transactions
    .filter((t) => String(t?.type || '').toUpperCase() === 'EXPENSE')
    .reduce((acc, t) => {
      const category =
        (t?.category && String(t.category).trim()) || 'Uncategorized';
      const amount = toFiniteExpenseAmount(t?.amount);

      if (!Number.isFinite(amount) || amount <= 0) return acc;
      acc[category] = (acc[category] || 0) + amount;
      return acc;
    }, {});

  return Object.entries(categoryTotals)
    .map(([name, value]) => ({ name, value: Number(value) }))
    .filter((row) => Number.isFinite(row.value) && row.value > 0)
    .sort((a, b) => b.value - a.value);
}

export function MonthlyTrendsChart({ data }) {
  const { isDark } = useTheme();

  const gridStroke = 'rgba(127, 127, 127, 0.18)';
  const incomeFill = '#FFD700';
  const expenseFill = '#FF4D4F';

  if (!data || data.length === 0) {
    return (
      <div className="chart-surface">
        <h3 className="chart-surface__title">Monthly Trends</h3>
        <div className="empty-state empty-state--chart">
          <div className="empty-state-icon" aria-hidden>
            <BarChart3 strokeWidth={1.5} />
          </div>
          <div className="empty-state-text">No trend data available</div>
        </div>
      </div>
    );
  }

  const chartData = data.map((item) => ({
    name: `${item.monthName} ${item.year}`,
    Income: parseFloat(item.income) || 0,
    Expense: parseFloat(item.expense) || 0,
  }));

  return (
    <div className="chart-surface">
      <h3 className="chart-surface__title">Monthly Trends</h3>
      <div className="chart-surface__viz">
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData} barGap={4}>
            <CartesianGrid strokeDasharray="3 3" stroke={gridStroke} />
            <XAxis
              dataKey="name"
              tick={{ fill: 'var(--text-secondary)', fontSize: 12 }}
              axisLine={{ stroke: 'var(--border-color)' }}
            />
            <YAxis
              tick={{ fill: 'var(--text-secondary)', fontSize: 12 }}
              axisLine={{ stroke: 'var(--border-color)' }}
            />
            <Tooltip content={<MonthlyTrendsTooltip isDark={isDark} />} />
            <Legend wrapperStyle={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }} />
            <Bar dataKey="Income" fill={incomeFill} radius={[4, 4, 0, 0]} />
            <Bar dataKey="Expense" fill={expenseFill} radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

export function CategoryBreakdownChart({ data }) {
  const tooltipStyle = useMemo(() => getTooltipStyle(), []);

  const pieData = useMemo(() => buildExpenseCategoryPieData(data), [data]);

  if (pieData.length === 0) {
    return (
      <div className="chart-surface chart-surface--pie">
        <h3 className="chart-surface__title">Expense Breakdown</h3>
        <div className="empty-state empty-state--chart">
          <div className="empty-state-icon" aria-hidden>
            <PieChartIcon strokeWidth={1.5} />
          </div>
          <div className="empty-state-text">No expense data available</div>
        </div>
      </div>
    );
  }

  const labelLineStroke = 'var(--text-secondary)';

  const renderLabel = ({ name, percent }) =>
    `${name} ${(percent * 100).toFixed(0)}%`;

  // Use the existing palette but force Expense to read as “expense-y” first.
  const categoryColors = {
    dark: PIE_COLORS_DARK,
    light: PIE_COLORS_LIGHT,
  }[document.documentElement.classList.contains('dark') ? 'dark' : 'light'];

  return (
    <div className="chart-surface chart-surface--pie">
      <h3 className="chart-surface__title">Expense Breakdown</h3>
      <p className="chart-surface__subtitle">Expenses by category</p>

      {pieData.length === 1 && (
        <p className="chart-surface__subtitle" style={{ marginTop: '-0.25rem' }}>
          Only one expense category available
        </p>
      )}

      <div className="chart-surface__viz">
        <ResponsiveContainer width="100%" height={320}>
          <PieChart>
            <Pie
              data={pieData}
              dataKey="value"
              nameKey="name"
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={92}
              paddingAngle={3}
              minAngle={5}
              stroke="none"
              label={renderLabel}
              labelLine={{ stroke: labelLineStroke, strokeWidth: 1 }}
            >
              {pieData.map((slice, index) => (
                <Cell
                  key={`cell-${slice.name}-${index}`}
                  fill={categoryColors[index % categoryColors.length]}
                />
              ))}
            </Pie>
            <Tooltip
              contentStyle={tooltipStyle}
              labelStyle={{ color: 'var(--text-primary)' }}
              itemStyle={{ color: 'var(--text-primary)' }}
              formatter={(value, name) => {
                const numericValue = Number(value);
                const safeValue = Number.isFinite(numericValue) ? numericValue : 0;
                const formatted = new Intl.NumberFormat('en-IN', {
                  style: 'currency',
                  currency: 'INR',
                  maximumFractionDigits: 0,
                }).format(safeValue);
                return [formatted, name];
              }}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
