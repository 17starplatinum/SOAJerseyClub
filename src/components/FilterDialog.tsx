import { motion, AnimatePresence } from 'framer-motion';
import { useState } from 'react';
import Swal from "sweetalert2";
import AnimatedSelect, {type SelectOption} from './AnimatedSelect';

export interface Filter {
    id: number;
    field: string;
    operation: string;
    value: string;
}

interface FilterDialogProps {
    currentFilters: Filter[];
    onSave: (filters: Filter[]) => void;
}

const ENUM_VALUES = {
    'weaponType': ['AXE', 'SHOTGUN', 'MACHINE_GUN'],
    'mood': ['SADNESS', 'SORROW', 'GLOOM', 'APATHY', 'RAGE'],
    'car.color': ['RED', 'BLUE', 'YELLOW', 'GREEN', 'BLACK', 'WHITE']
};

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

const FILTER_CONFIG = {
    fields: [
        { value: 'id', label: 'Id', type: 'number' },
        { value: 'name', label: 'Name', type: 'string' },
        { value: 'coordinates.x', label: 'Coordinates X', type: 'number' },
        { value: 'coordinates.y', label: 'Coordinates Y', type: 'number' },
        { value: 'impactSpeed', label: 'Impact Speed', type: 'number' },
        { value: 'realHero', label: 'Real Hero', type: 'boolean' },
        { value: 'hasToothpick', label: 'Has Toothpick', type: 'boolean' },
        { value: 'weaponType', label: 'Weapon Type', type: 'enum' },
        { value: 'mood', label: 'Mood', type: 'enum' },
        { value: 'car.model', label: 'Car model', type: 'string' },
        { value: 'car.cool', label: 'Car cool', type: 'boolean' },
        { value: 'car.color', label: 'Car color', type: 'enum' },
    ],
    operations: {
        string: ['eq', 'neq', 'like'],
        number: ['eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'],
        boolean: ['eq', 'neq', 'like'],
        enum: ['eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like']
    }
};

const FilterDialog = ({ currentFilters, onSave }: FilterDialogProps) => {
    const [filters, setFilters] = useState<Filter[]>(currentFilters);
    const [currentFilter, setCurrentFilter] = useState<Partial<Filter>>({});

    const getFieldType = (fieldName: string) => {
        const field = FILTER_CONFIG.fields.find(f => f.value === fieldName);
        return field?.type || 'string';
    };

    const getEnumValues = (fieldName: string) => {
        return ENUM_VALUES[fieldName as keyof typeof ENUM_VALUES] || [];
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
    const fieldOptions: SelectOption[] = FILTER_CONFIG.fields.map(field => ({
        value: field.value,
        label: field.label
    }));

    // Опции для операций
    const operationOptions: SelectOption[] = currentFilter.field
        ? FILTER_CONFIG.operations[getFieldType(currentFilter.field) as keyof typeof FILTER_CONFIG.operations].map(op => ({
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

    const renderValueInput = () => {
        if (!currentFilter.field || !currentFilter.operation) return null;

        const fieldType = getFieldType(currentFilter.field);
        const operation = currentFilter.operation;

        if (fieldType === 'boolean' && operation !== 'like') {
            return (
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    style={{ display: 'flex', alignItems: 'center', gap: '20px' }}
                >
                    <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer' }}>
                        <input
                            type="radio"
                            name="booleanValue"
                            checked={currentFilter.value === 'true'}
                            onChange={() => setCurrentFilter(prev => ({ ...prev, value: 'true' }))}
                            style={{ width: 'auto' }}
                        />
                        True
                    </label>
                    <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer' }}>
                        <input
                            type="radio"
                            name="booleanValue"
                            checked={currentFilter.value === 'false'}
                            onChange={() => setCurrentFilter(prev => ({ ...prev, value: 'false' }))}
                            style={{ width: 'auto' }}
                        />
                        False
                    </label>
                </motion.div>
            );
        }

        if (fieldType === 'enum' && operation !== 'like') {
            const enumValues = getEnumValues(currentFilter.field);
            const enumOptions: SelectOption[] = enumValues.map(value => ({ value, label: value }));

            return (
                <AnimatedSelect
                    value={currentFilter.value || ''}
                    onChange={value => setCurrentFilter(prev => ({ ...prev, value }))}
                    options={enumOptions}
                    placeholder="Выберите значение"
                />
            );
        }

        return (
            <motion.input
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                type="text"
                value={currentFilter.value || ''}
                onChange={e => setCurrentFilter(prev => ({ ...prev, value: e.target.value }))}
                placeholder={
                    fieldType === 'boolean' && operation === 'like'
                        ? "Введите 'true' или 'false'"
                        : "Введите значение"
                }
                style={{
                    padding: '8px',
                    border: '1px solid #d1d5db',
                    borderRadius: '6px',
                    fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                    fontSize: "18px",
                    width: '100%',
                    boxSizing: 'border-box'
                }}
                whileFocus={{
                    scale: 1.02,
                    borderColor: '#3b82f6',
                    boxShadow: '0 0 0 2px rgba(59, 130, 246, 0.1)'
                }}
            />
        );
    };

    return (
        <div style={{ minWidth: '300px', padding: '20px' }}>
            <div style={{ marginBottom: '20px', padding: '15px', fontSize: '24px' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
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
                        placeholder="Выберите поле"
                    />

                    {/* Операция */}
                    <AnimatePresence mode="wait">
                        {currentFilter.field && (
                            <motion.div
                                key="operation-select"
                                initial={{ opacity: 0, height: 0 }}
                                animate={{ opacity: 1, height: 'auto' }}
                                exit={{ opacity: 0, height: 0 }}
                                transition={{ duration: 0.3 }}
                            >
                                <AnimatedSelect
                                    value={currentFilter.operation || ''}
                                    onChange={operation => setCurrentFilter(prev => ({
                                        ...prev,
                                        operation,
                                        value: ''
                                    }))}
                                    options={operationOptions}
                                    placeholder="Выберите операцию"
                                />
                            </motion.div>
                        )}
                    </AnimatePresence>

                    {/* Значение */}
                    <AnimatePresence mode="wait">
                        {currentFilter.operation && (
                            <motion.div
                                key="value-input"
                                initial={{ opacity: 0, height: 0 }}
                                animate={{ opacity: 1, height: 'auto' }}
                                exit={{ opacity: 0, height: 0 }}
                                transition={{ duration: 0.3 }}
                            >
                                {renderValueInput()}
                            </motion.div>
                        )}
                    </AnimatePresence>

                    <motion.button
                        initial={{ boxShadow: "none" }}
                        whileHover={{
                            y: -5,
                            scale: 1.01,
                            boxShadow: "0 10px 10px #8c5f66"
                        }}
                        whileTap={{ scale: 0.95 }}
                        transition={{ times: [0, 0.9, 1] }}
                        onClick={handleAddFilter}
                        disabled={!currentFilter.field || !currentFilter.operation || !currentFilter.value}
                        style={{
                            padding: '8px 16px',
                            backgroundColor: '#007bff',
                            border: 'none',
                            borderRadius: '6px',
                            color: 'white',
                            cursor: 'pointer',
                            fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                            fontSize: "16px",
                            opacity: (!currentFilter.field || !currentFilter.operation || !currentFilter.value) ? 0.6 : 1
                        }}
                    >
                        Добавить фильтр
                    </motion.button>
                </div>
            </div>

            <div style={{ marginBottom: '20px' }}>
                <h4 style={{ fontFamily: "Comic Sans MS, Comic Sans, sans-serif", marginBottom: '10px' }}>
                    Активные фильтры ({filters.length})
                </h4>

                <div style={{ minHeight: '60px' }}>
                    <AnimatePresence mode="popLayout">
                        {filters.length === 0 ? (
                            <motion.p
                                key="no-filters"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -20 }}
                                style={{
                                    fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                                    textAlign: 'center',
                                    padding: '20px',
                                    color: '#6b7280'
                                }}
                            >
                                Нет активных фильтров
                            </motion.p>
                        ) : (
                            filters.map(filter => (
                                <motion.div
                                    key={filter.id}
                                    initial={{ opacity: 0, scale: 0.8, height: 0 }}
                                    animate={{ opacity: 1, scale: 1, height: 'auto' }}
                                    exit={{
                                        opacity: 0,
                                        scale: 0.8,
                                        height: 0,
                                        marginTop: 0,
                                        marginBottom: 0
                                    }}
                                    transition={{ duration: 0.3 }}
                                    layout
                                    style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        padding: '10px',
                                        margin: '5px 0',
                                        backgroundColor: '#f2cc0c',
                                        borderRadius: '6px',
                                        fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                                        overflow: 'hidden'
                                    }}
                                >
                                    <span style={{ flex: 1 }}>
                                        <strong>{filter.field}</strong> {getOperationSymbol(filter.operation)} {filter.value}
                                    </span>
                                    <motion.button
                                        onClick={() => removeFilter(filter.id)}
                                        whileHover={{ scale: 1.2 }}
                                        whileTap={{ scale: 0.9 }}
                                        style={{
                                            background: 'none',
                                            border: 'none',
                                            color: '#dc3545',
                                            cursor: 'pointer',
                                            fontSize: '24px',
                                            width: '30px',
                                            height: '30px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            flexShrink: 0
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

            <div style={{ display: 'flex', justifyContent: 'center', gap: '10px' }}>
                <motion.button
                    initial={{ boxShadow: "none" }}
                    whileHover={{
                        y: -5,
                        scale: 1.01,
                        boxShadow: "0 10px 10px #8c5f66"
                    }}
                    whileTap={{ scale: 0.95 }}
                    transition={{ times: [0, 0.9, 1] }}
                    onClick={() => Swal && Swal.close()}
                    style={{
                        padding: '8px 16px',
                        backgroundColor: '#6c757d',
                        border: 'none',
                        borderRadius: '6px',
                        color: 'white',
                        cursor: 'pointer',
                        fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                        fontSize: "16px"
                    }}
                >
                    Отмена
                </motion.button>
                <motion.button
                    initial={{ boxShadow: "none" }}
                    whileHover={{
                        y: -5,
                        scale: 1.01,
                        boxShadow: "0 10px 10px #8c5f66"
                    }}
                    whileTap={{ scale: 0.95 }}
                    transition={{ times: [0, 0.9, 1] }}
                    onClick={handleSave}
                    style={{
                        padding: '8px 16px',
                        backgroundColor: '#28a745',
                        border: 'none',
                        borderRadius: '6px',
                        color: 'white',
                        cursor: 'pointer',
                        fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                        fontSize: "16px"
                    }}
                >
                    Применить фильтры
                </motion.button>
            </div>
        </div>
    );
};

export default FilterDialog;