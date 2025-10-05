import {motion} from "framer-motion";
import {NavLink} from "react-router-dom";
import React from "react";

const NavigationLink: React.FC<{ to: string, title: string }> = ({to, title}) => {
    const linkStyle: React.CSSProperties = {
        margin: "0 20px",
        textDecoration: "none",
        fontSize: "96px",
        fontFamily: "BadaBoom, Arial, sans-serif",
        fontWeight: "500",
        padding: "10px 20px",
        position: "relative"
    };
    const inactiveLinkStyle: React.CSSProperties = {
        ...linkStyle,
        transition: "all 0.3s ease",
        fontSize: "82px"
    };
    const activeLinkStyle: React.CSSProperties = {
        ...linkStyle,
        transition: "all 0.3s ease"
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
                            color: isActive ? "#e13d60" : "#6c757d",
                            position: "relative",
                            zIndex: 1
                        }}
                        whileHover={{color: isActive ? "#e13d60" : "#495057"}}
                        transition={{duration: 0.2}}
                    >
                        {title}
                    </motion.span>
                </motion.div>
            )}
        </NavLink>
    )
}

export default NavigationLink