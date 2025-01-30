import "@/common/components/cardChart/style.scss";

type TCardChartProps = {
    title: string;
    count: number;
};

const CardChart = ({ title, count }: TCardChartProps) => {
    return (
        <article className="card-chart">
            <div className="card-chart__wrapper">
                <h3 className="card-chart__title">{title}</h3>
                <p className="card-chart__total">{count} заявки</p>
            </div>
        </article>
    )
};

export default CardChart;