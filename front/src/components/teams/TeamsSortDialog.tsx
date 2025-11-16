import GenericSortDialog, { type SortRule } from '../GenericSortDialog.tsx';

interface TeamsSortDialogProps {
    currentSorts: SortRule[];
    onSave: (sorts: SortRule[]) => void;
}

const TEAMS_SORT_FIELDS = [
    { value: 'id', label: 'ID' },
    { value: 'name', label: 'Name' },
    { value: 'size', label: 'Size' },
];

const TeamsSortDialog = ({ currentSorts, onSave }: TeamsSortDialogProps) => {
    return (
        <GenericSortDialog
            currentSorts={currentSorts}
            onSave={onSave}
            fields={TEAMS_SORT_FIELDS}
            title="Teams Sorting"
        />
    );
};

export default TeamsSortDialog;