import type {HumanBeingPaginatedSchema} from '../types';
import { stubHumanBeings } from '../stubData';

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const HumanBeingService = {
    async getHumanBeings(
        sort?: string[],
        filter?: string[],
        page?: number,
        pageSize?: number
    ): Promise<HumanBeingPaginatedSchema> {

        await delay(500);

        if (Math.random() < 0.1) {
            throw {
                code: 500,
                message: "Internal server error: unexpected failure",
                time: new Date().toISOString()
            };
        }

        return {
            humanBeingGetResponseDtos: stubHumanBeings,
            page: page || 1,
            pageSize: pageSize || stubHumanBeings.length,
            totalPages: 1,
            totalCount: stubHumanBeings.length
        };
    }
};