import GenericFilterDialog, { type Filter } from '../GenericFilterDialog.tsx';

interface TeamsFilterDialogProps {
    currentFilters: Filter[];
    onSave: (filters: Filter[]) => void;
}

const TEAMS_FILTER_CONFIG = {
    fields: [
        {value: 'id', label: 'Id', type: 'number'},
        {value: 'name', label: 'Name', type: 'string'},
        {value: 'size', label: 'Size', type: 'number'},
    ],
    operations: {
        string: ['eq', 'neq', 'like'],
        number: ['eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'],
    }
};

const TeamsFilterDialog = ({ currentFilters, onSave }: TeamsFilterDialogProps) => {
    return (
        <GenericFilterDialog
            currentFilters={currentFilters}
            onSave={onSave}
            config={TEAMS_FILTER_CONFIG}
            title="Teams Filters"
        />
    );
};

export default TeamsFilterDialog;