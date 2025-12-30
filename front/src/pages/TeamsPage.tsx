import React, { useEffect, useState } from "react";
import TeamPanel from "../components/teams/TeamPanel.tsx";
import { useTeams } from "../hooks/useTeams.ts";
import { motion, AnimatePresence } from "framer-motion";
import FilterBox from "../components/FilterBox.tsx";
import FilterButton from "../components/FilterButton.tsx";
import AnimatedSelect from "../components/AnimatedSelect.tsx";
import "../styles/variables.css";
import CreateTeamDialog from '../components/teams/CreateTeamDialog.tsx';
import withReactContent from 'sweetalert2-react-content';
import Swal from "sweetalert2";
import type { TeamFullSchema } from "../heroAPI.ts";
import SortButton from "../components/SortButton.tsx";
import SortBox from "../components/SortBox.tsx";
import TeamsFilterDialog from "../components/teams/TeamsFilterDialog.tsx";
import TeamsSortDialog from "../components/teams/TeamsSortDialog.tsx";
import {type Filter, getOperationSymbol} from "../components/GenericFilterDialog.tsx";
import type {SortRule} from "../components/GenericSortDialog.tsx";
import toast from 'react-hot-toast';

const MySwal = withReactContent(Swal);

const TeamsPage: React.FC = () => {
    const [filters, setFilters] = useState<Filter[]>(() => {
        const savedFilters = localStorage.getItem("teamsFilters");
        return savedFilters ? JSON.parse(savedFilters) : [];
    });

    const [sorts, setSorts] = useState<SortRule[]>(() => {
        const savedSorts = localStorage.getItem("teamsSorts");
        return savedSorts ? JSON.parse(savedSorts) : [];
    });

    const {
        teams,
        loading,
        page,
        pageSize,
        totalPages,
        totalCount,
        setPage,
        setPageSize,
        loadTeams,
        updateFilters,
        updateSorts,
        deleteTeam,
    } = useTeams(filters, sorts);

    // Опционально: очистка тостов при размонтировании страницы
    useEffect(() => {
        return () => {
            toast.dismiss();
        };
    }, []);

    useEffect(() => {
        localStorage.setItem("teamsFilters", JSON.stringify(filters));
    }, [filters]);

    useEffect(() => {
        localStorage.setItem("teamsSorts", JSON.stringify(sorts));
    }, [sorts]);

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
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-success)">Create Team</p>`,
            html: <CreateTeamDialog onSuccess={() => { loadTeams(); }} />,
            width: 500,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: true,
            allowEscapeKey: true,
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            showClass: { popup: 'animate__animated animate__fadeInDown' },
            hideClass: { popup: 'animate__animated animate__fadeOutUp' },
            customClass: { popup: 'custom-swal' }
        });
    };

    const handleOpenEditDialog = (team: TeamFullSchema) => {
        MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-primary)">Edit Team</p>`,
            html: <CreateTeamDialog onSuccess={() => { loadTeams(); }} editingTeam={team} />,
            width: 500,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: true,
            allowEscapeKey: true,
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            showClass: { popup: 'animate__animated animate__fadeInDown' },
            hideClass: { popup: 'animate__animated animate__fadeOutUp' },
            customClass: { popup: 'custom-swal' }
        });
    };

    return (
        <>
            <section>
                <div style={{display: "flex", justifyContent: "center"}}>
                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap", width: "70%"}}>
                        <FilterButton
                            onFiltersUpdate={onFiltersUpdate}
                            currentFilters={filters}
                            dialogComponent={TeamsFilterDialog}
                        />
                        {filters.map((filter) => (
                            <FilterBox
                                key={filter.id}
                                name={`${filter.field} ${getOperationSymbol(filter.operation)} ${filter.value}`}
                            />
                        ))}

                        <SortButton
                            onSortsUpdate={onSortsUpdate}
                            currentSorts={sorts}
                            dialogComponent={TeamsSortDialog}
                        />
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
                            onClick={() => loadTeams()}
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
                            Create Team
                        </motion.button>
                    </div>

                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        <AnimatePresence>
                            {teams.map((team) => (
                                <motion.div
                                    key={team.id}
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
                                    <TeamPanel
                                        key={team.id}
                                        team={team}
                                        onDelete={() => deleteTeam(team.id!)}
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

export default TeamsPage;