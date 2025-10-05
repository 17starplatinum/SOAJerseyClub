import React, {useEffect, useState} from "react";

const ScrollToTopButton = () => {
    const [isVisible, setIsVisible] = useState(false);

    const handleScrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    };

    useEffect(() => {
        const checkScroll = () => {
            if (window.scrollY > 400) {
                setIsVisible(true);
            } else {
                setIsVisible(false);
            }
        };

        window.addEventListener("scroll", checkScroll);

        return () => {
            window.removeEventListener("scroll", checkScroll);
        };
    }, []);

    const buttonStyle = {
        position: "fixed" as const,
        bottom: "100px",
        right: "60px",
        width: "0",
        height: "0",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        cursor: "pointer",
        transition: "opacity 0.3s ease, transform 0.3s ease",
        opacity: isVisible ? 1 : 0,
        transform: isVisible ? "scale(1)" : "scale(0.5)",
        pointerEvents: isVisible ? "auto" as const : "none" as const,
        zIndex: 1000
    };

    return (
        <button
            style={buttonStyle}
            onClick={handleScrollToTop}
            aria-label="Прокрутить наверх"
        >
            <img
                src="/arrow.png"
                alt="Вверх"
                style={{
                    height: "128px",
                    transform: "rotate(180deg)"
                }}
            />
        </button>
    );
};

export default ScrollToTopButton;