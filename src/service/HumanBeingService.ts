import type { HumanBeingPaginatedSchema } from "../humanBeingAPI.ts";
import { Api } from "../humanBeingAPI.ts";

export interface UiFilter {
    id: number;
    field: string;
    operation: string;
    value: string;
}

const api = new Api({
    baseUrl: "https://localhost:15478/api/v1",
});

const toApiFilters = (filters: UiFilter[]): string[] => {
    return filters.map((f) => `${f.field}[${f.operation}]=${f.value}`);
};

export const HumanBeingService = {
    async getHumanBeings(
        page?: number,
        pageSize?: number,
        filters: UiFilter[] = []
    ): Promise<HumanBeingPaginatedSchema> {
        const query: Record<string, unknown> = {};
        if (page && page > 0) query.page = page;
        if (pageSize && pageSize > 0) query.pageSize = pageSize;
        const filterArr = toApiFilters(filters);
        if (filterArr.length > 0) query.filter = filterArr;

        const { data } = await api.humanBeings.getHumanBeings(query);
        return data;
    },

    async deleteHumanBeing(id: number): Promise<void> {
        await api.humanBeings.deleteHumanBeing(id);
    },
};