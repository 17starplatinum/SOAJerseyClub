import React, { useEffect, useState } from "react";
import Panel from "../components/Panel.tsx";
import { useHumanBeings } from "../hooks/useHumanBeings.ts";
import { motion, AnimatePresence } from "framer-motion";
import FilterBox from "../components/FilterBox.tsx";
import FilterButton from "../components/FilterButton.tsx";
import { type Filter, getOperationSymbol } from "../components/FilterDialog.tsx";
import AnimatedSelect from "../components/AnimatedSelect.tsx";
import { Toaster } from 'react-hot-toast';
import "../variables.css";
import CreateHumanBeingDialog from '../components/CreateHumanBeingDialog.tsx';
import withReactContent from 'sweetalert2-react-content';
import Swal from "sweetalert2";
import type {HumanBeingFullSchema} from "../humanBeingAPI.ts";
import type {SortRule} from "../components/SortDialog.tsx";
import SortButton from "../components/SortButton.tsx";
import SortBox from "../components/SortBox.tsx";

const MySwal = withReactContent(Swal);


const HumanBeingsPage: React.FC = () => {
    const [filters, setFilters] = useState<Filter[]>(() => {
        const savedFilters = localStorage.getItem("humanBeingsFilters");
        return savedFilters ? JSON.parse(savedFilters) : [];
    });

    const [sorts, setSorts] = useState<SortRule[]>(() => {
        const savedSorts = localStorage.getItem("humanBeingsSorts");
        return savedSorts ? JSON.parse(savedSorts) : [];
    });

    const {
        humanBeings,
        loading,
        page,
        pageSize,
        totalPages,
        totalCount,
        setPage,
        setPageSize,
        loadHumanBeings,
        updateFilters,
        deleteHumanBeing,
        updateSorts,
    } = useHumanBeings(filters, sorts);

    useEffect(() => {
        localStorage.setItem("humanBeingsFilters", JSON.stringify(filters));
    }, [filters]);

    const onFiltersUpdate = (newFilters: Filter[]) => {
        setFilters(newFilters);
        updateFilters(newFilters);
    };

    const onSortsUpdate = (newSorts: SortRule[]) => {
        setSorts(newSorts);
        updateSorts(newSorts);
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

    const pageSizeOptions = [
        { value: "4", label: "4" },
        { value: "8", label: "8" },
        { value: "32", label: "32" },
        { value: "64", label: "64" }
    ];

    const handleOpenCreateDialog = () => {
        MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-success)">Create Object</p>`,
            html: <CreateHumanBeingDialog onSuccess={() => {
                loadHumanBeings();
            }} />,
            width: 600,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: true,
            allowEscapeKey: true,
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            showClass: {
                popup: 'animate__animated animate__fadeInDown'
            },
            hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
            },
            customClass: {
                popup: 'custom-swal'
            }
        });
    };

    const handleOpenEditDialog = (human: HumanBeingFullSchema) => {
        MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-primary)">Edit Object</p>`,
            html: <CreateHumanBeingDialog
                onSuccess={() => {
                    loadHumanBeings();
                }}
                editingHuman={human}
            />,
            width: 600,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: true,
            allowEscapeKey: true,
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            showClass: {
                popup: 'animate__animated animate__fadeInDown'
            },
            hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
            },
            customClass: {
                popup: 'custom-swal'
            }
        });
    };

    return (
        <>
            <Toaster
                position="top-right"
                toastOptions={{
                    duration: 4000,
                    style: {
                        background: 'var(--color-primary)',
                        color: 'var(--color-light)',
                        fontSize: 'var(--font-size-general)',
                        fontFamily: 'var(--font-family-primary)',
                        border: 'var(--border-width) var(--border-style) var(--color-black)',
                        borderRadius: 'var(--border-radius)',
                    },
                }}
            />

            <section>
                <div style={{display: "flex", justifyContent: "center"}}>
                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap", width: "70%"}}>
                        <FilterButton onFiltersUpdate={onFiltersUpdate} currentFilters={filters}/>
                        {filters.map((filter) => (
                            <FilterBox
                                key={filter.id}
                                name={`${filter.field} ${getOperationSymbol(filter.operation)} ${filter.value}`}
                            />
                        ))}

                        <SortButton onSortsUpdate={onSortsUpdate} currentSorts={sorts}/>
                        {sorts.map((sort) => (
                            <SortBox
                                key={sort.id}
                                name={`${sort.field} (${sort.direction})`}
                            />
                        ))}
                    </div>
                </div>
            </section>

            <section>
                <div style={{padding: "var(--spacing-lg)"}}>
                    <div style={{padding: "var(--spacing-lg)", display: "flex", gap: "var(--spacing-md)", justifyContent: "center"}}>
                        <motion.button
                            onClick={() => loadHumanBeings()}
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
                            {loading ? "Loading..." : "Refresh"}
                        </motion.button>

                        <motion.button
                            onClick={handleOpenCreateDialog}
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
                                backgroundColor: loading ? "var(--color-disabled)" : "var(--color-success)",
                                cursor: loading ? "not-allowed" : "pointer",
                                minWidth: "150px",
                                fontSize: "var(--font-size-general)"
                            }}
                        >
                            Create
                        </motion.button>
                    </div>

                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        <AnimatePresence>
                            {humanBeings.map((human) => (
                                <motion.div
                                    key={human.id}
                                    layout
                                    initial={{ opacity: 1, scale: 1 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    exit={{
                                        opacity: 0,
                                        scale: 0.8,
                                        transition: { duration: 0.3 }
                                    }}
                                    transition={{ duration: 0.2 }}
                                >
                                    <Panel
                                        key={human.id}
                                        human={human}
                                        onDelete={() => deleteHumanBeing(human.id)}
                                        onEdit={handleOpenEditDialog}
                                    />
                                </motion.div>
                            ))}
                        </AnimatePresence>
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
                        Total: {totalCount} • Page: {page} / {Math.max(totalPages, 1)}
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
                        Previous
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
                        Next
                    </motion.button>
                </div>
            </section>
        </>
    );
};

export default HumanBeingsPage;