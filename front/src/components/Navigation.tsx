import React from "react";
import '../fonts.css';
import '../variables.css';
import NavigationLink from "./NavigationLink.tsx";

const Navigation: React.FC = () => {
    const navStyle: React.CSSProperties = {
        display: "flex",
        justifyContent: "center",
        paddingTop: "var(--spacing-lg)",
        minHeight: "170px"
    };

    return (
        <nav style={navStyle}>
            <NavigationLink to="/page1" title="Human Beings"/>
            <NavigationLink to="/page2" title="Teams"/>
        </nav>
    );
};

export default Navigation;