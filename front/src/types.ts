import type { HumanBeingFullSchema } from "./humanBeingAPI.ts";
import type { TeamFullSchema } from "./heroAPI.ts";

export interface HumanBeingWithTeam extends HumanBeingFullSchema {
    team?: TeamFullSchema;
}

export interface TeamsState {
    teams: TeamFullSchema[];
    loading: boolean;
    error: string | null;
}