import React, {useEffect, useState} from "react";
import Panel from "../components/Panel";
import {useHumanBeings} from "../hooks/useHumanBeings";
import {motion} from "framer-motion";
import FilterBox from "../components/FilterBox.tsx";
import FilterButton from "../components/FilterButton.tsx";
import {type Filter, getOperationSymbol} from "../components/FilterDialog.tsx";
import AnimatedSelect from "../components/AnimatedSelect";
import "../variables.css";

const HumanBeingsPage: React.FC = () => {
    const [filters, setFilters] = useState<Filter[]>(() => {
        const savedFilters = localStorage.getItem("humanBeingsFilters");
        if (savedFilters) {
            try {
                return JSON.parse(savedFilters);
            } catch {
                return [];
            }
        }
        return [];
    });

    const {
        humanBeings,
        loading,
        error,
        page,
        pageSize,
        totalPages,
        totalCount,
        setPage,
        setPageSize,
        loadHumanBeings,
        updateFilters,
        deleteHumanBeing,
    } = useHumanBeings(filters);

    useEffect(() => {
        localStorage.setItem("humanBeingsFilters", JSON.stringify(filters));
    }, [filters]);

    const onFiltersUpdate = (newFilters: Filter[]) => {
        setFilters(newFilters);
        updateFilters(newFilters);
    };

    const handlePrev = () => {
        if (page > 1) setPage(page - 1);
    };

    const handleNext = () => {
        if (page < totalPages) setPage(page + 1);
    };

    const handlePageSizeChange = (newSize: number) => {
        const newPage = Math.min(Math.ceil(((page - 1) * pageSize + 1) / newSize), totalPages);
        setPageSize(newSize);
        setPage(Math.max(1, newPage));
    };

    // Опции для выбора размера страницы
    const pageSizeOptions = [
        { value: "5", label: "5" },
        { value: "10", label: "10" },
        { value: "20", label: "20" },
        { value: "50", label: "50" }
    ];

    return (
        <>
            <section>
                <div style={{display: "flex", justifyContent: "center"}}>
                    <div style={{display: "flex", flexWrap: "wrap", width: "70%"}}>
                        <FilterButton onFiltersUpdate={onFiltersUpdate} currentFilters={filters}/>
                        {filters.map((filter) => (
                            <FilterBox
                                key={filter.id}
                                name={`${filter.field} ${getOperationSymbol(filter.operation)} ${filter.value}`}
                            />
                        ))}
                    </div>
                </div>
            </section>

            <section>
                <div style={{padding: "var(--spacing-lg)"}}>
                    <motion.button
                        onClick={loadHumanBeings}
                        disabled={loading}
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: loading ? 0 : -5,
                            scale: loading ? 1 : 1.01,
                            boxShadow: loading ? "" : "var(--shadow-hover)"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        style={{
                            padding: "var(--spacing-sm) var(--spacing-lg)",
                            backgroundColor: loading ? "var(--color-disabled)" : "var(--color-primary)",
                            cursor: loading ? "not-allowed" : "pointer",
                            minWidth: "150px",
                            fontSize: "var(--font-size-general)"
                        }}
                    >
                        {loading ? "Загрузка..." : "Обновить"}
                    </motion.button>

                    {error && (
                        <motion.div initial={{opacity: 0}} animate={{opacity: 1}}
                                    style={{
                                        color: "var(--color-accent)",
                                        marginTop: "var(--spacing-sm)",
                                        fontSize: "var(--font-size-general)"
                                    }}>
                            Ошибка: {error}
                        </motion.div>
                    )}
                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        {humanBeings.map((human) => (
                            <Panel key={human.id} human={human} onDelete={() => deleteHumanBeing(human.id)}/>
                        ))}
                    </div>
                </div>
                <div style={{
                    display: "flex",
                    gap: "var(--spacing-md)",
                    justifyContent: "center",
                    marginBottom: "12px",
                    fontSize: "var(--font-size-general)",
                    alignItems: "center"
                }}>
                    <div style={{display: "flex", alignItems: "center", gap: "var(--spacing-sm)"}}>
                        <label style={{whiteSpace: "nowrap"}}>
                            Page size:
                        </label>
                        <AnimatedSelect
                            value={pageSize.toString()}
                            onChange={(newValue) => {
                                const newSize = Number(newValue);
                                handlePageSizeChange(newSize);
                            }}
                            options={pageSizeOptions}
                            width="100px"
                            disabled={loading}
                        />
                    </div>

                    <div style={{whiteSpace: "nowrap"}}>
                        Всего: {totalCount} • Страница: {page} / {Math.max(totalPages, 1)}
                    </div>

                    <motion.button
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: (loading || page <= 1) ? 0 : -5,
                            scale: (loading || page <= 1) ? 1 : 1.01,
                            boxShadow: (loading || page <= 1) ? "none" : "var(--shadow-hover)"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        onClick={handlePrev}
                        disabled={loading || page <= 1}
                        style={{
                            padding: "var(--spacing-sm) var(--spacing-md)",
                            backgroundColor: (loading || page <= 1) ? "var(--color-disabled)" : "var(--color-primary)",
                            cursor: (loading || page <= 1) ? "not-allowed" : "pointer",
                            fontSize: "var(--font-size-general)",
                            minWidth: "80px"
                        }}
                    >
                        Назад
                    </motion.button>
                    <motion.button
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: (loading || page >= totalPages) ? 0 : -5,
                            scale: (loading || page >= totalPages) ? 1 : 1.01,
                            boxShadow: (loading || page >= totalPages) ? "none" : "var(--shadow-hover)"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        onClick={handleNext}
                        disabled={loading || page >= totalPages}
                        style={{
                            padding: "var(--spacing-sm) var(--spacing-md)",
                            backgroundColor: (loading || page >= totalPages) ? "var(--color-disabled)" : "var(--color-primary)",
                            cursor: (loading || page >= totalPages) ? "not-allowed" : "pointer",
                            fontSize: "var(--font-size-general)",
                            minWidth: "80px"
                        }}
                    >
                        Вперед
                    </motion.button>
                </div>
            </section>
        </>
    );
};

export default HumanBeingsPage;