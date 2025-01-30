import { TResponseData } from "@/common/types/application";
import Accordion from "@/common/ui/accordion/Accordion";
import Button from "@/common/ui/button/Button";
import Checkbox from "@/common/ui/checkbox/Checkbox";
import { memo, useState } from "react";
import "@/common/components/sidebar/style.scss";
import useGetApplications from "@/api/hooks/useGetApplications";
import { direction } from "@/common/arrays/direction";
import { statusApp } from "@/common/arrays/statusApp";

type TSidebarProps = {
    setData: (_: TResponseData[]) => void;
};

const Sidebar = memo(({ setData }: TSidebarProps) => {
    const { data: content } = useGetApplications();

    const [selectedStatuses, setSelectedStatuses] = useState<string[]>([]);
    const [selectedPositions, setSelectedPositions] = useState<string[]>([]);

    const handleClick = () => {
        let filteredData: TResponseData[] = [];
        if (content) {
            filteredData = content.filter((item) => {
                const matchesStatus =
                    selectedStatuses.length === 0 || selectedStatuses.includes(item.status);
                const matchesPosition =
                    selectedPositions.length === 0 || selectedPositions.includes(item.directionId);
                return matchesStatus && matchesPosition;
            });
        }
        setData(filteredData);
    }

    const handleStatusChange = (status: string, isChecked: boolean) => {
        setSelectedStatuses((prev) =>
            !isChecked ? [...prev, status] : prev.filter((s) => s !== status)
        );
    };

    const handlePositionChange = (position: string, isChecked: boolean) => {
        setSelectedPositions((prev) =>
            !isChecked ? [...prev, position] : prev.filter((p) => p !== position)
        );
    };

    return (
        <aside className="sidebar__settings">
            <p className="sidebar__settings-title">Параметры</p>

            <Accordion summary="Статус">
                <div className="sidebar__wrapper">
                    {statusApp.map((item, index) => (
                        <Checkbox
                            key={index}
                            name={item}
                            label={item}
                            onChecked={(isChecked) => handleStatusChange(item, isChecked)}
                        />
                    ))}
                </div>
            </Accordion>

            <Accordion summary="Направление">
                <div className="sidebar__wrapper">
                    {direction.map((item, index) => (
                        <Checkbox
                            key={index}
                            name={item}
                            label={item}
                            onChecked={(isChecked) => handlePositionChange(item, isChecked)}
                        />
                    ))}
                </div>
            </Accordion>

            <Button classes="sidebar__settings-button" onClick={handleClick}>
                Применить
            </Button>
        </aside>
    )
});

export default Sidebar;