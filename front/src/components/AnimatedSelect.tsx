import { motion, AnimatePresence } from 'framer-motion';
import { useState, useRef, useEffect, type ReactNode } from 'react';

export interface SelectOption {
    value: string;
    label: string | ReactNode;
}

interface AnimatedSelectProps {
    value: string;
    onChange: (value: string) => void;
    options: SelectOption[];
    placeholder?: string;
    disabled?: boolean;
    width?: string;
}

const AnimatedSelect = ({
                            value,
                            onChange,
                            options,
                            placeholder = "Select...",
                            disabled = false,
                            width = "100%"
                        }: AnimatedSelectProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const [dropdownDirection, setDropdownDirection] = useState<'down' | 'up'>('down');
    const selectRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (selectRef.current && !selectRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const calculateDropdownDirection = () => {
        if (!selectRef.current) return 'down';

        const rect = selectRef.current.getBoundingClientRect();
        const spaceBelow = window.innerHeight - rect.bottom;
        const spaceAbove = rect.top;
        const estimatedDropdownHeight = 200;

        if (spaceBelow < estimatedDropdownHeight && spaceAbove >= estimatedDropdownHeight) {
            return 'up';
        }

        return 'down';
    };

    const handleToggle = () => {
        if (disabled) return;

        if (!isOpen) {
            setDropdownDirection(calculateDropdownDirection());
        }
        setIsOpen(!isOpen);
    };

    const selectedOption = options.find(opt => opt.value === value);

    const handleSelect = (optionValue: string) => {
        onChange(optionValue);
        setIsOpen(false);
    };

    const dropdownVariants = {
        down: {
            initial: { opacity: 0, scale: 0.8, y: -10 },
            animate: { opacity: 1, scale: 1, y: 0 },
            exit: { opacity: 0, scale: 0.8, y: -10 }
        },
        up: {
            initial: { opacity: 0, scale: 0.8, y: 10 },
            animate: { opacity: 1, scale: 1, y: 0 },
            exit: { opacity: 0, scale: 0.8, y: 10 }
        }
    };

    const currentVariants = dropdownVariants[dropdownDirection];

    return (
        <div
            ref={selectRef}
            className={`animated-select ${disabled ? 'disabled' : ''}`}
            style={{ position: 'relative', width }}
        >
            <motion.div
                className="select-trigger"
                onClick={handleToggle}
                whileHover={!disabled ? { scale: 1.02 } : {}}
                whileTap={!disabled ? { scale: 0.98 } : {}}
                style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    background: 'var(--color-light)',
                    cursor: disabled ? 'not-allowed' : 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    fontFamily: 'var(--font-family-primary)',
                    fontSize: 'var(--font-size-general)',
                    opacity: disabled ? 0.6 : 1,
                    minHeight: '40px',
                    boxSizing: 'border-box',
                    border: 'var(--border-width) var(--border-style) black',
                    borderRadius: 'var(--border-radius)'
                }}
            >
                <span style={{
                    color: value ? 'var(--color-dark)' : '#9ca3af',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 'var(--spacing-sm)'
                }}>
                    {selectedOption ? selectedOption.label : placeholder}
                </span>
                <motion.svg
                    animate={{ rotate: isOpen ? 180 : 0 }}
                    transition={{ duration: 0.2 }}
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    style={{ flexShrink: 0, marginLeft: 'var(--spacing-sm)' }}
                >
                    <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                </motion.svg>
            </motion.div>

            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={currentVariants.initial}
                        animate={currentVariants.animate}
                        exit={currentVariants.exit}
                        transition={{ duration: 0.2 }}
                        style={{
                            position: 'absolute',
                            [dropdownDirection === 'down' ? 'top' : 'bottom']: '100%',
                            left: 0,
                            right: 0,
                            background: 'var(--color-light)',
                            boxShadow: 'var(--shadow-dropdown)',
                            marginTop: dropdownDirection === 'down' ? 'var(--spacing-xs)' : 0,
                            marginBottom: dropdownDirection === 'up' ? 'var(--spacing-xs)' : 0,
                            overflow: 'hidden',
                            zIndex: 1000,
                            maxHeight: '200px',
                            overflowY: 'auto',
                            border: 'var(--border-width) var(--border-style) black',
                            borderRadius: 'var(--border-radius)'
                        }}
                    >
                        {options.map((option, index) => (
                            <motion.div
                                key={option.value}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: index * 0.05 }}
                                className={`select-option ${option.value === value ? 'selected' : ''}`}
                                onClick={() => handleSelect(option.value)}
                                style={{
                                    padding: 'var(--spacing-sm) var(--spacing-md)',
                                    cursor: 'pointer',
                                    borderBottom: '1px solid #f3f4f6',
                                    background: option.value === value ? 'var(--color-primary)' : 'var(--color-light)',
                                    color: option.value === value ? 'var(--color-light)' : 'var(--color-dark)',
                                    fontFamily: 'var(--font-family-primary)',
                                    fontSize: 'var(--font-size-sm)',
                                    transition: 'all var(--transition-fast)',
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 'var(--spacing-sm)'
                                }}
                                whileHover={{
                                    backgroundColor: option.value === value ? '#2563eb' : '#f3f4f6'
                                }}
                            >
                                {option.label}
                            </motion.div>
                        ))}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default AnimatedSelect;