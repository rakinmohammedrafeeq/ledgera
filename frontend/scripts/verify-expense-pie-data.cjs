// Simple verification script to ensure expense pie transformation parses currency strings.
// Run: node scripts/verify-expense-pie-data.cjs

function toFiniteExpenseAmount(raw) {
  if (raw == null || raw === '') return 0;

  if (typeof raw === 'number' && Number.isFinite(raw)) {
    return Math.abs(raw);
  }

  const cleaned = String(raw).replace(/[^0-9.-]+/g, '');
  const n = Number.parseFloat(cleaned);

  return Number.isFinite(n) ? Math.abs(n) : 0;
}

function buildExpensePieData(inputData) {
  if (!Array.isArray(inputData) || inputData.length === 0) return [];

  const isTransactionRecord = (item) =>
    item &&
    typeof item === 'object' &&
    Object.prototype.hasOwnProperty.call(item, 'amount') &&
    Object.prototype.hasOwnProperty.call(item, 'type');

  const hasTransactions = inputData.some(isTransactionRecord);

  // Case 1: raw transactions
  if (hasTransactions) {
    const categoryTotals = inputData
      .filter((item) => String(item?.type || '').toUpperCase() === 'EXPENSE')
      .reduce((acc, item) => {
        const category =
          (item?.category && String(item.category).trim()) || 'Uncategorized';
        const amount = toFiniteExpenseAmount(item?.amount);

        if (!Number.isFinite(amount) || amount <= 0) return acc;
        acc[category] = (acc[category] || 0) + amount;
        return acc;
      }, {});

    return Object.entries(categoryTotals)
      .map(([name, value]) => ({ name, value: Number(value) }))
      .filter((row) => Number.isFinite(row.value) && row.value > 0)
      .sort((a, b) => b.value - a.value);
  }

  // Case 2: dashboard categoryTotals aggregates
  const categoryTotals = inputData.reduce((acc, item) => {
    const category =
      (item?.category && String(item.category).trim()) || 'Uncategorized';

    const expenseRaw =
      item?.expense ?? item?.Expense ?? item?.expenses ?? item?.totalExpense;
    const expense = toFiniteExpenseAmount(expenseRaw);

    if (!Number.isFinite(expense) || expense <= 0) return acc;
    acc[category] = (acc[category] || 0) + expense;
    return acc;
  }, {});

  return Object.entries(categoryTotals)
    .map(([name, value]) => ({ name, value: Number(value) }))
    .filter((row) => Number.isFinite(row.value) && row.value > 0)
    .sort((a, b) => b.value - a.value);
}

const sample = [
  { type: 'EXPENSE', category: 'Food', amount: '₹1,200' },
  { type: 'EXPENSE', category: 'Food', amount: '-₹300' },
  { type: 'EXPENSE', category: 'Rent', amount: '-1200' },
  { type: 'INCOME', category: 'Salary', amount: '₹50,000' },
];

const out = buildExpensePieData(sample);
console.log('Raw transactions -> pie data:');
console.log(JSON.stringify(out, null, 2));

// Lightweight assertions
if (!Array.isArray(out) || out.length === 0) {
  throw new Error('Expected non-empty pie data for sample expense transactions');
}

const food = out.find((r) => r.name === 'Food');
if (!food || food.value !== 1500) {
  throw new Error(`Expected Food total 1500, got ${food ? food.value : 'missing'}`);
}

const rent = out.find((r) => r.name === 'Rent');
if (!rent || rent.value !== 1200) {
  throw new Error(`Expected Rent total 1200, got ${rent ? rent.value : 'missing'}`);
}

const dashboardCategoryTotalsSample = [
  { category: 'Food', income: 0, expense: '₹1,200', total: -1200 },
  { category: 'Rent', income: 0, expense: '-1200', total: -1200 },
  { category: 'Salary', income: '₹50,000', expense: 0, total: 50000 },
];

const out2 = buildExpensePieData(dashboardCategoryTotalsSample);
console.log('\nDashboard categoryTotals -> pie data:');
console.log(JSON.stringify(out2, null, 2));

if (!Array.isArray(out2) || out2.length !== 2) {
  throw new Error('Expected 2 pie slices from dashboard categoryTotals sample');
}

const food2 = out2.find((r) => r.name === 'Food');
if (!food2 || food2.value !== 1200) {
  throw new Error(`Expected Food total 1200, got ${food2 ? food2.value : 'missing'}`);
}

const rent2 = out2.find((r) => r.name === 'Rent');
if (!rent2 || rent2.value !== 1200) {
  throw new Error(`Expected Rent total 1200, got ${rent2 ? rent2.value : 'missing'}`);
}

console.log('\nOK: expense pie transformation parses and aggregates correctly for both input shapes.');
