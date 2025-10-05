import {motion} from "framer-motion";
// import {type HumanBeingFullSchema, Mood, WeaponType} from "../types";
import React, {useState} from "react";
import {type HumanBeingFullSchema, Mood, WeaponType} from "../humanBeingAPI.ts";

interface Props {
    human: HumanBeingFullSchema;
}

const Panel = ({human}: Props) => {
    const [isHovered, setIsHovered] = useState(false);
    const getWeaponImage = (weaponType: WeaponType | null) => {
        switch (weaponType) {
            case WeaponType.AXE:
                return "/axe.png";
            case WeaponType.SHOTGUN:
                return "/shotgun.png";
            case WeaponType.MACHINE_GUN:
                return "/machine_gun.png";
        }
    };
    const getMoodImage = (mood: Mood | null) => {
        switch (mood) {
            case Mood.SADNESS:
                return "/sadness.png";
            case Mood.SORROW:
                return "/sorrow.png";
            case Mood.GLOOM:
                return "/gloom.png";
            case Mood.APATHY:
                return "/apathy.png";
            case Mood.RAGE:
                return "/rage.png";
        }
    };

    const cardStyle: React.CSSProperties = {
        margin: "15px",
        border: human.realHero ? "6px ridge #e13d60" : "solid black 6px",
        boxShadow: isHovered ? "0 10px 15px #8c5f66" : "none",
        padding: "10px",
        position: "relative",
        width: "400px",
        transition: "all 0.3s ease",
        cursor: "pointer"
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("ru-RU");
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
                fontSize: "24px",
                maxWidth: "min-content"
            }}
        >
            <motion.div
                initial={false}
                animate={{
                    y: isHovered ? -10 : 0,
                    scale: isHovered ? 1.02 : 1
                }}
                transition={{
                    times: [0, 0.9, 1]
                }}
            >
                <div
                    style={cardStyle}
                    onMouseEnter={() => setIsHovered(true)}
                    onMouseLeave={() => setIsHovered(false)}
                >
                    <div style={{display: "flex"}}>
                        {human.mood && (
                            <div>
                                <motion.img
                                    initial={{opacity: 0, y: -20, scale: 3}}
                                    animate={{opacity: 1, y: 0, scale: 1}}
                                    transition={{
                                        delay: 0.2,
                                        duration: 0.5,
                                        ease: [0.175, 0.885, 0.42, 1.2]
                                    }}
                                    height="128"
                                    src={getMoodImage(human.mood)}
                                    alt={human.mood}
                                    title={human.mood}
                                />
                            </div>
                        )}
                        <div style={{
                            marginLeft: "30px",
                            maxWidth: "240px",
                            fontSize: "32px",
                            overflowX: "auto"
                        }}>{human.name}</div>
                    </div>
                    <div>Real Hero: <img src={human.realHero ? "/true.png" : "/false.png"}
                                         alt={human.realHero ? "true" : "false"}
                                         title={human.realHero ? "true" : "false"}
                                         height="40px"
                                         style={{
                                             transform: human.realHero ? "translate(5px, -10px)" : "translate(5px, -5px)",
                                             position: "absolute"
                                         }}/>
                    </div>
                    <div>Has Toothpick: <img src={human.hasToothpick ? "/true.png" : "/false.png"}
                                             alt={human.hasToothpick ? "true" : "false"}
                                             title={human.hasToothpick ? "true" : "false"}
                                             height="40px"
                                             style={{
                                                 transform: human.hasToothpick ? "translate(5px, -10px)" : "translate(5px, -5px)",
                                                 position: "absolute"
                                             }}/>
                    </div>
                    <div>Impact Speed: {human.impactSpeed}</div>
                    <div>Team: {human.team?.name || "No team"}</div>

                    {human.weaponType && (
                        <div style={{
                            transform: human.weaponType === WeaponType.MACHINE_GUN ? "translate(200px, -80px)" : "translate(250px, -110px)",
                            position: "absolute",
                            zIndex: 1
                        }}>
                            <motion.img
                                initial={{opacity: 0, y: -20}}
                                animate={{
                                    opacity: 1,
                                    y: 0,
                                    rotate: [0, 360],
                                }}
                                transition={{
                                    delay: 0.2,
                                    duration: 0.4,
                                    rotate: {
                                        ease: [0.7, -1.2, 0.58, 1.4],
                                        duration: 1.3,
                                    },
                                }}
                                height="128"
                                src={getWeaponImage(human.weaponType)}
                                alt={human.weaponType}
                                title={human.weaponType}
                            />
                        </div>
                    )}

                    <div>Car: {human.car ? "Yes" : "No"}</div>
                    {human.car && (
                        <>
                            <div>Cool: {human.car.cool === null ? "Unknown" : human.car.cool ? "Yes" : "No"}</div>
                            <div>Model: {human.car.model || "No model"}</div>
                            <div>Color: {human.car.color}</div>
                        </>
                    )}

                    {human.coordinates && <div>Coordinates: ({human.coordinates.x}, {human.coordinates.y})</div>}
                    {human.creationDate && <div>Created: {formatDate(human.creationDate)}</div>}
                </div>
            </motion.div>
        </motion.div>
    );
};

export default Panel;