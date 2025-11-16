import {motion, AnimatePresence} from 'framer-motion';
import {useState, useEffect} from 'react';
import Swal from "sweetalert2";
import AnimatedSelect, {type SelectOption} from './AnimatedSelect.tsx';
import Button from './Button.tsx';
import { TeamService } from '../service/TeamService.ts';
import type { TeamFullSchema } from '../heroAPI.ts';

export interface Filter {
    id: number;
    field: string;
    operation: string;
    value: string;
}

interface FilterConfig {
    fields: Array<{value: string; label: string; type: string}>;
    operations: Record<string, string[]>;
    enumValues?: Record<string, string[]>;
    loadTeams?: boolean;
}

interface GenericFilterDialogProps {
    currentFilters: Filter[];
    onSave: (filters: Filter[]) => void;
    config: FilterConfig;
    title: string;
}

// eslint-disable-next-line react-refresh/only-export-components
export const getOperationSymbol = (operation: string) => {
    switch (operation) {
        case 'eq':
            return '=';
        case 'neq':
            return '≠';
        case 'gt':
            return '>';
        case 'lt':
            return '<';
        case 'gte':
            return '≥';
        case 'lte':
            return '≤';
        case 'like':
            return '⊂';
        default:
            return operation;
    }
};

const GenericFilterDialog = ({currentFilters, onSave, config, title}: GenericFilterDialogProps) => {
    const [filters, setFilters] = useState<Filter[]>(currentFilters);
    const [currentFilter, setCurrentFilter] = useState<Partial<Filter>>({});
    const [teams, setTeams] = useState<TeamFullSchema[]>([]);
    const [teamsLoading, setTeamsLoading] = useState(false);

    useEffect(() => {
        if (config.loadTeams) {
            const loadTeams = async () => {
                setTeamsLoading(true);
                try {
                    const teamsData = await TeamService.getAllTeams();
                    setTeams(teamsData);
                } catch (error) {
                    console.error('Failed to load teams:', error);
                } finally {
                    setTeamsLoading(false);
                }
            };

            loadTeams();
        }
    }, [config.loadTeams]);

    const getFieldType = (fieldName: string) => {
        const field = config.fields.find(f => f.value === fieldName);
        return field?.type || 'string';
    };

    const getEnumValues = (fieldName: string) => {
        return config.enumValues?.[fieldName as keyof typeof config.enumValues] || [];
    };

    const handleAddFilter = () => {
        if (currentFilter.field && currentFilter.operation && currentFilter.value !== undefined && currentFilter.value !== '') {
            const newFilter: Filter = {
                id: Date.now(),
                field: currentFilter.field,
                operation: currentFilter.operation,
                value: currentFilter.value
            };
            setFilters(prev => [...prev, newFilter]);
            setCurrentFilter({});
        }
    };

    const removeFilter = (id: number) => {
        setFilters(prev => prev.filter(filter => filter.id !== id));
    };

    const handleSave = () => {
        onSave(filters);
        Swal.close();
    };

    // Опции для полей
    const fieldOptions: SelectOption[] = config.fields.map(field => ({
        value: field.value,
        label: field.label
    }));

    // Опции для операций
    const operationOptions: SelectOption[] = currentFilter.field
        ? config.operations[getFieldType(currentFilter.field) as keyof typeof config.operations].map(op => ({
            value: op,
            label:
                op === 'eq' ? '= (Equal)' :
                    op === 'neq' ? '≠ (Not equal)' :
                        op === 'gt' ? '> (Greater than)' :
                            op === 'lt' ? '< (Less than)' :
                                op === 'gte' ? '≥ (Greater than or equal)' :
                                    op === 'lte' ? '≤ (Less than or equal)' :
                                        '⊂ (Contains)'
        }))
        : [];

    // Опции для команд (для операций eq и neq)
    const teamOptions: SelectOption[] = [
        { value: '', label: 'No team' },
        ...teams.map(team => ({
            value: team.id?.toString() || '',
            label: team.name || `Team #${team.id}`
        }))
    ];

    const renderValueInput = () => {
        if (!currentFilter.field || !currentFilter.operation) return null;

        const fieldType = getFieldType(currentFilter.field);
        const operation = currentFilter.operation;

        if (fieldType === 'team') {
            if (operation === 'like') {
                return (
                    <motion.input
                        initial={{opacity: 0, y: 10}}
                        animate={{opacity: 1, y: 0}}
                        exit={{opacity: 0, y: -10}}
                        type="text"
                        value={currentFilter.value || ''}
                        onChange={e => setCurrentFilter(prev => ({...prev, value: e.target.value}))}
                        placeholder="Enter team name substring"
                        style={{
                            padding: 'var(--spacing-sm)',
                            fontFamily: 'var(--font-family-primary)',
                            fontSize: 'var(--font-size-general)',
                            width: '100%',
                            boxSizing: 'border-box',
                            border: 'var(--border-width) var(--border-style) black',
                            borderRadius: 'var(--border-radius)'
                        }}
                        whileFocus={{
                            scale: 1.02,
                            borderColor: 'var(--color-primary)',
                            boxShadow: '0 0 0 2px rgba(59, 130, 246, 0.1)'
                        }}
                    />
                );
            } else {
                return (
                    <AnimatedSelect
                        value={currentFilter.value || ''}
                        onChange={value => setCurrentFilter(prev => ({...prev, value}))}
                        options={teamOptions}
                        placeholder={teamsLoading ? "Loading teams..." : "Select team"}
                        disabled={teamsLoading}
                    />
                );
            }
        }

        if (fieldType === 'boolean' && operation !== 'like') {
            return (
                <motion.div
                    initial={{opacity: 0, y: 10}}
                    animate={{opacity: 1, y: 0}}
                    exit={{opacity: 0, y: -10}}
                    style={{display: 'flex', alignItems: 'center', gap: 'var(--spacing-lg)'}}
                >
                    <label style={{display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer'}}>
                        <input
                            type="radio"
                            name="booleanValue"
                            checked={currentFilter.value === 'true'}
                            onChange={() => setCurrentFilter(prev => ({...prev, value: 'true'}))}
                            style={{width: 'auto'}}
                        />
                        True
                    </label>
                    <label style={{display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer'}}>
                        <input
                            type="radio"
                            name="booleanValue"
                            checked={currentFilter.value === 'false'}
                            onChange={() => setCurrentFilter(prev => ({...prev, value: 'false'}))}
                            style={{width: 'auto'}}
                        />
                        False
                    </label>
                </motion.div>
            );
        }

        if (fieldType === 'enum' && operation !== 'like') {
            const enumValues = getEnumValues(currentFilter.field);
            const enumOptions: SelectOption[] = enumValues.map(value => ({value, label: value}));

            return (
                <AnimatedSelect
                    value={currentFilter.value || ''}
                    onChange={value => setCurrentFilter(prev => ({...prev, value}))}
                    options={enumOptions}
                    placeholder="Select value"
                />
            );
        }

        return (
            <motion.input
                initial={{opacity: 0, y: 10}}
                animate={{opacity: 1, y: 0}}
                exit={{opacity: 0, y: -10}}
                type="text"
                value={currentFilter.value || ''}
                onChange={e => setCurrentFilter(prev => ({...prev, value: e.target.value}))}
                placeholder={
                    fieldType === 'boolean' && operation === 'like'
                        ? "Enter 'true' or 'false'"
                        : "Enter value"
                }
                style={{
                    padding: 'var(--spacing-sm)',
                    fontFamily: 'var(--font-family-primary)',
                    fontSize: 'var(--font-size-general)',
                    width: '100%',
                    boxSizing: 'border-box',
                    border: 'var(--border-width) var(--border-style) black',
                    borderRadius: 'var(--border-radius)'
                }}
                whileFocus={{
                    scale: 1.02,
                    borderColor: 'var(--color-primary)',
                    boxShadow: '0 0 0 2px rgba(59, 130, 246, 0.1)'
                }}
            />
        );
    };

    return (
        <div style={{minWidth: '300px', padding: 'var(--spacing-lg)'}}>
            <h3 style={{
                fontFamily: 'var(--font-family-accent)',
                fontSize: 'var(--font-size-xl)',
                color: 'var(--color-accent)',
                textAlign: 'center',
                marginBottom: 'var(--spacing-lg)'
            }}>
                {title}
            </h3>

            <div style={{
                marginBottom: 'var(--spacing-lg)',
                padding: 'var(--spacing-md)',
                fontSize: 'var(--font-size-general)'
            }}>
                <div style={{display: 'flex', flexDirection: 'column', gap: 'var(--spacing-sm)'}}>
                    {/* Поле выбора */}
                    <AnimatedSelect
                        value={currentFilter.field || ''}
                        onChange={field => setCurrentFilter(prev => ({
                            ...prev,
                            field,
                            operation: '',
                            value: ''
                        }))}
                        options={fieldOptions}
                        placeholder="Select field"
                    />

                    {/* Операция */}
                    <AnimatePresence mode="wait">
                        {currentFilter.field && (
                            <motion.div
                                key="operation-select"
                                initial={{opacity: 0, height: 0}}
                                animate={{opacity: 1, height: 'auto'}}
                                exit={{opacity: 0, height: 0}}
                                transition={{duration: 0.3}}
                            >
                                <AnimatedSelect
                                    value={currentFilter.operation || ''}
                                    onChange={operation => setCurrentFilter(prev => ({
                                        ...prev,
                                        operation,
                                        value: ''
                                    }))}
                                    options={operationOptions}
                                    placeholder="Select operation"
                                />
                            </motion.div>
                        )}
                    </AnimatePresence>

                    {/* Значение */}
                    <AnimatePresence mode="wait">
                        {currentFilter.operation && (
                            <motion.div
                                key="value-input"
                                initial={{opacity: 0, height: 0}}
                                animate={{opacity: 1, height: 'auto'}}
                                exit={{opacity: 0, height: 0}}
                                transition={{duration: 0.3}}
                            >
                                {renderValueInput()}
                            </motion.div>
                        )}
                    </AnimatePresence>

                    <Button
                        onClick={handleAddFilter}
                        disabled={!currentFilter.field || !currentFilter.operation || !currentFilter.value}
                    >
                        Add filter
                    </Button>
                </div>
            </div>

            <div style={{marginBottom: 'var(--spacing-lg)'}}>
                <h4 style={{
                    fontFamily: 'var(--font-family-primary)',
                    marginBottom: 'var(--spacing-sm)',
                    fontSize: 'var(--font-size-general)'
                }}>
                    Active filters ({filters.length})
                </h4>

                <div style={{minHeight: '60px'}}>
                    <AnimatePresence mode="popLayout">
                        {filters.length === 0 ? (
                            <motion.p
                                key="no-filters"
                                initial={{opacity: 0, y: 20}}
                                animate={{opacity: 1, y: 0}}
                                exit={{opacity: 0, y: -20}}
                                style={{
                                    fontFamily: 'var(--font-family-primary)',
                                    textAlign: 'center',
                                    padding: 'var(--spacing-lg)',
                                    color: 'var(--color-gray)',
                                    fontSize: 'var(--font-size-general)'
                                }}
                            >
                                No active filters
                            </motion.p>
                        ) : (
                            filters.map(filter => (
                                <motion.div
                                    key={filter.id}
                                    initial={{opacity: 0, scale: 0.8, height: 0}}
                                    animate={{opacity: 1, scale: 1, height: 'auto'}}
                                    exit={{
                                        opacity: 0,
                                        scale: 0.8,
                                        height: 0,
                                        marginTop: 0,
                                        marginBottom: 0
                                    }}
                                    transition={{duration: 0.3}}
                                    layout
                                    style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        padding: 'var(--spacing-sm)',
                                        margin: '5px 0',
                                        backgroundColor: 'var(--color-secondary)',
                                        fontFamily: 'var(--font-family-primary)',
                                        overflow: 'hidden',
                                        fontSize: 'var(--font-size-general)'
                                    }}
                                >
                                  <span style={{flex: 1}}>
                                    <strong>{filter.field}</strong> {getOperationSymbol(filter.operation)} {filter.value}
                                  </span>
                                    <motion.button
                                        onClick={() => removeFilter(filter.id)}
                                        whileHover={{scale: 1.2}}
                                        whileTap={{scale: 0.9}}
                                        style={{
                                            background: 'none',
                                            color: 'var(--color-accent)',
                                            cursor: 'pointer',
                                            fontSize: 'var(--font-size-general)',
                                            width: '30px',
                                            height: '30px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            flexShrink: 0,
                                            border: 'none'
                                        }}
                                    >
                                        ×
                                    </motion.button>
                                </motion.div>
                            ))
                        )}
                    </AnimatePresence>
                </div>
            </div>

            <div style={{display: 'flex', justifyContent: 'center', gap: 'var(--spacing-sm)'}}>
                <Button variant="gray" onClick={() => Swal && Swal.close()}>
                    Cancel
                </Button>
                <Button variant="success" onClick={handleSave}>
                    Apply filters
                </Button>
            </div>
        </div>
    );
};

export default GenericFilterDialog;