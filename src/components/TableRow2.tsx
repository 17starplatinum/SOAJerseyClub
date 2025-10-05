import {motion} from "framer-motion";
import React from "react";

interface TableRowProps {
    index: number;
}

const TableRow2: React.FC<TableRowProps> = ({index}) => {
    const cellStyle: React.CSSProperties = {
        margin: "5px",
        textAlign: "center"
    }
    return (
        <motion.div
            initial={{opacity: 0, y: -10 * index - 10}}
            animate={{opacity: 1, y: 0}}
            transition={{
                delay: 0.2 + index * 0.1,
                duration: 0.5
            }}
            style={{
                display: "flex",
                justifyContent: "center",
                fontSize: "24px",
                margin: "5px 0"
            }}
        >
            <div style={{
                ...cellStyle,
                minWidth: "100px",
                maxWidth: "120px",
                fontFamily: "BadaBoom, Arial, sans-serif",
                textAlign: "right",
                fontSize: "96px",
                padding: "10px"
            }}>{index}</div>
            <div>
                <div>Орчиков Даниил Валерьевич</div>
                <div>Реальный герой</div>
                <div>MACHINE_GUN</div>
                <div>SADNESS</div>
            </div>
            <div></div>
            <div style={{...cellStyle, maxWidth: "220px", overflowX: "auto"}}>2025-10-03 17:59:25</div>
            <div style={{...cellStyle, minWidth: "100px", maxWidth: "180px", overflowX: "auto"}}>1000090000; 1000000
            </div>
            <div style={{...cellStyle, minWidth: "20px"}}>Есть зубочистка</div>
            <div style={{...cellStyle, minWidth: "100px", maxWidth: "180px", overflowX: "auto"}}>5894848484848</div>
            <div style={{...cellStyle, minWidth: "100px", overflowX: "auto"}}>team</div>
            <div>
                <div>Car</div>
                <div>cool</div>
                <div>color</div>
                <div>model</div>
            </div>
        </motion.div>
    );
};

export default TableRow2;