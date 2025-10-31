import {useState, useEffect, useCallback, useRef} from "react";
import { HumanBeingService, type UiFilter } from "../service/HumanBeingService.ts";
import { TeamService } from "../service/TeamService.ts";
import type { HumanBeingDTOSchema, HumanBeingFullSchema } from "../humanBeingAPI.ts";
import type { TeamFullSchema } from "../heroAPI.ts";
import type { HumanBeingWithTeam, TeamsState } from "../types.ts";
import { useToast } from "./useToast.ts";
import type {SortRule} from "../components/GenericSortDialog.tsx";

interface HumanBeingsResponse {
    humanBeingGetResponseDtos?: HumanBeingFullSchema[];
    totalPages?: number;
    totalCount?: number;
}

interface LoadTeamsResult {
    teams: TeamFullSchema[];
    error: string | null;
}

export const useHumanBeings = (
    initialFilters: UiFilter[] = [],
    initialSorts: SortRule[] = []
) => {
    const [humanBeings, setHumanBeings] = useState<HumanBeingWithTeam[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [teamsState, setTeamsState] = useState<TeamsState>({
        teams: [],
        loading: false,
        error: null,
    });

    const isFirstRender = useRef(true);

    const [page, setPage] = useState<number>(() => {
        const saved = localStorage.getItem("humanBeingsPage");
        return saved ? Number(saved) : 1;
    });
    const [pageSize, setPageSize] = useState<number>(() => {
        const saved = localStorage.getItem("humanBeingsPageSize");
        return saved ? Number(saved) : 10;
    });
    const [totalPages, setTotalPages] = useState<number>(1);
    const [totalCount, setTotalCount] = useState<number>(0);

    const [filters, setFilters] = useState<UiFilter[]>(initialFilters);
    const { showLoading, updateToast } = useToast();

    const [sorts, setSorts] = useState<SortRule[]>(initialSorts);

    const handleError = useCallback((error: unknown): string => {
        if (error instanceof Error) {
            return error.message;
        } else if (typeof error === 'string') {
            return error;
        } else {
            return "Unknown error";
        }
    }, []);

    const loadTeams = useCallback(async (): Promise<LoadTeamsResult> => {
        setTeamsState(prev => ({ ...prev, loading: true, error: null }));
        try {
            const teams = await TeamService.getAllTeams();
            const result: LoadTeamsResult = { teams, error: null };
            setTeamsState({ teams, loading: false, error: null });
            return result;
        } catch (err) {
            const errorMessage = handleError(err);
            const result: LoadTeamsResult = { teams: [], error: errorMessage };
            setTeamsState({ teams: [], loading: false, error: errorMessage });
            return result;
        }
    }, [handleError]);

    const enrichHumanBeingsWithTeams = useCallback((
        humanBeings: HumanBeingFullSchema[],
        teams: TeamFullSchema[],
        teamLoadFailed: boolean
    ): HumanBeingWithTeam[] => {
        return humanBeings.map(human => ({
            ...human,
            team: teams.find(team => team.id === human.teamId) || undefined,
            teamLoadError: !!human.teamId && teamLoadFailed
        }));
    }, []);

    const handleApiResponse = useCallback((resp: HumanBeingsResponse, teams: TeamFullSchema[], teamLoadFailed: boolean): void => {
        const enrichedHumanBeings = enrichHumanBeingsWithTeams(
            resp.humanBeingGetResponseDtos ?? [],
            teams,
            teamLoadFailed
        );

        setHumanBeings(enrichedHumanBeings);
        setTotalPages(resp.totalPages ?? 1);
        setTotalCount(resp.totalCount ?? 0);

        if (page > (resp.totalPages ?? 1) && (resp.totalPages ?? 1) > 0) {
            setPage(resp.totalPages ?? 1);
        }
    }, [page, enrichHumanBeingsWithTeams]);

    const loadHumanBeingsData = useCallback(async (
        options: {
            showToast?: boolean;
            toastMessage?: string;
            successMessage?: string;
        } = {}
    ): Promise<void> => {
        const { showToast = true, toastMessage = "Загрузка данных...", successMessage } = options;

        setLoading(true);
        setError(null);

        const toastId = showToast ? showLoading(toastMessage) : null;

        try {
            const [teamsResult, resp] = await Promise.all([
                loadTeams(),
                HumanBeingService.getHumanBeings(page, pageSize, filters, sorts)
            ]);

            handleApiResponse(resp, teamsResult.teams, !!teamsResult.error);

            if (showToast && toastId) {
                const message = successMessage || `${resp.humanBeingGetResponseDtos?.length ?? 0} objects uploaded`;
                updateToast(toastId, 'success', message);
            }
        } catch (err: unknown) {
            const errorMessage = handleError(err);
            setError(errorMessage);

            if (showToast && toastId) {
                updateToast(toastId, 'error', `Download error: ${errorMessage}`);
            }
        } finally {
            setLoading(false);
        }
    }, [page, pageSize, filters, sorts, showLoading, updateToast, handleApiResponse, handleError, loadTeams]);

    const performEntityOperation = useCallback(async <T>(
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
                await loadHumanBeingsData({
                    showToast: false,
                    successMessage: 'The data has been updated successfully'
                });
            }
        } catch (err: unknown) {
            const errorMessage = handleError(err);
            setError(errorMessage);
            updateToast(toastId, 'error', `${errorPrefix}: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    }, [showLoading, updateToast, loadHumanBeingsData, handleError]);

    const loadHumanBeings = useCallback(() => {
        loadHumanBeingsData();
    }, [loadHumanBeingsData]);

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        loadHumanBeings();
    }, [loadHumanBeings]);

    useEffect(() => {
        const loadInitialData = async (): Promise<void> => {
            await loadHumanBeingsData({ showToast: false });
        };

        loadInitialData();
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
        localStorage.setItem("humanBeingsPage", String(page));
    }, [page]);

    useEffect(() => {
        localStorage.setItem("humanBeingsPageSize", String(pageSize));
    }, [pageSize]);

    const updateFilters = (newFilters: UiFilter[]): void => {
        setFilters(newFilters);
        localStorage.setItem("humanBeingsFilters", JSON.stringify(newFilters));
        setPage(1);
    };

    const updateSorts = (newSorts: SortRule[]): void => {
        setSorts(newSorts);
        localStorage.setItem("humanBeingsSorts", JSON.stringify(newSorts));
        setPage(1);
    };

    const deleteHumanBeing = async (id: number): Promise<void> => {
        await performEntityOperation(
            () => HumanBeingService.deleteHumanBeing(id),
            {
                loadingMessage: "Deleting an entry...",
                successMessage: 'The record was successfully deleted',
                errorPrefix: 'Deletion error'
            }
        );
    };

    const updateHumanBeing = async (id: number, data: HumanBeingDTOSchema): Promise<void> => {
        await performEntityOperation(
            () => HumanBeingService.updateHumanBeing(id, data),
            {
                loadingMessage: "Updating the record...",
                successMessage: 'The record was successfully updated',
                errorPrefix: 'Update error'
            }
        );
    };

    const getUniqueImpactSpeeds = async (): Promise<number[]> => {
        setLoading(true);
        const toastId = showLoading("Getting unique speeds...");

        try {
            const speeds = await HumanBeingService.getUniqueImpactSpeeds();
            updateToast(toastId, 'success', 'Unique speeds have been successfully uploaded');
            return speeds;
        } catch (err: unknown) {
            const errorMessage = handleError(err);
            updateToast(toastId, 'error', `Download error: ${errorMessage}`);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return {
        humanBeings,
        loading,
        error,
        page,
        pageSize,
        totalPages,
        totalCount,
        teamsState,
        setPage,
        setPageSize,
        loadHumanBeings,
        filters,
        updateFilters,
        deleteHumanBeing,
        updateHumanBeing,
        sorts,
        updateSorts,
        getUniqueImpactSpeeds,
        loadTeams,
    };
};