import { useState, useEffect, useCallback, useRef } from "react";
import { HumanBeingService, type UiFilter } from "../service/HumanBeingService.ts";
import type {HumanBeingDTOSchema, HumanBeingFullSchema} from "../humanBeingAPI.ts";
import { useToast } from "./useToast";

export const useHumanBeings = (initialFilters: UiFilter[] = []) => {
    const [humanBeings, setHumanBeings] = useState<HumanBeingFullSchema[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

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
    const { showSuccess, showError, showLoading, updateToast } = useToast();

    const isFirstRender = useRef(true);

    const loadHumanBeings = useCallback(async () => {
        setLoading(true);
        setError(null);
        const toastId = showLoading("Загрузка данных...");

        try {
            const resp = await HumanBeingService.getHumanBeings(page, pageSize, filters);
            setHumanBeings(resp.humanBeingGetResponseDtos ?? []);
            setTotalPages(resp.totalPages ?? 1);
            setTotalCount(resp.totalCount ?? 0);

            if (page > (resp.totalPages ?? 1) && (resp.totalPages ?? 1) > 0) {
                setPage(resp.totalPages ?? 1);
            }

            updateToast(toastId, 'success', `${resp.humanBeingGetResponseDtos?.length ?? 0} objects uploaded`);
        } catch (err: any) {
            const errorMessage = err?.message || "Unknown error";
            setError(errorMessage);
            updateToast(toastId, 'error', `Download error: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    }, [page, pageSize, filters, showLoading, updateToast]);

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        loadHumanBeings();
    }, [loadHumanBeings]);

    useEffect(() => {
        const loadInitialData = async () => {
            setLoading(true);
            setError(null);

            try {
                const resp = await HumanBeingService.getHumanBeings(page, pageSize, filters);
                setHumanBeings(resp.humanBeingGetResponseDtos ?? []);
                setTotalPages(resp.totalPages ?? 1);
                setTotalCount(resp.totalCount ?? 0);

                if (page > (resp.totalPages ?? 1) && (resp.totalPages ?? 1) > 0) {
                    setPage(resp.totalPages ?? 1);
                }
            } catch (err: any) {
                const errorMessage = err?.message || "Unknown error";
                setError(errorMessage);
                showError(`Download error: ${errorMessage}`);
            } finally {
                setLoading(false);
            }
        };

        loadInitialData();
    }, []);

    useEffect(() => {
        localStorage.setItem("humanBeingsPage", String(page));
    }, [page]);

    useEffect(() => {
        localStorage.setItem("humanBeingsPageSize", String(pageSize));
    }, [pageSize]);

    const updateFilters = (newFilters: UiFilter[]) => {
        setFilters(newFilters);
        localStorage.setItem("humanBeingsFilters", JSON.stringify(newFilters));
        setPage(1);
    };

    const deleteHumanBeing = async (id: number) => {
        setLoading(true);
        setError(null);
        const toastId = showLoading("Deleting an entry...");

        try {
            await HumanBeingService.deleteHumanBeing(id);
            updateToast(toastId, 'success', 'The record was successfully deleted');

            const loadingToastId = showLoading("Uploading updated data...");

            const resp = await HumanBeingService.getHumanBeings(page, pageSize, filters);
            const items = resp.humanBeingGetResponseDtos ?? [];
            if (items.length === 0 && page > 1) {
                setPage((p) => Math.max(1, p - 1));
            } else {
                setHumanBeings(items);
                setTotalPages(resp.totalPages ?? 1);
                setTotalCount(resp.totalCount ?? 0);
            }

            updateToast(loadingToastId, 'success', 'The data has been updated successfully');
        } catch (err: any) {
            const errorMessage = err?.message || "Unknown error";
            setError(errorMessage);
            updateToast(toastId, 'error', `Deletion error: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    };

    const updateHumanBeing = async (id: number, data: HumanBeingDTOSchema) => {
        setLoading(true);
        setError(null);
        const toastId = showLoading("Updating the record...");

        try {
            await HumanBeingService.updateHumanBeing(id, data);
            updateToast(toastId, 'success', 'The record was successfully updated');

            await loadHumanBeings();
        } catch (err: any) {
            const errorMessage = err?.message || "Unknown error";
            setError(errorMessage);
            updateToast(toastId, 'error', `Update error: ${errorMessage}`);
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
        setPage,
        setPageSize,
        loadHumanBeings,
        filters,
        updateFilters,
        deleteHumanBeing,
        updateHumanBeing
    };
};