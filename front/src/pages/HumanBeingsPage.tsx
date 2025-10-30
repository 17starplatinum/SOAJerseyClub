import React, {useEffect, useState} from "react";
import HumanBeingPanel from "../components/humanBeings/HumanBeingPanel.tsx";
import {useHumanBeings} from "../hooks/useHumanBeings.ts";
import {motion, AnimatePresence} from "framer-motion";
import FilterBox from "../components/FilterBox.tsx";
import FilterButton from "../components/FilterButton.tsx";
import AnimatedSelect from "../components/AnimatedSelect.tsx";
import {Toaster} from 'react-hot-toast';
import "../styles/variables.css";
import CreateHumanBeingDialog from '../components/humanBeings/CreateHumanBeingDialog.tsx';
import withReactContent from 'sweetalert2-react-content';
import Swal from "sweetalert2";
import type {HumanBeingFullSchema} from "../humanBeingAPI.ts";
import SortButton from "../components/SortButton.tsx";
import SortBox from "../components/SortBox.tsx";
import {HumanBeingService} from "../service/HumanBeingService.ts";
import HumanBeingsFilterDialog from "../components/humanBeings/HumanBeingsFilterDialog.tsx";
import HumanBeingsSortDialog from "../components/humanBeings/HumanBeingsSortDialog.tsx";
import {type Filter, getOperationSymbol} from "../components/GenericFilterDialog.tsx";
import type {SortRule} from "../components/GenericSortDialog.tsx";
import { TeamService } from "../service/TeamService.ts";
import {useToast} from "../hooks/useToast.ts";

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

    const [searchMode, setSearchMode] = useState(false);
    const [searchResults, setSearchResults] = useState<HumanBeingFullSchema[]>([]);

    const { showLoading, updateToast, showError  } = useToast();

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
        updateSorts,
        deleteHumanBeing,
    } = useHumanBeings(filters, sorts);

    useEffect(() => {
        localStorage.setItem("humanBeingsFilters", JSON.stringify(filters));
    }, [filters]);

    useEffect(() => {
        localStorage.setItem("humanBeingsSorts", JSON.stringify(sorts));
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
        {value: "4", label: "4"},
        {value: "8", label: "8"},
        {value: "32", label: "32"},
        {value: "64", label: "64"}
    ];

    const handleGetUniqueSpeeds = async () => {
        try {
            const uniqueSpeeds = await HumanBeingService.getUniqueImpactSpeeds();

            const speedsText = uniqueSpeeds.length > 0
                ? uniqueSpeeds.join(', ')
                : 'No data available';

            await MySwal.fire({
                title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-primary)">Unique impact speeds</p>`,
                html: `
                <div style="text-align: center; padding: var(--spacing-md); font-family: var(--font-family-primary);">
                    <p style="font-size: var(--font-size-accent); margin-bottom: var(--spacing-md);">
                        Unique speeds found: ${uniqueSpeeds.length}
                    </p>
                    <div style="
                        background: var(--color-light);
                        border: var(--border-width) var(--border-style) var(--color-black);
                        padding: var(--spacing-md);
                        border-radius: var(--border-radius);
                        max-height: 200px;
                        overflow-y: auto;
                        font-family: var(--font-family-primary);
                        font-size: var(--font-size-general);
                    ">
                        ${speedsText}
                    </div>
                </div>
            `,
                width: 500,
                showConfirmButton: true,
                confirmButtonText: 'Закрыть',
                confirmButtonColor: 'var(--color-primary)',
                background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
                showClass: {
                    popup: 'animate__animated animate__fadeInDown'
                },
                hideClass: {
                    popup: 'animate__animated animate__fadeOutUp'
                },
                customClass: {
                    popup: 'custom-swal',
                    confirmButton: 'btn btn-primary'
                }
            });
        } catch (error) {
            console.error('Failed to get unique speeds:', error);
        }
    };

    const handleOpenCreateDialog = () => {
        MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-success)">Create Object</p>`,
            html: <CreateHumanBeingDialog onSuccess={() => {
                loadHumanBeings();
            }}/>,
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

    const handleAssignRedLada = async () => {
        let teams: Awaited<ReturnType<typeof TeamService.getAllTeams>> = [];
        try {
            teams = await TeamService.getAllTeams();
        } catch (error) {
            showError('Не удалось загрузить команды для назначения машины');
            teams = [];
        }

        const { value: selectedTeamId } = await MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-primary)">Assign Red Lada Kalina</p>`,
            html: `
            <div style="text-align: center; padding: var(--spacing-md); font-family: var(--font-family-primary);">
                <p style="font-size: var(--font-size-accent); margin-bottom: var(--spacing-md);">
                    Select team to assign red Lada Kalina to members without cars
                </p>
                <select id="teamSelect" style="
                    width: 100%;
                    padding: var(--spacing-sm);
                    font-family: var(--font-family-primary);
                    font-size: var(--font-size-general);
                    border: var(--border-width) var(--border-style) var(--color-black);
                    border-radius: var(--border-radius);
                    margin-bottom: var(--spacing-md);
                ">
                    <option value="">${teams.length ? 'Select a team' : 'No teams available'}</option>
                    ${teams.map(team =>
                `<option value="${team.id}">${team.name || `Team #${team.id}`}</option>`
            ).join('')}
                </select>
            </div>
        `,
            showCancelButton: true,
            confirmButtonText: 'Assign',
            cancelButtonText: 'Cancel',
            confirmButtonColor: 'var(--color-primary)',
            cancelButtonColor: 'var(--color-accent)',
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            preConfirm: () => {
                const select = document.getElementById('teamSelect') as HTMLSelectElement;
                if (!select.value) {
                    Swal.showValidationMessage('Please select a team');
                    return;
                }
                return select.value;
            }
        });

        if (selectedTeamId) {
            const toastId = showLoading('Assigning red Lada Kalina to team...');
            try {
                await TeamService.assignRedLadaToTeam(Number(selectedTeamId));
                updateToast(toastId, 'success', 'Red Lada Kalina assigned successfully!');
                loadHumanBeings();
            } catch (error) {
                updateToast(toastId, 'error', 'Failed to assign red Lada Kalina');
            }
        }
    };

    const handleSearchHeroes = async () => {
        const { value } = await MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl);margin:0;font-family: var(--font-family-accent); color: var(--color-primary)">Search Heroes</p>`,
            html: `
            <div style="text-align: center; padding: var(--spacing-md); font-family: var(--font-family-primary);">
                <p style="font-size: var(--font-size-accent); margin-bottom: var(--spacing-md);">
                    Search for heroes
                </p>
                <select id="heroSearchSelect" style="
                    width: 100%;
                    padding: var(--spacing-sm);
                    font-family: var(--font-family-primary);
                    font-size: var(--font-size-general);
                    border: var(--border-width) var(--border-style) var(--color-black);
                    border-radius: var(--border-radius);
                    margin-bottom: var(--spacing-md);
                ">
                    <option value="false">All Heroes</option>
                    <option value="true">Real Heroes Only</option>
                </select>
            </div>
        `,
            showCancelButton: true,
            confirmButtonText: 'Search',
            cancelButtonText: 'Cancel',
            confirmButtonColor: 'var(--color-primary)',
            cancelButtonColor: 'var(--color-accent)',
            background: "repeating-linear-gradient(45deg, var(--color-background-primary), var(--color-background-primary) 50px, var(--color-background-secondary) 50px, var(--color-background-secondary) 100px)",
            preConfirm: () => {
                const select = document.getElementById('heroSearchSelect') as HTMLSelectElement;
                return select.value;
            }
        });

        if (value !== undefined) {
            const realHeroOnly = value === 'true';
            const toastId = showLoading('Searching heroes...');
            try {
                const results = await TeamService.getHeroes(realHeroOnly);
                setSearchResults(results);
                setSearchMode(true);
                updateToast(toastId, 'success', `Found ${results.length} heroes`);
            } catch (error) {
                updateToast(toastId, 'error', 'Failed to search heroes');
            }
        }
    };

    const handleBackFromSearch = () => {
        setSearchMode(false);
        setSearchResults([]);
    };

    const displayHumanBeings = searchMode ? searchResults : humanBeings;

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

            {!searchMode && (
                <section>
                    <div style={{display: "flex", justifyContent: "center"}}>
                        <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap", width: "70%"}}>
                            <FilterButton
                                onFiltersUpdate={onFiltersUpdate}
                                currentFilters={filters}
                                dialogComponent={HumanBeingsFilterDialog}
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
                                dialogComponent={HumanBeingsSortDialog}
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
            )}

            <section>
                <div style={{padding: "var(--spacing-lg)"}}>
                    <div style={{
                        padding: "var(--spacing-lg)",
                        display: "flex",
                        gap: "var(--spacing-md)",
                        justifyContent: "center",
                        flexWrap: "wrap"
                    }}>
                        {!searchMode ? (
                            <>
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

                                <motion.button
                                    onClick={handleGetUniqueSpeeds}
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
                                        backgroundColor: loading ? "var(--color-disabled)" : "var(--color-secondary)",
                                        cursor: loading ? "not-allowed" : "pointer",
                                        minWidth: "150px",
                                        fontSize: "var(--font-size-general)",
                                        color: "var(--color-black)"
                                    }}
                                >
                                    Unique Speeds
                                </motion.button>

                                <motion.button
                                    onClick={handleAssignRedLada}
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
                                        backgroundColor: loading ? "var(--color-disabled)" : "var(--color-light-blue)",
                                        cursor: loading ? "not-allowed" : "pointer",
                                        minWidth: "150px",
                                        fontSize: "var(--font-size-general)",
                                        color: "var(--color-black)"
                                    }}
                                >
                                    Assign Red Lada
                                </motion.button>

                                <motion.button
                                    onClick={handleSearchHeroes}
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
                                        backgroundColor: loading ? "var(--color-disabled)" : "var(--color-accent)",
                                        cursor: loading ? "not-allowed" : "pointer",
                                        minWidth: "150px",
                                        fontSize: "var(--font-size-general)"
                                    }}
                                >
                                    Search Heroes
                                </motion.button>
                            </>
                        ) : (
                            <motion.button
                                onClick={handleBackFromSearch}
                                initial={{
                                    boxShadow: "none"
                                }}
                                whileHover={{
                                    y: -5,
                                    scale: 1.01,
                                    boxShadow: "var(--shadow-hover)"
                                }}
                                transition={{
                                    times: [0, 0.9, 1]
                                }}
                                style={{
                                    padding: "var(--spacing-sm) var(--spacing-lg)",
                                    backgroundColor: "var(--color-primary)",
                                    cursor: "pointer",
                                    minWidth: "150px",
                                    fontSize: "var(--font-size-general)"
                                }}
                            >
                                Back to Normal View
                            </motion.button>
                        )}
                    </div>

                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        <AnimatePresence>
                            {displayHumanBeings.map((human) => (
                                <motion.div
                                    key={human.id}
                                    layout
                                    initial={{opacity: 1, scale: 1}}
                                    animate={{opacity: 1, scale: 1}}
                                    exit={{
                                        opacity: 0,
                                        scale: 0.8,
                                        transition: {duration: 0.3}
                                    }}
                                    transition={{duration: 0.2}}
                                >
                                    <HumanBeingPanel
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

                {!searchMode && (
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
                )}
            </section>
        </>
    );
};

export default HumanBeingsPage;