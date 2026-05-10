import { apiClient } from '@/api/client'
import type { UserListItem } from '@/types/userList'

export const usersApi = {
  async list(): Promise<UserListItem[]> {
    const response = await apiClient.get<UserListItem[]>('/api/users')
    return response.data
  },

  async assignable(): Promise<UserListItem[]> {
    const response = await apiClient.get<UserListItem[]>('/api/users/assignable')
    return response.data
  },

  async toggleStatus(id: number): Promise<UserListItem> {
    const response = await apiClient.put<UserListItem>(`/api/users/${id}/toggle-status`)
    return response.data
  },
}
