import { Api } from "../heroAPI.ts";
import type { TeamFullSchema, TeamPaginatedSchema } from "../heroAPI.ts";

const api = new Api({
    baseUrl: "https://localhost:15479/api/v1/heroes",
});

export const TeamService = {
    async getAllTeams(): Promise<TeamFullSchema[]> {
        const { data } = await api.teams.getTeams({ page: 1, pageSize: 1000 });
        return data.teams || [];
    },

    async getTeams(
        page?: number,
        pageSize?: number,
        filters: string[] = [],
        sort?: string
    ): Promise<TeamPaginatedSchema> {
        const query: Record<string, unknown> = {};
        if (page && page > 0) query.page = page;
        if (pageSize && pageSize > 0) query.pageSize = pageSize;
        if (filters.length > 0) query.filter = filters;
        if (sort) query.sort = sort;

        const { data } = await api.teams.getTeams(query);
        return data;
    },
};