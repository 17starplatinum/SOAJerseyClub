import { useState, useEffect, useCallback } from "react";
import { HumanBeingService, type UiFilter } from "../service/HumanBeingService.ts";
import type { HumanBeingFullSchema } from "../humanBeingAPI.ts";

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

    const loadHumanBeings = useCallback(async () => {
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
            const errorMessage = err?.message || "Неизвестная ошибка";
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    }, [page, pageSize, filters]);

    useEffect(() => {
        loadHumanBeings();
    }, [loadHumanBeings]);

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
        try {
            await HumanBeingService.deleteHumanBeing(id);
            const resp = await HumanBeingService.getHumanBeings(page, pageSize, filters);
            const items = resp.humanBeingGetResponseDtos ?? [];
            if (items.length === 0 && page > 1) {
                setPage((p) => Math.max(1, p - 1));
            } else {
                setHumanBeings(items);
                setTotalPages(resp.totalPages ?? 1);
                setTotalCount(resp.totalCount ?? 0);
            }
        } catch (err: any) {
            const errorMessage = err?.message || "Неизвестная ошибка";
            setError(errorMessage);
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
    };
};