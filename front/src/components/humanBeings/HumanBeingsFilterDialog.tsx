import GenericFilterDialog, { type Filter } from '../GenericFilterDialog.tsx';

interface HumanBeingsFilterDialogProps {
    currentFilters: Filter[];
    onSave: (filters: Filter[]) => void;
}

const HUMAN_BEINGS_FILTER_CONFIG = {
    fields: [
        {value: 'id', label: 'Id', type: 'number'},
        {value: 'name', label: 'Name', type: 'string'},
        {value: 'coordinates.x', label: 'Coordinates X', type: 'number'},
        {value: 'coordinates.y', label: 'Coordinates Y', type: 'number'},
        {value: 'impactSpeed', label: 'Impact Speed', type: 'number'},
        {value: 'realHero', label: 'Real Hero', type: 'boolean'},
        {value: 'hasToothpick', label: 'Has Toothpick', type: 'boolean'},
        {value: 'weaponType', label: 'Weapon Type', type: 'enum'},
        {value: 'mood', label: 'Mood', type: 'enum'},
        {value: 'car.model', label: 'Car model', type: 'string'},
        {value: 'car.cool', label: 'Car cool', type: 'boolean'},
        {value: 'car.color', label: 'Car color', type: 'enum'},
        {value: 'teamId', label: 'Team', type: 'team'},
    ],
    operations: {
        string: ['eq', 'neq', 'like'],
        number: ['eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'],
        boolean: ['eq', 'neq', 'like'],
        enum: ['eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'],
        team: ['eq', 'neq', 'like']
    },
    enumValues: {
        'weaponType': ['AXE', 'SHOTGUN', 'MACHINE_GUN'],
        'mood': ['SADNESS', 'SORROW', 'GLOOM', 'APATHY', 'RAGE'],
        'car.color': ['RED', 'BLUE', 'YELLOW', 'GREEN', 'BLACK', 'WHITE']
    },
    loadTeams: true
};

const HumanBeingsFilterDialog = ({ currentFilters, onSave }: HumanBeingsFilterDialogProps) => {
    return (
        <GenericFilterDialog
            currentFilters={currentFilters}
            onSave={onSave}
            config={HUMAN_BEINGS_FILTER_CONFIG}
            title="Human Beings Filters"
        />
    );
};

export default HumanBeingsFilterDialog;