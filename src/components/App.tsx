import { Routes, Route, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import Navigation from "./Navigation";
import HumanBeingsPage from "../pages/HumanBeingsPage.tsx";
import Page2 from "../pages/Page2";
import React from "react";
import ScrollToTopButton from "./ScrollToTopButton.tsx";

const pageVariants = {
    initial: {
        opacity: 0
    },
    in: {
        opacity: 1
    },
    out: {
        opacity: 0
    }
};



const App: React.FC = () => {
    const location = useLocation();

    return (
        <div>
            <Navigation/>
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
                                <Page2/>
                            </motion.div>
                        }
                    />
                    <Route path="/" element={<HumanBeingsPage/>}/>
                </Routes>
            </AnimatePresence>

            <ScrollToTopButton/>
        </div>
    );
};

export default App;