import {motion} from "framer-motion";
import React from "react";

interface ButtonProps {
    children: React.ReactNode;
    onClick?: () => void;
    disabled?: boolean;
    type?: 'button' | 'submit' | 'reset';
    variant?: 'primary' | 'success' | 'danger' | 'gray';
    className?: string;
    style?: React.CSSProperties;
}

const Button: React.FC<ButtonProps> = ({
                                           children,
                                           onClick,
                                           disabled = false,
                                           type = 'button',
                                           variant = 'primary',
                                           className = '',
                                           style = {}
                                       }) => {
    const baseClasses = `btn btn-${variant} ${className}`.trim();

    return (
        <motion.button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={baseClasses}
            style={style}
            initial={{boxShadow: "none"}}
            whileHover={disabled ? {} : {
                y: -5,
                scale: 1.01,
                boxShadow: "var(--shadow-hover)"
            }}
            whileTap={disabled ? {} : {scale: 0.95}}
            transition={{times: [0, 0.9, 1]}}
        >
            {children}
        </motion.button>
    );
};

export default Button;