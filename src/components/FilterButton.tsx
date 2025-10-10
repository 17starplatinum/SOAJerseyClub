import FilterBox from "./FilterBox.tsx";
import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';
import FilterDialog from './FilterDialog.tsx';

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
    const handleOpenDialog = () => {
        MySwal.fire({
            title: '<p style="font-size: 64px; margin: 0; margin-bottom: -40px; font-family: BadaBoom, Arial, sans-serif; color: #e13d60">ФИЛЬТРЫ</p>',
            html: <FilterDialog currentFilters={currentFilters} onSave={onFiltersUpdate} />,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: true,
            allowEscapeKey: true,
            background: "repeating-linear-gradient(45deg, #fff899, #fff899 50px, #fff785 50px, #fff785 100px)",
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
            <div onClick={handleOpenDialog}>
                <FilterBox name={"ФИЛЬТРЫ"} onRemove={() => {}} />
            </div>
        </>
    );
};

export default FilterButton;