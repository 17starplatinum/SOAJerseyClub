import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';
import FilterDialog from './FilterDialog.tsx';
import {useState} from "react";
import {motion} from "framer-motion";

const MySwal = withReactContent(Swal);

interface Filter {
    id: number;
    field: string;
    operation: string;
    value: string;
}

interface FilterButtonProps {
    currentFilters: Filter[];
    onFiltersUpdate: (filters: Filter[]) => void;
}

const FilterButton = ({ currentFilters, onFiltersUpdate }: FilterButtonProps) => {
    const [isHovered, setIsHovered] = useState(false);

    const handleOpenDialog = () => {
        MySwal.fire({
            title: `<p style="font-size: var(--font-size-xl); margin: 0; margin-bottom: -40px; font-family: var(--font-family-accent); color: var(--color-accent)">Filters</p>`,
            html: <FilterDialog currentFilters={currentFilters} onSave={onFiltersUpdate} />,
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
        <div onClick={handleOpenDialog}>
            <div style={{margin: "5px 25px"}}>
                <motion.div
                    style={{
                        position: "relative",
                        display: "inline-block",
                        filter: isHovered ? `drop-shadow(var(--shadow-hover))` : "none",
                        transition: `filter var(--transition-normal) ease`,
                        cursor: "pointer"
                    }}
                    initial={false}
                    whileHover={{
                        y: -5,
                        scale: 1.01
                    }}
                    transition={{
                        times: [0, 0.9, 1]
                    }}
                >
                    <div
                        className={"skew"}
                        style={{
                            fontSize: "var(--font-size-accent)",
                            zIndex: 1,
                            fontFamily: "var(--font-family-primary)"
                        }}
                        onMouseEnter={() => setIsHovered(true)}
                        onMouseLeave={() => setIsHovered(false)}
                    >
                        FILTERS
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default FilterButton;