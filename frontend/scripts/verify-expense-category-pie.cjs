// Verification script for Expense Breakdown (by category) pie data.
// Run: node scripts/verify-expense-category-pie.cjs

function toFiniteExpenseAmount(raw) {
  if (raw == null || raw === '') return 0;

  if (typeof raw === 'number' && Number.isFinite(raw)) {
    return Math.abs(raw);
  }

  const cleaned = String(raw).replace(/[^0-9.-]+/g, '');
  const n = Number.parseFloat(cleaned);

  return Number.isFinite(n) ? Math.abs(n) : 0;
}

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
      const category = (t?.category && String(t.category).trim()) || 'Uncategorized';
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

const txSample = [
  { type: 'INCOME', category: 'Salary', amount: '₹50,000' },
  { type: 'EXPENSE', category: 'Food', amount: '₹1,200' },
  { type: 'EXPENSE', category: 'Food', amount: '-₹300' },
  { type: 'EXPENSE', category: 'Rent', amount: '-1200' },
  { type: 'EXPENSE', category: 'Travel', amount: '1,000' },
  { type: 'EXPENSE', category: '', amount: '₹50' },
  { type: 'EXPENSE', category: 'Bad', amount: 'not-a-number' },
];

const out = buildExpenseCategoryPieData(txSample);
console.log('Expense category pie data:');
console.log(JSON.stringify(out, null, 2));

const food = out.find((x) => x.name === 'Food')?.value;
const rent = out.find((x) => x.name === 'Rent')?.value;
const travel = out.find((x) => x.name === 'Travel')?.value;
const unc = out.find((x) => x.name === 'Uncategorized')?.value;

if (food !== 1500) throw new Error(`Expected Food=1500, got ${food}`);
if (rent !== 1200) throw new Error(`Expected Rent=1200, got ${rent}`);
if (travel !== 1000) throw new Error(`Expected Travel=1000, got ${travel}`);
if (unc !== 50) throw new Error(`Expected Uncategorized=50, got ${unc}`);
if (out.some((x) => x.name === 'Salary')) throw new Error('Income should not be included');
if (out.some((x) => x.name === 'Bad')) throw new Error('Invalid amounts should not be included');

// Edge case: no expenses
const none = buildExpenseCategoryPieData([{ type: 'INCOME', category: 'Salary', amount: 100 }]);
if (none.length !== 0) throw new Error('Expected empty when no expenses');

console.log('\nOK: Expense category pie data builder works as expected.');

