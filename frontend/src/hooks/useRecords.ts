import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { recordsApi, type RecordsQueryParams } from '@/api/recordsApi'
import { usersApi } from '@/api/usersApi'
import { queryKeys } from '@/hooks/queryKeys'

export function useRecordsQuery(params: RecordsQueryParams) {
  return useQuery({
    queryKey: queryKeys.records(params),
    queryFn: () => recordsApi.list(params),
  })
}

export function useAssignableQuery(enabled: boolean) {
  return useQuery({
    queryKey: queryKeys.assignableUsers,
    queryFn: () => usersApi.assignable(),
    enabled,
  })
}

export function useCreateRecordMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: recordsApi.create,
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['records'] })
      void qc.invalidateQueries({ queryKey: queryKeys.dashboard })
    },
  })
}

export function useUpdateRecordMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: Parameters<typeof recordsApi.update>[1] }) =>
      recordsApi.update(id, body),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['records'] })
      void qc.invalidateQueries({ queryKey: queryKeys.dashboard })
    },
  })
}

export function useDeleteRecordMutation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: recordsApi.remove,
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: ['records'] })
      void qc.invalidateQueries({ queryKey: queryKeys.dashboard })
    },
  })
}
