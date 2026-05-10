import { createContext, useContext, useMemo, type ReactNode } from 'react'

/** Ledgera backend has no workspaces; provider kept so the Fintrix app shell stays intact. */
interface WorkspaceContextType {
  activeWorkspaceId: null
  setActiveWorkspaceId: (_id: number | null) => void
  workspaces: []
  isWorkspacesLoading: boolean
  activeWorkspace: null
}

const WorkspaceContext = createContext<WorkspaceContextType | undefined>(undefined)

export function WorkspaceProvider({ children }: { children: ReactNode }) {
  const value = useMemo<WorkspaceContextType>(
    () => ({
      activeWorkspaceId: null,
      setActiveWorkspaceId: () => {},
      workspaces: [],
      isWorkspacesLoading: false,
      activeWorkspace: null,
    }),
    [],
  )

  return <WorkspaceContext.Provider value={value}>{children}</WorkspaceContext.Provider>
}

export function useWorkspace() {
  const ctx = useContext(WorkspaceContext)
  if (!ctx) throw new Error('useWorkspace must be used within a WorkspaceProvider')
  return ctx
}
