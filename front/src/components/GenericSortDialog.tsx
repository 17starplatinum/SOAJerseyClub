import { motion, AnimatePresence } from 'framer-motion';
import { useState } from 'react';
import Swal from "sweetalert2";
import AnimatedSelect, { type SelectOption } from './AnimatedSelect.tsx';
import Button from './Button.tsx';

export interface SortRule {
    id: number;
    field: string;
    direction: 'asc' | 'desc';
}

interface GenericSortDialogProps {
    currentSorts: SortRule[];
    onSave: (sorts: SortRule[]) => void;
    fields: Array<{ value: string; label: string }>;
    title: string;
}

const GenericSortDialog = ({ currentSorts, onSave, fields, title }: GenericSortDialogProps) => {
    const [sorts, setSorts] = useState<SortRule[]>(currentSorts);
    const [currentSort, setCurrentSort] = useState<Partial<SortRule>>({});

    const handleAddSort = () => {
        if (currentSort.field && currentSort.direction) {
            const newSort: SortRule = {
                id: Date.now(),
                field: currentSort.field,
                direction: currentSort.direction
            };
            setSorts(prev => [...prev, newSort]);
            setCurrentSort({});
        }
    };

    const removeSort = (id: number) => {
        setSorts(prev => prev.filter(sort => sort.id !== id));
    };

    const handleSave = () => {
        onSave(sorts);
        Swal.close();
    };

    const fieldOptions: SelectOption[] = fields.map(field => ({
        value: field.value,
        label: field.label
    }));

    const directionOptions: SelectOption[] = [
        { value: 'asc', label: 'Ascending' },
        { value: 'desc', label: 'Descending' }
    ];

    return (
        <div style={{ minWidth: '300px', padding: 'var(--spacing-lg)' }}>
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
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-sm)' }}>
                    <AnimatedSelect
                        value={currentSort.field || ''}
                        onChange={field => setCurrentSort(prev => ({ ...prev, field }))}
                        options={fieldOptions}
                        placeholder="Select field"
                    />

                    <AnimatePresence mode="wait">
                        {currentSort.field && (
                            <motion.div
                                key="direction-select"
                                initial={{ opacity: 0, height: 0 }}
                                animate={{ opacity: 1, height: 'auto' }}
                                exit={{ opacity: 0, height: 0 }}
                                transition={{ duration: 0.3 }}
                            >
                                <AnimatedSelect
                                    value={currentSort.direction || ''}
                                    onChange={direction => setCurrentSort(prev => ({
                                        ...prev,
                                        direction: direction as 'asc' | 'desc'
                                    }))}
                                    options={directionOptions}
                                    placeholder="Select direction"
                                />
                            </motion.div>
                        )}
                    </AnimatePresence>

                    <Button
                        onClick={handleAddSort}
                        disabled={!currentSort.field || !currentSort.direction}
                    >
                        Add sort
                    </Button>
                </div>
            </div>

            <div style={{ marginBottom: 'var(--spacing-lg)' }}>
                <h4 style={{
                    fontFamily: 'var(--font-family-primary)',
                    marginBottom: 'var(--spacing-sm)',
                    fontSize: 'var(--font-size-general)'
                }}>
                    Active sorts ({sorts.length})
                </h4>

                <div style={{ minHeight: '60px' }}>
                    <AnimatePresence mode="popLayout">
                        {sorts.length === 0 ? (
                            <motion.p
                                key="no-sorts"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -20 }}
                                style={{
                                    fontFamily: 'var(--font-family-primary)',
                                    textAlign: 'center',
                                    padding: 'var(--spacing-lg)',
                                    color: 'var(--color-gray)',
                                    fontSize: 'var(--font-size-general)'
                                }}
                            >
                                No active sorts
                            </motion.p>
                        ) : (
                            sorts.map(sort => (
                                <motion.div
                                    key={sort.id}
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
                                        padding: 'var(--spacing-sm)',
                                        margin: '5px 0',
                                        backgroundColor: 'var(--color-secondary)',
                                        fontFamily: 'var(--font-family-primary)',
                                        overflow: 'hidden',
                                        fontSize: 'var(--font-size-general)'
                                    }}
                                >
                                    <span style={{ flex: 1 }}>
                                        <strong>{sort.field}</strong> ({sort.direction})
                                    </span>
                                    <motion.button
                                        onClick={() => removeSort(sort.id)}
                                        whileHover={{ scale: 1.2 }}
                                        whileTap={{ scale: 0.9 }}
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

            <div style={{ display: 'flex', justifyContent: 'center', gap: 'var(--spacing-sm)' }}>
                <Button variant="gray" onClick={() => Swal && Swal.close()}>
                    Cancel
                </Button>
                <Button variant="success" onClick={handleSave}>
                    Apply sorts
                </Button>
            </div>
        </div>
    );
};

export default GenericSortDialog;