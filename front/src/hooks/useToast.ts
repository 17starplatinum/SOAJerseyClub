import { useCallback } from 'react';
import toast from 'react-hot-toast';

export const useToast = () => {
    const showSuccess = useCallback((message: string) => {
        toast.success(message, {
            duration: 4000,
            style: {
                background: 'var(--color-success)',
                color: 'var(--color-light)',
                fontSize: 'var(--font-size-general)',
                fontFamily: 'var(--font-family-primary)',
                border: 'var(--border-width) var(--border-style) var(--color-black)',
                borderRadius: 'var(--border-radius)',
                padding: 'var(--spacing-md)',
            },
        });
    }, []);

    const showError = useCallback((message: string) => {
        toast.error(message, {
            duration: 6000,
            style: {
                background: 'var(--color-accent)',
                color: 'var(--color-light)',
                fontSize: 'var(--font-size-general)',
                fontFamily: 'var(--font-family-primary)',
                border: 'var(--border-width) var(--border-style) var(--color-black)',
                borderRadius: 'var(--border-radius)',
                padding: 'var(--spacing-md)',
            },
        });
    }, []);

    const showLoading = useCallback((message: string): string => {
        return toast.loading(message, {
            style: {
                background: 'var(--color-primary)',
                color: 'var(--color-light)',
                fontSize: 'var(--font-size-general)',
                fontFamily: 'var(--font-family-primary)',
                border: 'var(--border-width) var(--border-style) var(--color-black)',
                borderRadius: 'var(--border-radius)',
                padding: 'var(--spacing-md)',
            },
        });
    }, []);

    const updateToast = useCallback((id: string, type: 'success' | 'error' | 'loading', message: string) => {
        const config = {
            style: {
                background: type === 'success' ? 'var(--color-success)' :
                    type === 'error' ? 'var(--color-accent)' : 'var(--color-primary)',
                color: 'var(--color-light)',
                fontSize: 'var(--font-size-general)',
                fontFamily: 'var(--font-family-primary)',
                border: 'var(--border-width) var(--border-style) var(--color-black)',
                borderRadius: 'var(--border-radius)',
                padding: 'var(--spacing-md)',
            },
        };

        if (type === 'success') {
            toast.success(message, { id, ...config });
        } else if (type === 'error') {
            toast.error(message, { id, ...config });
        } else {
            toast.loading(message, { id, ...config });
        }
    }, []);

    const dismissToast = useCallback((id: string) => {
        toast.dismiss(id);
    }, []);

    return {
        showSuccess,
        showError,
        showLoading,
        updateToast,
        dismissToast,
    };
};