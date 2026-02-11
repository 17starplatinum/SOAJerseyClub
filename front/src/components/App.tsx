import { Routes, Route, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import Navigation from "./Navigation.tsx";
import HumanBeingsPage from "../pages/HumanBeingsPage.tsx";
import TeamsPage from "../pages/TeamsPage.tsx";
import React, { useEffect } from "react";
import ScrollToTopButton from "./ScrollToTopButton.tsx";
import toast, { Toaster } from 'react-hot-toast';

const pageVariants = {
    initial: { opacity: 0 },
    in: { opacity: 1 },
    out: { opacity: 0 }
};

const App: React.FC = () => {
    const location = useLocation();

    useEffect(() => {
        toast.dismiss();
    }, [location.pathname]);

    return (
        <div>
            <Navigation/>

            <Toaster
                position="top-right"
                toastOptions={{
                    duration: 4000,
                    style: {
                        background: 'var(--color-primary)',
                        color: 'var(--color-light)',
                        fontSize: 'var(--font-size-general)',
                        fontFamily: 'var(--font-family-primary)',
                        border: 'var(--border-width) var(--border-style) var(--color-black)',
                        borderRadius: 'var(--border-radius)',
                    },
                }}
            />

            <AnimatePresence mode="wait">
                <Routes location={location} key={location.pathname}>
                    <Route
                        path="/page1"
                        element={
                            <motion.div
                                initial="initial"
                                animate="in"
                                exit="out"
                                variants={pageVariants}
                            >
                                <HumanBeingsPage/>
                            </motion.div>
                        }
                    />
                    <Route
                        path="/page2"
                        element={
                            <motion.div
                                initial="initial"
                                animate="in"
                                exit="out"
                                variants={pageVariants}
                            >
                                <TeamsPage/>
                            </motion.div>
                        }
                    />
                    <Route path="/" element={<HumanBeingsPage/>}/>
                </Routes>
            </AnimatePresence>

            <ScrollToTopButton/>

            <footer style={{height: "20px"}}></footer>
        </div>
    );
};

export default App;