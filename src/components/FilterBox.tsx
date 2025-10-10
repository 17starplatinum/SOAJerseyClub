import "../filterBox.css";
import {useState} from "react";
import {motion} from "framer-motion";

interface Props {
    name: string;
    onRemove: () => void;
}

const FilterBox = ({name, onRemove}: Props) => {
    const [isHovered, setIsHovered] = useState(false);

    const handleClick = () => {
        onRemove();
    };

    return (
        <div style={{margin: "5px 25px"}}>
            <motion.div
                style={{
                    position: "relative",
                    display: "inline-block",
                    filter: isHovered ? "drop-shadow(0 10px 10px #8c5f66)" : "none",
                    transition: "filter 0.3s ease",
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
                onClick={handleClick}
            >
                <div
                    className={"skew"}
                    style={{fontSize: "25px", zIndex: 1}}
                    onMouseEnter={() => setIsHovered(true)}
                    onMouseLeave={() => setIsHovered(false)}
                >
                    {name}
                </div>
            </motion.div>
        </div>
    )
}

export default FilterBox;