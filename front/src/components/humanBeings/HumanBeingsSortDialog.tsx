import GenericSortDialog, { type SortRule } from '../GenericSortDialog.tsx';

interface HumanBeingsSortDialogProps {
    currentSorts: SortRule[];
    onSave: (sorts: SortRule[]) => void;
}

const HUMAN_BEINGS_SORT_FIELDS = [
    { value: 'id', label: 'ID' },
    { value: 'name', label: 'Name' },
    { value: 'coordinates.x', label: 'Coordinates X' },
    { value: 'coordinates.y', label: 'Coordinates Y' },
    { value: 'creationDate', label: 'Creation Date' },
    { value: 'realHero', label: 'Real Hero' },
    { value: 'hasToothpick', label: 'Has Toothpick' },
    { value: 'impactSpeed', label: 'Impact Speed' },
    { value: 'weaponType', label: 'Weapon Type' },
    { value: 'mood', label: 'Mood' },
    { value: 'car.color', label: 'Car Color' },
    { value: 'car.model', label: 'Car Model' },
    { value: 'car.cool', label: 'Car Cool' },
    { value: 'teamId', label: 'Team' },
];

const HumanBeingsSortDialog = ({ currentSorts, onSave }: HumanBeingsSortDialogProps) => {
    return (
        <GenericSortDialog
            currentSorts={currentSorts}
            onSave={onSave}
            fields={HUMAN_BEINGS_SORT_FIELDS}
            title="Human Beings Sorting"
        />
    );
};

export default HumanBeingsSortDialog;