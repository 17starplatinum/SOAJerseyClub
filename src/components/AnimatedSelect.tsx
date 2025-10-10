import { motion, AnimatePresence } from 'framer-motion';
import { useState, useRef, useEffect } from 'react';

export interface SelectOption {
    value: string;
    label: string;
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
                            placeholder = "Выберите...",
                            disabled = false,
                            width = "100%"
                        }: AnimatedSelectProps) => {
    const [isOpen, setIsOpen] = useState(false);
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

    const selectedOption = options.find(opt => opt.value === value);

    const handleSelect = (optionValue: string) => {
        onChange(optionValue);
        setIsOpen(false);
    };

    return (
        <div
            ref={selectRef}
            className={`animated-select ${disabled ? 'disabled' : ''}`}
            style={{ position: 'relative', width }}
        >
            <motion.div
                className="select-trigger"
                onClick={() => !disabled && setIsOpen(!isOpen)}
                whileHover={!disabled ? { scale: 1.02 } : {}}
                whileTap={!disabled ? { scale: 0.98 } : {}}
                style={{
                    padding: '8px 12px',
                    background: 'white',
                    cursor: disabled ? 'not-allowed' : 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                    fontSize: "18px",
                    opacity: disabled ? 0.6 : 1,
                    minHeight: '40px',
                    boxSizing: 'border-box'
                }}
            >
                <span style={{
                    color: value ? '#374151' : '#9ca3af',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap'
                }}>
                    {selectedOption ? selectedOption.label : placeholder}
                </span>
                <motion.svg
                    animate={{ rotate: isOpen ? 180 : 0 }}
                    transition={{ duration: 0.2 }}
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    style={{ flexShrink: 0, marginLeft: '8px' }}
                >
                    <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                </motion.svg>
            </motion.div>

            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.8, y: -10 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.8, y: -10 }}
                        transition={{ duration: 0.2 }}
                        style={{
                            position: 'absolute',
                            top: '100%',
                            left: 0,
                            right: 0,
                            background: 'white',
                            boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                            marginTop: '4px',
                            overflow: 'hidden',
                            zIndex: 1000,
                            maxHeight: '200px',
                            overflowY: 'auto'
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
                                    padding: '10px 12px',
                                    cursor: 'pointer',
                                    borderBottom: '1px solid #f3f4f6',
                                    background: option.value === value ? '#3b82f6' : 'white',
                                    color: option.value === value ? 'white' : '#374151',
                                    fontFamily: "Comic Sans MS, Comic Sans, sans-serif",
                                    fontSize: "16px",
                                    transition: 'all 0.2s'
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