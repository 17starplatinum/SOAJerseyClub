import React, {useEffect, useState} from "react";
import Panel from "../components/Panel";
import {useHumanBeings} from "../hooks/useHumanBeings";
import {motion} from "framer-motion";
import FilterBox from "../components/FilterBox.tsx";
import FilterButton from "../components/FilterButton.tsx";
import {type Filter, getOperationSymbol} from "../components/FilterDialog.tsx";

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

    const removeFilter = (id: number) => {
        const newFilters = filters.filter((filter) => filter.id !== id);
        setFilters(newFilters);
        updateFilters(newFilters);
    };

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
        // Рассчитываем новый номер страницы так, чтобы сохранить позицию данных
        const newPage = Math.min(Math.ceil(((page - 1) * pageSize + 1) / newSize), totalPages);
        setPageSize(newSize);
        setPage(Math.max(1, newPage));
    };

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
                                onRemove={() => removeFilter(filter.id)}
                            />
                        ))}
                    </div>
                </div>
            </section>

            <section>
                <div style={{padding: "20px"}}>
                    <motion.button
                        onClick={loadHumanBeings}
                        disabled={loading}
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: loading ? 0 : -5,
                            scale: loading ? 1 : 1.01,
                            boxShadow: loading ? "" : "0 10px 10px #8c5f66"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        style={{
                            padding: "10px 20px",
                            backgroundColor: loading ? "#ccc" : "#007bff",
                            cursor: loading ? "not-allowed" : "pointer",
                            minWidth: "150px",
                        }}
                    >
                        {loading ? "Загрузка..." : "Обновить"}
                    </motion.button>

                    {error && (
                        <motion.div initial={{opacity: 0}} animate={{opacity: 1}}
                                    style={{color: "red", marginTop: "10px"}}>
                            Ошибка: {error}
                        </motion.div>
                    )}
                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        {humanBeings.map((human) => (
                            <Panel key={human.id} human={human} onDelete={() => deleteHumanBeing(human.id)}/>
                        ))}
                    </div>
                </div>
                <div style={{display: "flex", gap: 16, justifyContent: "center", marginBottom: 12}}>
                    <div>
                        <label>
                            Page size:{" "}
                            <select
                                value={pageSize}
                                onChange={(e) => {
                                    const newSize = Number(e.target.value);
                                    handlePageSizeChange(newSize);
                                }}
                            >
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>
                        </label>
                    </div>

                    <div>
                        Всего: {totalCount} • Страница: {page} / {Math.max(totalPages, 1)}
                    </div>

                    <motion.button
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: -5,
                            scale: 1.01,
                            boxShadow: "0 10px 10px #8c5f66"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        onClick={handlePrev}
                        disabled={loading || page <= 1}
                        style={{backgroundColor: "#007bff"}}
                    >
                        Назад
                    </motion.button>
                    <motion.button
                        initial={{
                            boxShadow: "none"
                        }}
                        whileHover={{
                            y: -5,
                            scale: 1.01,
                            boxShadow: "0 10px 10px #8c5f66"
                        }}
                        transition={{
                            times: [0, 0.9, 1]
                        }}
                        onClick={handleNext}
                        disabled={loading || page >= totalPages}
                        style={{backgroundColor: "#007bff"}}
                    >
                        Вперед
                    </motion.button>
                </div>
            </section>
        </>
    );
};

export default HumanBeingsPage;