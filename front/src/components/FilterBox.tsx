import "../filterBox.css";
import "../variables.css";

interface Props {
    name: string;
}

const FilterBox = ({name}: Props) => {
    return (
        <div style={{margin: "5px 25px"}}>
            <div>
                <div
                    className={"skew"}
                    style={{
                        fontSize: "var(--font-size-accent)",
                        zIndex: 1,
                        fontFamily: "var(--font-family-primary)"
                    }}
                >
                    {name}
                </div>
            </div>
        </div>
    )
}

export default FilterBox;