// export const WeaponType = {
//     AXE: "AXE",
//     SHOTGUN: "SHOTGUN",
//     MACHINE_GUN: "MACHINE_GUN",
// } as const;
// export type WeaponType = (typeof WeaponType)[keyof typeof WeaponType];
// export const Mood = {
//     SADNESS: "SADNESS",
//     SORROW: "SORROW",
//     GLOOM: "GLOOM",
//     APATHY: "APATHY",
//     RAGE: "RAGE",
// } as const;
//
// export type Mood = (typeof Mood)[keyof typeof Mood];
// export const Color = {
//     RED: "RED",
//     BLUE: "BLUE",
//     YELLOW: "YELLOW",
//     GREEN: "GREEN",
//     BLACK: "BLACK",
//     WHITE: "WHITE",
// } as const;
//
// export type Color = (typeof Color)[keyof typeof Color];
//
// export interface Coordinates {
//     x: number;
//     y: number;
// }
//
// export interface Car {
//     cool: boolean | null;
//     color: Color;
//     model: string | null;
// }
//
// export interface TeamDTOSchema {
//     // Замените на реальную структуру из HeroService.yaml
//     name: string;
//     size: number;
// }
//
// export interface HumanBeingDTOSchema {
//     name: string;
//     coordinates: Coordinates;
//     realHero: boolean;
//     hasToothpick: boolean;
//     impactSpeed: number;
//     weaponType: WeaponType | null;
//     team: TeamDTOSchema | null;
//     mood: Mood;
//     car: Car | null;
// }
//
// export interface HumanBeingFullSchema extends HumanBeingDTOSchema {
//     id: number;
//     creationDate: string;  // ISO date-time
// }
//
// export interface HumanBeingPaginatedSchema {
//     humanBeingGetResponseDtos: HumanBeingFullSchema[];
//     page: number | null;
//     pageSize: number | null;
//     totalPages: number | null;
//     totalCount: number | null;
// }
//
// export interface ImpactSpeedPaginatedSchema {
//     uniqueSpeeds: number[];
// }
//
// export interface Error {
//     code: number;
//     message: string;
//     time: string;  // ISO date-time
// }