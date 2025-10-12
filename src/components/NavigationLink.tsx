import {motion} from "framer-motion";
import {NavLink} from "react-router-dom";
import React from "react";

const NavigationLink: React.FC<{ to: string, title: string }> = ({to, title}) => {
    const linkStyle: React.CSSProperties = {
        margin: "0 var(--spacing-lg)",
        textDecoration: "none",
        fontSize: "var(--font-size-2xl)",
        fontFamily: "var(--font-family-accent)",
        fontWeight: "500",
        padding: "var(--spacing-sm) var(--spacing-lg)",
        position: "relative"
    };

    const inactiveLinkStyle: React.CSSProperties = {
        ...linkStyle,
        transition: "all var(--transition-normal) ease",
        fontSize: "var(--font-size-xl)"
    };

    const activeLinkStyle: React.CSSProperties = {
        ...linkStyle,
        transition: "all var(--transition-normal) ease"
    };

    return (
        <NavLink to={to}
                 style={{textDecoration: "none"}}
        >
            {({isActive}) => (
                <motion.div
                    style={isActive ? activeLinkStyle : inactiveLinkStyle}
                    transition={{duration: 0.3, ease: "easeInOut"}}
                >
                    <motion.span
                        style={{
                            color: isActive ? "var(--color-accent)" : "var(--color-gray)",
                            position: "relative",
                            zIndex: 1
                        }}
                        whileHover={{color: isActive ? "var(--color-accent)" : "#495057"}}
                        transition={{duration: 0.2}}
                    >
                        {title}
                    </motion.span>
                </motion.div>
            )}
        </NavLink>
    )
}

export default NavigationLink;