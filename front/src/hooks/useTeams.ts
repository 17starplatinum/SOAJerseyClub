import { useState, useEffect, useCallback, useRef } from "react";
import { TeamService } from "../service/TeamService.ts";
import type { TeamDTOSchema, TeamFullSchema } from "../heroAPI.ts";
import { useToast } from "./useToast.ts";
import type { UiFilter } from "../service/HumanBeingService.ts";
import type {SortRule} from "../components/GenericSortDialog.tsx";

interface TeamsResponse {
    teams?: TeamFullSchema[];
    totalPages?: number;
    totalCount?: number;
}

export const useTeams = (
    initialFilters: UiFilter[] = [],
    initialSorts: SortRule[] = []
) => {
    const [teams, setTeams] = useState<TeamFullSchema[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [page, setPage] = useState<number>(() => {
        const saved = localStorage.getItem("teamsPage");
        return saved ? Number(saved) : 1;
    });
    const [pageSize, setPageSize] = useState<number>(() => {
        const saved = localStorage.getItem("teamsPageSize");
        return saved ? Number(saved) : 10;
    });
    const [totalPages, setTotalPages] = useState<number>(1);
    const [totalCount, setTotalCount] = useState<number>(0);

    const [filters, setFilters] = useState<UiFilter[]>(initialFilters);
    const { showLoading, updateToast } = useToast();

    const [sorts, setSorts] = useState<SortRule[]>(initialSorts);

    const isFirstRender = useRef(true);

    const handleError = useCallback((error: unknown): string => {
        if (error instanceof Error) {
            return error.message;
        } else if (typeof error === 'string') {
            return error;
        } else {
            return "Unknown error";
        }
    }, []);

    const handleApiResponse = useCallback((resp: TeamsResponse): void => {
        setTeams(resp.teams ?? []);
        setTotalPages(resp.totalPages ?? 1);
        setTotalCount(resp.totalCount ?? 0);

        if (page > (resp.totalPages ?? 1) && (resp.totalPages ?? 1) > 0) {
            setPage(resp.totalPages ?? 1);
        }
    }, [page]);

    const loadTeamsData = useCallback(async (
        options: {
            showToast?: boolean;
            toastMessage?: string;
            successMessage?: string;
        } = {}
    ): Promise<void> => {
        const { showToast = true, toastMessage = "Loading teams...", successMessage } = options;

        setLoading(true);
        setError(null);

        const toastId = showToast ? showLoading(toastMessage) : null;

        try {
            const resp = await TeamService.getTeams(page, pageSize, filters.map(f => `${f.field}[${f.operation}]=${f.value}`), sorts.map(s => s.direction === 'desc' ? `-${s.field}` : s.field).join(','));

            handleApiResponse(resp);

            if (showToast && toastId) {
                const message = successMessage || `${resp.teams?.length ?? 0} teams loaded`;
                updateToast(toastId, 'success', message);
            }
        } catch (err: unknown) {
            const errorMessage = handleError(err);
            setError(errorMessage);

            if (showToast && toastId) {
                updateToast(toastId, 'error', `Load error: ${errorMessage}`);
            }
        } finally {
            setLoading(false);
        }
    }, [page, pageSize, filters, sorts, showLoading, updateToast, handleApiResponse, handleError]);

    const performTeamOperation = useCallback(async <T>(
        operation: () => Promise<T>,
        options: {
            loadingMessage: string;
            successMessage: string;
            errorPrefix: string;
            reloadAfterSuccess?: boolean;
        }
    ): Promise<void> => {
        const { loadingMessage, successMessage, errorPrefix, reloadAfterSuccess = true } = options;

        setLoading(true);
        setError(null);
        const toastId = showLoading(loadingMessage);

        try {
            await operation();
            updateToast(toastId, 'success', successMessage);

            if (reloadAfterSuccess) {
                await loadTeamsData({
                    showToast: false,
                    successMessage: 'Teams data updated successfully'
                });
            }
        } catch (err: unknown) {
            const errorMessage = handleError(err);
            setError(errorMessage);
            updateToast(toastId, 'error', `${errorPrefix}: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    }, [showLoading, updateToast, loadTeamsData, handleError]);

    const loadTeams = useCallback(() => {
        loadTeamsData();
    }, [loadTeamsData]);

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        loadTeams();
    }, [loadTeams]);

    useEffect(() => {
        const loadInitialData = async (): Promise<void> => {
            await loadTeamsData({ showToast: false });
        };

        loadInitialData();
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        localStorage.setItem("teamsPage", String(page));
    }, [page]);

    useEffect(() => {
        localStorage.setItem("teamsPageSize", String(pageSize));
    }, [pageSize]);

    const updateFilters = (newFilters: UiFilter[]): void => {
        setFilters(newFilters);
        localStorage.setItem("teamsFilters", JSON.stringify(newFilters));
        setPage(1);
    };

    const updateSorts = (newSorts: SortRule[]): void => {
        setSorts(newSorts);
        localStorage.setItem("teamsSorts", JSON.stringify(newSorts));
        setPage(1);
    };

    const deleteTeam = async (id: number): Promise<void> => {
        await performTeamOperation(
            () => TeamService.deleteTeam(id),
            {
                loadingMessage: "Deleting team...",
                successMessage: 'Team deleted successfully',
                errorPrefix: 'Delete error'
            }
        );
    };

    const updateTeam = async (id: number, data: TeamDTOSchema): Promise<void> => {
        await performTeamOperation(
            () => TeamService.updateTeam(id, data),
            {
                loadingMessage: "Updating team...",
                successMessage: 'Team updated successfully',
                errorPrefix: 'Update error'
            }
        );
    };

    return {
        teams,
        loading,
        error,
        page,
        pageSize,
        totalPages,
        totalCount,
        setPage,
        setPageSize,
        loadTeams,
        filters,
        updateFilters,
        deleteTeam,
        updateTeam,
        sorts,
        updateSorts,
    };
};