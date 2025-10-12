import React from "react";
import { motion } from "framer-motion";
import "../variables.css";

const Page2: React.FC = () => {
    return (
        <section>
            <motion.h1
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2, duration: 0.5 }}
                style={{ fontSize: "var(--font-size-accent)" }}
            >
                Содержимое Страницы 2
            </motion.h1>
            <motion.p
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4, duration: 0.5 }}
                style={{ fontSize: "var(--font-size-general)" }}
            >
                Это вторая страница с другим контентом. Анимации делают переход плавным и приятным.
            </motion.p>
            <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.6, duration: 0.5 }}
                style={{
                    padding: "var(--spacing-lg)",
                    borderRadius: "8px",
                    marginTop: "var(--spacing-lg)",
                    fontSize: "var(--font-size-general)"
                }}
            >
                <p>Этот блок также имеет красивую анимацию появления!</p>
            </motion.div>
        </section>
    );
};

export default Page2;