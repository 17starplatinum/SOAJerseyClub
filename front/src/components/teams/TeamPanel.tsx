import {motion} from "framer-motion";
import React, {useState} from "react";
import type { TeamFullSchema } from "../../heroAPI.ts";
import "../../styles/variables.css";

interface Props {
    team: TeamFullSchema;
    onDelete?: () => void;
    onEdit?: (team: TeamFullSchema) => void;
}

const TeamPanel = ({team, onDelete, onEdit}: Props) => {
    const [isHovered, setIsHovered] = useState(false);

    const cardStyle: React.CSSProperties = {
        margin: "var(--spacing-md)",
        border: "var(--border-width-thick) var(--border-style) var(--color-black)",
        boxShadow: isHovered ? "var(--shadow-card)" : "none",
        padding: "var(--spacing-sm)",
        position: "relative",
        width: "280px",
        transition: "all var(--transition-normal) ease",
        cursor: "pointer",
        background: "var(--color-background-primary)",
        fontSize: "var(--font-size-general)"
    };

    const handleClick = () => {
        onEdit?.(team);
    };

    return (
        <motion.div
            initial={{opacity: 0, y: -20}}
            animate={{opacity: 1, y: 0}}
            transition={{
                delay: 0.2,
                duration: 0.5,
            }}
            style={{
                display: "flex",
                justifyContent: "center",
                fontSize: "var(--font-size-general)",
                maxWidth: "min-content",
                cursor: onEdit ? 'pointer' : 'default'
            }}
            onClick={handleClick}
        >
            <motion.div
                initial={false}
                animate={{
                    y: isHovered ? -10 : 0,
                    scale: isHovered ? 1.02 : 1,
                }}
                transition={{
                    times: [0, 0.9, 1],
                }}
            >
                <div style={cardStyle} onMouseEnter={() => setIsHovered(true)} onMouseLeave={() => setIsHovered(false)}>
                    <div style={{display: "flex", alignItems: "center", gap: "var(--spacing-md)"}}>
                        <div style={{
                            fontSize: "var(--font-size-accent)",
                            fontWeight: "bold",
                            maxWidth: "200px",
                            overflowX: "auto",
                        }}>
                            {team.name || `Team #${team.id}`}
                        </div>
                    </div>

                    <div>ID: {team.id}</div>

                    <div style={{display: "flex", justifyContent: "space-between", marginTop: "var(--spacing-md)"}}>
                        <div style={{fontSize: "var(--font-size-sm)"}}>
                            Size: {team.size || "couldn't upload"}
                        </div>
                        <motion.button
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
                            onClick={(e) => {
                                e.stopPropagation();
                                onDelete?.();
                            }}
                            style={{
                                background: "var(--color-accent)",
                                padding: "var(--spacing-xs) var(--spacing-sm)",
                                fontSize: "var(--font-size-general)"
                            }}
                            title="Delete"
                        >
                            Delete
                        </motion.button>
                    </div>
                </div>
            </motion.div>
        </motion.div>
    );
};

export default TeamPanel;