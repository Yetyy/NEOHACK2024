import useGetApplications from "@/api/hooks/useGetApplications";
import { direction } from "@/common/arrays/direction";
import CardChart from "@/common/components/cardChart/CardChart";
import "@/common/components/statistics/style.scss";
import Button from "@/common/ui/button/Button";
import Input from "@/common/ui/input/Input";
import React from "react";
const DirectionPieChart = React.lazy(() => import('@/common/components/charts/PieChart'));

const Statistics = () => {
    const { data } = useGetApplications();

    const result = direction.map(position => {
        if (data) {
            const count = data.filter(item => item.directionId === position).length;
            return { directionId: position, count };
        } else {
            return { directionId: position, count: 0 };
        }
    });
    return (
        <div className="statistics">
            <h2 className="statistics__title">Статистика</h2>

            <div className="statistics__wrapper">
                {result.map((item, index) => (
                    <CardChart key={index} title={item.directionId} count={item.count} />
                ))}
            </div>


            <div className="statistics__footer">
                <div className="statistics__file-wrap">
                    <h4 className="statistics__file-title">Получить отчет за выбранный период</h4>

                    <div className="statistics__inputs-wrap">
                        <Input
                            type="date"
                            containerClass="statistics__input"
                        />
                        <Input
                            type="date"
                            containerClass="statistics__input"
                        />
                    </div>

                    <Button classes="statistics__file-btn">
                        Получить
                    </Button>
                </div>

                <div className="statistics__pie-chart">
                    <DirectionPieChart data={result} />
                </div>
            </div>
        </div>
    )
};

export default Statistics;