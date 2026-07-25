import { apiClient } from './client'

// AI Categorization Types
export interface AiCategorizationRequest {
  description: string
  amount?: string
  date?: string
}

export interface AiCategorizationResponse {
  category: string
  type: 'INCOME' | 'EXPENSE'
  confidence: number
  reasoning: string
  success: boolean
  error?: string
}

// AI Receipt Types
export interface AiReceiptResponse {
  amount: number
  merchant: string
  date: string
  category: string
  type: 'INCOME' | 'EXPENSE'
  description: string
  confidence: number
  success: boolean
  error?: string
  cloudinaryUrl?: string
  cloudinaryPublicId?: string
}

// AI Insights Types
export interface AiInsightsResponse {
  summary: string
  keyInsights: string[]
  recommendations: string[]
  spendingAnalysis: {
    topCategory: string
    percentageChange: number
    comparisonPeriod: string
  }
  trendAnalysis: string
  success: boolean
  error?: string
}

/**
 * Get AI-powered category suggestion for a transaction
 */
export const categorizeTransaction = async (
  request: AiCategorizationRequest
): Promise<AiCategorizationResponse> => {
  const response = await apiClient.post<AiCategorizationResponse>(
    '/ai/categorize',
    request
  )
  return response.data
}

/**
 * Upload receipt for OCR and automatic data extraction
 */
export const uploadReceipt = async (file: File): Promise<AiReceiptResponse> => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await apiClient.post<AiReceiptResponse>(
    '/ai/receipt',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      timeout: 30000, // 30 seconds for image processing
    }
  )
  return response.data
}

/**
 * Get AI-generated financial insights for current workspace
 */
export const getAiInsights = async (): Promise<AiInsightsResponse> => {
  const response = await apiClient.get<AiInsightsResponse>('/ai/insights')
  return response.data
}

/**
 * Check if AI service is available
 */
export const checkAiHealth = async (): Promise<boolean> => {
  try {
    await apiClient.get('/ai/health')
    return true
  } catch (error) {
    console.error('AI service health check failed:', error)
    return false
  }
}
