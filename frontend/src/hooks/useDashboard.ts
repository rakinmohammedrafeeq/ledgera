import { useQuery } from '@tanstack/react-query'
import { dashboardApi } from '@/api'
import { queryKeys } from '@/hooks/queryKeys'
import { useWorkspace } from '@/contexts/WorkspaceContext'

export function useDashboardQuery() {
  const { currentWorkspace } = useWorkspace()
  return useQuery({
    queryKey: queryKeys.dashboard(currentWorkspace?.id ?? null),
    queryFn: () => dashboardApi.get(),
    enabled: Boolean(currentWorkspace),
  })
}
