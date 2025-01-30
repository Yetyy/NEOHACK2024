import "@/common/ui/table/style.scss";
import { memo, useEffect, useState } from "react";
import Button from "@/common/ui/button/Button";
import arrow from "/public/images/arrow.svg";

type TTableProps<T> = {
    header: string[];
    content: T[];
    onOpen?: (id: number | string) => void;
};

const Table = <T extends object & { id: string | number }>({ header, content, onOpen }: TTableProps<T>) => {
    const [data, setData] = useState<T[]>(content);
    const [activeCol, setActiveCol] = useState([true, ...Array(header.length - 1).fill(false)]);

    useEffect(() => {
        setData([...content])
    }, [content]);

    const handleNavigate = (id: number | string) => {
        onOpen?.(id);
    };

    const handleClick = (index: number) => {
        const arr = [...data].sort((a, b) => {
            const valueA = Object.values(a)[index]; 
            const valueB = Object.values(b)[index];
    
            const numA = Number(valueA);
            const numB = Number(valueB);
    
            if (isNaN(numA) || isNaN(numB)) {
                if (valueA < valueB) {
                    return activeCol[index] ? 1 : -1;
                }
                if (valueA > valueB) {
                    return activeCol[index] ? -1 : 1;
                }
                return 0;
            }
    
            if (numA < numB) {
                return activeCol[index] ? 1 : -1;
            }
            if (numA > numB) {
                return activeCol[index] ? -1 : 1;
            }
            return 0;
        });
    
        const newActive = Array(header.length).fill(false);
        newActive[index] = !activeCol[index];
    
        setActiveCol([...newActive]);
        setData([...arr]);
    };
    return (
        <div className="table__wrapper">
            <table className="table">
                <thead>
                    <tr>
                        {header.map((item, index) => (
                            <th key={index}>
                                <Button classes="table__button" onClick={() => handleClick(index)}>
                                    {item}
                                    <img
                                        src={arrow}
                                        className={`table__icon ${activeCol[index] ? 'table__icon_active' : ''}`}
                                        alt='arrow'
                                    />
                                </Button>
                            </th>
                        ))}
                    </tr>
                </thead>

                <tbody>
                    {data.map((row, rowIndex) => (
                        <tr key={rowIndex} className="table__content-row" onClick={() => handleNavigate(row.id)}>
                            {Object.values(row).map((headerItem, colIndex) => (
                                <td key={colIndex}>
                                    {headerItem}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default memo(Table) as typeof Table;