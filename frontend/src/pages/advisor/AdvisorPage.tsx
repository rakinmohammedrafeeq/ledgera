import { Brain } from 'lucide-react';
import { AdvisorChat } from '../../components/advisor/AdvisorChat';
import { FinancialInsights } from '../../components/advisor/FinancialInsights';

export const AdvisorPage = () => {
  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div>
          <h1 className="text-3xl font-bold">AI Financial Advisor</h1>
          <p className="text-muted-foreground">
            Get personalized investment advice and wealth-building strategies
          </p>
        </div>
      </div>

      {/* Info Banner */}
      <div className="bg-purple-50 dark:bg-purple-950/20 border border-purple-200 dark:border-purple-800 rounded-lg p-4">
        <div className="flex gap-3">
          <div className="flex-shrink-0">
            <Brain className="h-5 w-5 text-purple-600 dark:text-purple-400" />
          </div>
          <div className="space-y-1">
            <h3 className="font-semibold text-sm text-purple-900 dark:text-purple-100">
              Powered by RAG (Retrieval-Augmented Generation)
            </h3>
            <p className="text-sm text-purple-700 dark:text-purple-300">
              This AI advisor analyzes your actual financial records and provides professional investment advice, portfolio recommendations, and wealth-building strategies. All data stays secure in your database.
            </p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chat */}
        <AdvisorChat />

        {/* Insights */}
        <FinancialInsights />
      </div>

      {/* Features Info */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-4 rounded-lg border bg-card">
          <h3 className="font-semibold mb-2">📈 Investment Advice</h3>
          <p className="text-sm text-muted-foreground">
            Get recommendations on stocks, mutual funds, real estate, and other investment opportunities
          </p>
        </div>
        <div className="p-4 rounded-lg border bg-card">
          <h3 className="font-semibold mb-2">💼 Portfolio Planning</h3>
          <p className="text-sm text-muted-foreground">
            Receive personalized portfolio allocation strategies based on your financial capacity
          </p>
        </div>
        <div className="p-4 rounded-lg border bg-card">
          <h3 className="font-semibold mb-2">💰 Wealth Building</h3>
          <p className="text-sm text-muted-foreground">
            Get actionable advice on savings, tax planning, and long-term wealth creation
          </p>
        </div>
      </div>
    </div>
  );
};
