import { useQuery } from '@tanstack/react-query'
import { dashboardApi } from '@/api'
import { queryKeys } from '@/hooks/queryKeys'

export function useDashboardQuery() {
  return useQuery({
    queryKey: queryKeys.dashboard,
    queryFn: () => dashboardApi.get(),
  })
}
