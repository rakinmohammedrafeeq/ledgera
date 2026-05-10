export const queryKeys = {
  dashboard: ['dashboard'] as const,
  records: (params?: Record<string, unknown>) => ['records', params ?? {}] as const,
  users: ['users'] as const,
  assignableUsers: ['users', 'assignable'] as const,
}
