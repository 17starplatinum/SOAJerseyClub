import type {HumanBeingDTOSchema, HumanBeingFullSchema, HumanBeingPaginatedSchema} from "../humanBeingAPI.ts";
import {Api} from "../humanBeingAPI.ts";
import type {SortRule} from "../components/SortDialog.tsx";

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

const toApiSorts = (sorts: SortRule[]): string[] => {
    return sorts.map(sort =>
        sort.direction === 'desc' ? `-${sort.field}` : sort.field
    );
};

export const HumanBeingService = {
    async getHumanBeings(
        page?: number,
        pageSize?: number,
        filters: UiFilter[] = [],
        sorts: SortRule[] = []
    ): Promise<HumanBeingPaginatedSchema> {
        const query: Record<string, unknown> = {};
        if (page && page > 0) query.page = page;
        if (pageSize && pageSize > 0) query.pageSize = pageSize;

        const filterArr = toApiFilters(filters);
        if (filterArr.length > 0) query.filter = filterArr;

        const sortArr = toApiSorts(sorts);
        if (sortArr.length > 0) query.sort = sortArr;

        const {data} = await api.humanBeings.getHumanBeings(query);
        return data;
    },

    async deleteHumanBeing(id: number): Promise<void> {
        await api.humanBeings.deleteHumanBeing(id);
    },

    async addHumanBeing(data: HumanBeingDTOSchema): Promise<HumanBeingFullSchema> {
        const {data: response} = await api.humanBeings.addHumanBeing(data);
        return response;
    },

    async updateHumanBeing(id: number, data: HumanBeingDTOSchema): Promise<HumanBeingFullSchema> {
        const {data: response} = await api.humanBeings.updateHumanBeing(id, data);
        return response;
    },
};