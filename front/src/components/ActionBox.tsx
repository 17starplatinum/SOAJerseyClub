import "../styles/actionBox.css";
import "../styles/variables.css";

interface Props {
    name: string;
    variant?: 'filter' | 'sort';
}

const ActionBox = ({ name, variant}: Props) => {
    return (
        <div style={{ margin: "5px 25px" }}>
            <div>
                <div
                    className={variant == "sort" ? "sort_box" : "filter_box"}
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
    );
};

export default ActionBox;