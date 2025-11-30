import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';
import {useState} from "react";
import {motion} from "framer-motion";
import type { SortRule } from './GenericSortDialog.tsx';

const MySwal = withReactContent(Swal);

interface SortButtonProps {
    currentSorts: SortRule[];
    onSortsUpdate: (sorts: SortRule[]) => void;
    dialogComponent: React.ComponentType<{ currentSorts: SortRule[]; onSave: (sorts: SortRule[]) => void }>;
}

const SortButton = ({ currentSorts, onSortsUpdate, dialogComponent: DialogComponent }: SortButtonProps) => {
    const [isHovered, setIsHovered] = useState(false);

    const handleOpenDialog = () => {
        MySwal.fire({
            html: <DialogComponent currentSorts={currentSorts} onSave={onSortsUpdate} />,
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
            <div style={{ margin: "5px 25px" }}>
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
                        className={"sort_box"}
                        style={{
                            fontSize: "var(--font-size-accent)",
                            zIndex: 1,
                            fontFamily: "var(--font-family-primary)"
                        }}
                        onMouseEnter={() => setIsHovered(true)}
                        onMouseLeave={() => setIsHovered(false)}
                    >
                        SORTING
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default SortButton;