import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown, Plus, Check, MoreVertical, Edit, Trash2 } from 'lucide-react';
import { useWorkspace } from '@/contexts/WorkspaceContext';
import { useSidebar } from '@/contexts/SidebarContext';
import { useNavigate } from 'react-router-dom';
import { RenameWorkspaceModal } from './RenameWorkspaceModal';

interface WorkspaceSwitcherProps {
  onCreateClick: () => void;
}

export const WorkspaceSwitcher: React.FC<WorkspaceSwitcherProps> = ({ onCreateClick }) => {
  const { currentWorkspace, workspaces, switchWorkspace, updateWorkspace, deleteWorkspace } = useWorkspace();
  const { isCollapsed } = useSidebar();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [showRenameModal, setShowRenameModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showActionsMenu, setShowActionsMenu] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const actionsMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (actionsMenuRef.current && !actionsMenuRef.current.contains(event.target as Node)) {
        setShowActionsMenu(false);
      }
    };

    if (showActionsMenu) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showActionsMenu]);

  const handleSwitch = async (workspaceId: number) => {
    try {
      await switchWorkspace(workspaceId);
      setIsOpen(false);
    } catch (error) {
      console.error('Failed to switch workspace:', error);
    }
  };

  const handleRename = async (newName: string) => {
    if (!currentWorkspace) return;
    await updateWorkspace(currentWorkspace.id, newName);
  };

  const handleDelete = async () => {
    if (!currentWorkspace) return;

    setIsDeleting(true);
    setDeleteError(null);

    try {
      await deleteWorkspace(currentWorkspace.id);
      setShowDeleteConfirm(false);
      navigate('/app/dashboard');
    } catch (error: any) {
      // Extract clean error message
      let errorMessage = 'Failed to delete workspace';
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      setDeleteError(errorMessage);
    } finally {
      setIsDeleting(false);
    }
  };

  const isOwner = currentWorkspace?.userPermission === 'OWNER';

  if (isCollapsed) {
    return (
      <div className="px-3 py-2">
        <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-primary/20 to-primary/10 flex items-center justify-center flex-shrink-0">
          <span className="text-sm font-bold text-primary">
            {currentWorkspace?.name.charAt(0).toUpperCase() || 'W'}
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="relative px-3 py-2">
      <div className="w-full flex items-center justify-between gap-2 px-3 py-2.5 rounded-lg">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="flex items-center gap-3 min-w-0 flex-1 hover:opacity-80 transition-opacity"
        >
          {/* Gold accent line */}
          <div className="w-0.5 h-8 bg-gradient-to-b from-primary via-primary to-primary/60 rounded-full flex-shrink-0" />
          
          {/* Workspace info */}
          <div className="flex flex-col items-start min-w-0 flex-1">
            <span className="text-sm font-semibold text-sidebar-foreground break-words w-full leading-tight">
              {currentWorkspace?.name || 'Select Workspace'}
            </span>
            <span className="text-xs text-sidebar-foreground/60 whitespace-nowrap mt-0.5">
              {currentWorkspace?.memberCount || 0} {currentWorkspace?.memberCount === 1 ? 'member' : 'members'}
            </span>
          </div>
        </button>
        
        {/* Actions */}
        <div className="flex items-center gap-1 flex-shrink-0">
          {currentWorkspace && isOwner && (
            <div className="relative" ref={actionsMenuRef}>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setShowActionsMenu(!showActionsMenu);
                }}
                className="p-1.5 hover:bg-sidebar-accent rounded-lg transition-colors"
                title="Workspace actions"
              >
                <MoreVertical className="w-4 h-4 text-sidebar-foreground/60" />
              </button>

              {showActionsMenu && (
                <div className="absolute right-0 top-full mt-2 w-48 bg-card border border-border rounded-xl shadow-lg z-50 overflow-hidden">
                  <div className="py-1">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setShowActionsMenu(false);
                        setShowRenameModal(true);
                      }}
                      className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-accent transition-colors text-left"
                    >
                      <Edit className="w-4 h-4 text-muted-foreground" />
                      <span className="text-sm font-medium">Rename Workspace</span>
                    </button>
                    <div className="my-1 border-t border-border" />
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setShowActionsMenu(false);
                        setShowDeleteConfirm(true);
                      }}
                      className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-red-500/10 transition-colors text-left text-red-600 dark:text-red-400"
                    >
                      <Trash2 className="w-4 h-4" />
                      <span className="text-sm font-medium">Delete Workspace</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="p-1 hover:bg-sidebar-accent rounded transition-colors"
          >
            <ChevronDown
              className={`w-4 h-4 text-sidebar-foreground/60 transition-transform duration-200 ${
                isOpen ? 'rotate-180' : ''
              }`}
            />
          </button>
        </div>
      </div>

      {isOpen && (
        <>
          <div
            className="fixed inset-0 z-40"
            onClick={() => setIsOpen(false)}
          />
          <div className="absolute top-full left-3 right-3 mt-2 bg-card border border-border rounded-xl shadow-lg z-50 overflow-hidden">
            <div className="max-h-64 overflow-y-auto">
              {workspaces.map((workspace) => (
                <button
                  key={workspace.id}
                  onClick={() => handleSwitch(workspace.id)}
                  className="w-full flex items-center gap-3 px-4 py-3 hover:bg-accent transition-colors"
                >
                  {/* Gold accent line */}
                  <div className={`w-0.5 h-8 rounded-full flex-shrink-0 ${
                    currentWorkspace?.id === workspace.id 
                      ? 'bg-gradient-to-b from-primary via-primary to-primary/60' 
                      : 'bg-muted'
                  }`} />
                  
                  {/* Workspace info */}
                  <div className="flex flex-col items-start min-w-0 flex-1">
                    <span className="text-sm font-medium text-foreground break-words w-full leading-tight">
                      {workspace.name}
                    </span>
                    <span className="text-xs text-muted-foreground whitespace-nowrap mt-0.5">
                      {workspace.memberCount} {workspace.memberCount === 1 ? 'member' : 'members'} · {workspace.userPermission === 'OWNER' ? 'Owner' : workspace.userPermission === 'EDITOR' ? 'Editor' : 'Viewer'}
                    </span>
                  </div>
                  
                  {currentWorkspace?.id === workspace.id && (
                    <Check className="w-4 h-4 text-primary flex-shrink-0" />
                  )}
                </button>
              ))}
            </div>
            <div className="border-t border-border">
              <button
                onClick={() => {
                  setIsOpen(false);
                  onCreateClick();
                }}
                className="w-full flex items-center gap-3 px-4 py-3 hover:bg-accent transition-colors text-primary"
              >
                <Plus className="w-4 h-4" />
                <span className="text-sm font-medium">Create Workspace</span>
              </button>
            </div>
          </div>
        </>
      )}

      {currentWorkspace && (
        <>
          <RenameWorkspaceModal
            isOpen={showRenameModal}
            onClose={() => setShowRenameModal(false)}
            currentName={currentWorkspace.name}
            onRename={handleRename}
          />

          {/* Delete Workspace Confirmation */}
          {showDeleteConfirm && (
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
              <div className="bg-card border border-border rounded-2xl shadow-2xl w-full max-w-md">
                <div className="p-6">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center">
                      <Trash2 className="w-6 h-6 text-red-600 dark:text-red-400" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-foreground">Delete Workspace</h3>
                      <p className="text-sm text-muted-foreground">This action cannot be undone</p>
                    </div>
                  </div>
                  <p className="text-sm text-muted-foreground mb-6">
                    Are you sure you want to delete <span className="font-medium text-foreground">"{currentWorkspace.name}"</span>? 
                    All workspace data, members, and records will be permanently removed.
                  </p>
                  
                  {deleteError && (
                    <div className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-lg">
                      <p className="text-sm text-red-600 dark:text-red-400">{deleteError}</p>
                    </div>
                  )}
                  
                  <div className="flex gap-3">
                    <button
                      onClick={() => {
                        setShowDeleteConfirm(false);
                        setDeleteError(null);
                      }}
                      disabled={isDeleting}
                      className="flex-1 px-4 py-2.5 bg-accent hover:bg-accent/80 text-foreground rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Cancel
                    </button>
                    <button
                      onClick={handleDelete}
                      disabled={isDeleting}
                      className="flex-1 px-4 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                    >
                      {isDeleting ? (
                        <>
                          <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                          Deleting...
                        </>
                      ) : (
                        'Delete Workspace'
                      )}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
