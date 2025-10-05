import React from 'react';
import Panel from '../components/Panel';
import {useHumanBeings} from '../hooks/useHumanBeings';
import {motion} from 'framer-motion';
import FilterBox from "../components/FilterBox.tsx";

const HumanBeingsPage: React.FC = () => {
    const {humanBeings, loading, error, loadHumanBeings} = useHumanBeings();

    return (
        <>
            <section>
                <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                    <div style={{width: "60%"}}>
                        <FilterBox name={"Filter"}/>
                        <FilterBox name={"id = 5"}/>
                    </div>
                </div>
            </section>
            <section>
                <div style={{padding: '20px'}}>
                    <motion.button
                        onClick={loadHumanBeings}
                        disabled={loading}
                        initial={{opacity: 0, y: -10}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.3}}
                        style={{
                            padding: '10px 20px',
                            fontSize: '16px',
                            backgroundColor: loading ? '#ccc' : '#007bff',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            cursor: loading ? 'not-allowed' : 'pointer'
                        }}
                    >
                        {loading ? 'Загрузка...' : 'Загрузить данные'}
                    </motion.button>

                    {error && (
                        <motion.div
                            initial={{opacity: 0}}
                            animate={{opacity: 1}}
                            style={{color: 'red', marginTop: '10px'}}
                        >
                            Ошибка: {error}
                        </motion.div>
                    )}
                    <div style={{display: "flex", justifyContent: "center", flexWrap: "wrap"}}>
                        {humanBeings.map(human => (
                            <Panel key={human.id} human={human}/>
                        ))}
                    </div>
                </div>
            </section>
        </>
    );
};

export default HumanBeingsPage;