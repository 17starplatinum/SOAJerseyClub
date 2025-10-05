import { useState, useEffect } from 'react';
// import type {HumanBeingFullSchema} from '../types';
import { HumanBeingService } from '../service/HumanBeingService.ts';
import type {HumanBeingFullSchema} from "../humanBeingAPI.ts";

export const useHumanBeings = () => {
    const [humanBeings, setHumanBeings] = useState<HumanBeingFullSchema[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const saved = localStorage.getItem('humanBeings');
        if (saved) {
            try {
                setHumanBeings(JSON.parse(saved));
            } catch (e) {
                console.error('Ошибка загрузки из localStorage:', e);
                localStorage.removeItem('humanBeings');
            }
        }
    }, []);

    useEffect(() => {
        if (humanBeings.length > 0) {
            localStorage.setItem('humanBeings', JSON.stringify(humanBeings));
        }
    }, [humanBeings]);

    const loadHumanBeings = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await HumanBeingService.getHumanBeings();
            setHumanBeings(response.humanBeingGetResponseDtos);
        } catch (err: any) {
            const errorMessage = err?.message || 'Неизвестная ошибка';
            setError(errorMessage);
            alert(`Ошибка загрузки: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    };

    return { humanBeings, loading, error, loadHumanBeings };
};