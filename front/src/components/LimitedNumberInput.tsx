import React, {useState, useCallback, type ChangeEvent} from 'react';

interface LimitedNumberInputProps {
    value?: number;
    onChange?: (value: number | null) => void;
    placeholder?: string;
    className?: string;
    min?: number;
    max?: number;
    step?: number;
    disabled?: boolean;
}

const LimitedNumberInput: React.FC<LimitedNumberInputProps> = ({
                                                                   value,
                                                                   onChange,
                                                                   placeholder = 'Enter number',
                                                                   className = '',
                                                                   min = -Number.MAX_SAFE_INTEGER,
                                                                   max = Number.MAX_SAFE_INTEGER,
                                                                   disabled = false,
                                                               }) => {
    const [inputValue, setInputValue] = useState<string>(value?.toString() || '');

    const isValidNumber = useCallback((num: number): boolean => {
        return (
            !isNaN(num) &&
            isFinite(num) &&
            num >= min &&
            num <= max &&
            num >= -Number.MAX_SAFE_INTEGER &&
            num <= Number.MAX_SAFE_INTEGER
        );
    }, [min, max]);

    const handleChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value;

        const allowedPatterns = [
            '',
            '-',
            '-?\\d+',
            '-?\\d*\\.',
            '-?\\d*\\.\\d+',
            '-?\\.\\d+',
        ];

        const isValidInput = allowedPatterns.some(pattern =>
            new RegExp(`^${pattern}$`).test(rawValue)
        );

        if (!isValidInput) {
            return;
        }

        if (rawValue === '' || rawValue === '-' || rawValue === '.' || rawValue === '-.') {
            setInputValue(rawValue);
            if (rawValue === '') {
                onChange?.(null);
            }
            return;
        }

        const numberValue = parseFloat(rawValue);

        if (!isValidNumber(numberValue)) {
            return;
        }

        setInputValue(rawValue);
        onChange?.(numberValue);
    }, [onChange, isValidNumber]);

    const handleBlur = useCallback(() => {
        if (inputValue === '-' || inputValue === '.' || inputValue === '-.') {
            setInputValue('');
            onChange?.(null);
        }
    }, [inputValue, onChange]);

    return (
        <input
            type="text"
            inputMode="decimal"
            value={inputValue}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder={placeholder}
            className={className}
            disabled={disabled}
            style={{
                padding: 'var(--spacing-sm)',
                fontFamily: 'var(--font-family-primary)',
                fontSize: 'var(--font-size-general)',
                width: '100%',
                boxSizing: 'border-box',
                border: 'var(--border-width) var(--border-style) black',
                borderRadius: 'var(--border-radius)',
            }}
        />
    );
};

export default LimitedNumberInput;