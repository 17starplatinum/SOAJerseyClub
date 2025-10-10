import {Color, type HumanBeingFullSchema, Mood, WeaponType} from "./humanBeingAPI.ts";

export const stubHumanBeings: HumanBeingFullSchema[] = [
    {
        id: 1,
        name: "Sean Combs",
        coordinates: { x: -62, y: 3.41 },
        creationDate: "2027-12-06T13:35:27.265Z",
        realHero: true,
        hasToothpick: false,
        impactSpeed: 53,
        weaponType: WeaponType.SHOTGUN,
        mood: Mood.RAGE,
        car: {
            cool: true,
            color: Color.RED,
            model: "Lada Kalina"
        },
        team: { name: "Team A", size: 5 }
    },
    {
        id: 2,
        name: "Test Subject",
        coordinates: { x: -5, y: 1.23 },
        creationDate: "2027-11-01T10:00:00.000Z",
        realHero: false,
        hasToothpick: true,
        impactSpeed: 25,
        weaponType: WeaponType.AXE,
        mood: Mood.SORROW,
        car: null,
        team: null
    },
    {
        id: 3,
        creationDate: "2025-11-22 09:45:33",
        name: "Сидорова Мария Ивановна",
        coordinates: {x: 1500, y: 456789},
        realHero: true,
        hasToothpick: false,
        impactSpeed: 45,
        weaponType: WeaponType.MACHINE_GUN,
        team: {name: "Beta", size: 5},
        mood: Mood.APATHY,
        car: {cool: true, color: Color.BLACK, model: "Audi"}
    },
    {
        id: 4,
        creationDate: "2025-08-07 21:15:47",
        name: "Козлов Станислав Петрович",
        coordinates: {x: 78900, y: 234567},
        realHero: false,
        hasToothpick: true,
        impactSpeed: 18,
        weaponType: WeaponType.SHOTGUN,
        team: {name: "Gamma", size: 2},
        mood: Mood.SORROW,
        car: {cool: true, color: Color.GREEN, model: "Ford"}
    },
    {
        id: 5,
        creationDate: "2025-12-01 08:30:55",
        name: "Николаева Екатерина Викторовна",
        coordinates: {x: 32000, y: 765432},
        realHero: true,
        hasToothpick: false,
        impactSpeed: 52,
        weaponType: WeaponType.AXE,
        team: {name: "Delta", size: 4},
        mood: Mood.GLOOM,
        car: {cool: false, color: Color.WHITE, model: "Kia"}
    },
    {
        id: 6,
        creationDate: "2025-07-19 16:40:18",
        name: "Васильев Артем Олегович",
        coordinates: {x: 45670, y: 123456},
        realHero: false,
        hasToothpick: true,
        impactSpeed: 29,
        weaponType: WeaponType.MACHINE_GUN,
        team: {name: "Omega", size: 7},
        mood: Mood.RAGE,
        car: null
    },
    {
        id: 7,
        creationDate: "2025-09-30 11:25:42",
        name: "Морозова Анна Дмитриевна",
        coordinates: {x: 88000, y: 654321},
        realHero: true,
        hasToothpick: false,
        impactSpeed: 38,
        weaponType: WeaponType.SHOTGUN,
        team: null,
        mood: Mood.SADNESS,
        car: null
    },
    {
        id: 8,
        creationDate: "2025-11-11 13:55:29",
        name: "Громов Павел Игоревич",
        coordinates: {x: 12500, y: 987654},
        realHero: false,
        hasToothpick: true,
        impactSpeed: 22,
        weaponType: WeaponType.AXE,
        team: {name: "Zeta", size: 3},
        mood: Mood.APATHY,
        car: {cool: true, color: Color.BLACK, model: "BMW"}
    },
    {
        id: 9,
        creationDate: "2025-08-25 19:10:36",
        name: "Белова Ольга Никитична",
        coordinates: {x: 67000, y: 345678},
        realHero: true,
        hasToothpick: false,
        impactSpeed: 41,
        weaponType: WeaponType.MACHINE_GUN,
        team: {name: "Theta", size: 8},
        mood: Mood.SORROW,
        car: {cool: true, color: Color.BLUE, model: "Toyota"}
    },
    {
        id: 10,
        creationDate: "2025-10-17 22:05:14",
        name: "Давыдов Иван Кириллович",
        coordinates: {x: 54300, y: 876543},
        realHero: false,
        hasToothpick: true,
        impactSpeed: 31,
        weaponType: WeaponType.SHOTGUN,
        team: {name: "Lambda", size: 2},
        mood: Mood.GLOOM,
        car: {cool: false, color: Color.GREEN, model: "Volkswagen"}
    }
];