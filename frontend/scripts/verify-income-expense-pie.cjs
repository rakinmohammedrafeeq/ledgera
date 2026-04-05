// Verification script for Income vs Expense pie data.
// Run: node scripts/verify-income-expense-pie.cjs

function toFiniteExpenseAmount(raw) {
  if (raw == null || raw === '') return 0;

  if (typeof raw === 'number' && Number.isFinite(raw)) {
    return Math.abs(raw);
  }

  const cleaned = String(raw).replace(/[^0-9.-]+/g, '');
  const n = Number.parseFloat(cleaned);

  return Number.isFinite(n) ? Math.abs(n) : 0;
}

function buildIncomeExpensePieData(input) {
  const dashboard = input && typeof input === 'object' && !Array.isArray(input) ? input : null;
  const transactions = Array.isArray(input)
    ? input
    : Array.isArray(dashboard?.transactions)
      ? dashboard.transactions
      : Array.isArray(dashboard?.recentTransactions)
        ? dashboard.recentTransactions
        : [];

  let totalIncome = toFiniteExpenseAmount(dashboard?.totalIncome);
  let totalExpense = toFiniteExpenseAmount(dashboard?.totalExpenses);

  if ((totalIncome === 0 && totalExpense === 0) && transactions.length > 0) {
    for (const t of transactions) {
      const type = String(t?.type || '').toUpperCase();
      const amount = toFiniteExpenseAmount(t?.amount);
      if (amount <= 0) continue;
      if (type === 'INCOME') totalIncome += amount;
      if (type === 'EXPENSE') totalExpense += amount;
    }
  }

  const pieData = [
    { name: 'Income', value: Number(totalIncome) },
    { name: 'Expense', value: Number(totalExpense) },
  ].filter((r) => Number.isFinite(r.value));

  const allZero = pieData.every((r) => r.value === 0);
  return allZero ? [] : pieData;
}

// Case 1: totals from dashboard (preferred)
const dashboardSample = {
  totalIncome: '₹50,000',
  totalExpenses: '-₹12,345',
};

const out1 = buildIncomeExpensePieData(dashboardSample);
console.log('Dashboard totals -> pie data:');
console.log(JSON.stringify(out1, null, 2));

if (out1.length !== 2 || out1[0].value !== 50000 || out1[1].value !== 12345) {
  throw new Error('Unexpected output for dashboard totals sample');
}

// Case 2: fallback from transactions
const txSample = [
  { type: 'INCOME', amount: '₹10,000' },
  { type: 'INCOME', amount: 5000 },
  { type: 'EXPENSE', amount: '-₹2,500' },
  { type: 'EXPENSE', amount: '-1200' },
];

const out2 = buildIncomeExpensePieData({ totalIncome: 0, totalExpenses: 0, recentTransactions: txSample });
console.log('\nTransactions fallback -> pie data:');
console.log(JSON.stringify(out2, null, 2));

const income2 = out2.find((x) => x.name === 'Income')?.value;
const expense2 = out2.find((x) => x.name === 'Expense')?.value;

if (income2 !== 15000 || expense2 !== 3700) {
  throw new Error(`Unexpected fallback totals. income=${income2}, expense=${expense2}`);
}

// Case 3: empty state when both are 0
const out3 = buildIncomeExpensePieData({ totalIncome: 0, totalExpenses: 0, recentTransactions: [] });
console.log('\nAll zero -> pie data:');
console.log(JSON.stringify(out3, null, 2));

if (out3.length !== 0) {
  throw new Error('Expected empty array when both totals are 0');
}

console.log('\nOK: Income vs Expense pie data builder works as expected.');

