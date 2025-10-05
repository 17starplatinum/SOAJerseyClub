import {motion} from "framer-motion";
import React from "react";

interface TableRowProps {
    index: number;
}

const TableRow: React.FC<TableRowProps> = ({index}) => {
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
                border: "solid gray 3px",
                borderRadius: "10px",
                margin: "5px 0",
                overflowX: "auto"
            }}
        >
            <div style={{...cellStyle, minWidth: "50px", maxWidth:"80px", overflowX: "auto"}}>1000000</div>
            <div style={{...cellStyle, minWidth: "220px", maxWidth:"240px", overflowX: "auto"}}>2025-10-03 17:59:25</div>
            <div style={{...cellStyle, minWidth: "100px", maxWidth:"220px", overflowX: "auto"}}>Орчиков Даниил Валерьевич</div>
            <div style={{...cellStyle, minWidth: "100px", maxWidth:"180px", overflowX: "auto"}}>1000090000; 1000000</div>
            <div style={{...cellStyle, minWidth: "20px"}}>Реальный герой</div>
            <div style={{...cellStyle, minWidth: "20px"}}>Есть зубочистка</div>
            <div style={{...cellStyle, minWidth: "100px", maxWidth:"180px", overflowX: "auto"}}>5894848484848</div>
            <div style={{...cellStyle, minWidth: "200px"}}>MACHINE_GUN</div>
            <div style={{...cellStyle, minWidth: "100px", overflowX: "auto"}}>team</div>
            <div style={{...cellStyle, minWidth: "130px"}}>SADNESS</div>
            <div style={{...cellStyle, minWidth: "100px", overflowX: "auto"}}>Car</div>
        </motion.div>
    );
};

export default TableRow;