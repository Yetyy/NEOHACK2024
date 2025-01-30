import { PureComponent } from 'react';
import { PieChart, Pie, Sector, ResponsiveContainer } from 'recharts';

interface ChartDataItem {
  directionId: string;
  count: number;
}

interface Props {
  data: ChartDataItem[];
}

interface State {
  activeIndex: number;
}

const renderActiveShape = (props: any) => {
  const RADIAN = Math.PI / 180;
  const { 
    cx, 
    cy, 
    midAngle, 
    innerRadius, 
    outerRadius, 
    startAngle, 
    endAngle, 
    fill, 
    payload, 
    percent, 
    value 
  } = props;
  
  const sin = Math.sin(-RADIAN * midAngle);
  const cos = Math.cos(-RADIAN * midAngle);
  const sx = cx + (outerRadius + 10) * cos;
  const sy = cy + (outerRadius + 10) * sin;
  const mx = cx + (outerRadius + 30) * cos;
  const my = cy + (outerRadius + 30) * sin;
  const ex = mx + (cos >= 0 ? 1 : -1) * 22;
  const ey = my;
  const textAnchor = cos >= 0 ? 'start' : 'end';

  return (
    <g>
      <text x={cx} y={cy} dy={8} textAnchor="middle" fill={fill}>
        {payload.name}
      </text>
      <Sector
        cx={cx}
        cy={cy}
        innerRadius={innerRadius}
        outerRadius={outerRadius}
        startAngle={startAngle}
        endAngle={endAngle}
        fill={fill}
      />
      <Sector
        cx={cx}
        cy={cy}
        startAngle={startAngle}
        endAngle={endAngle}
        innerRadius={outerRadius + 6}
        outerRadius={outerRadius + 10}
        fill={fill}
      />
      <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
      <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
      <text 
        x={ex + (cos >= 0 ? 1 : -1) * 12} 
        y={ey} 
        textAnchor={textAnchor} 
        fill="#333"
      >
        {`Количество: ${value}`}
      </text>
      <text 
        x={ex + (cos >= 0 ? 1 : -1) * 12} 
        y={ey} 
        dy={18} 
        textAnchor={textAnchor} 
        fill="#999"
      >
        {`(Доля: ${(percent * 100).toFixed(2)}%)`}
      </text>
    </g>
  );
};

export default class DirectionPieChart extends PureComponent<Props, State> {
  state: State = {
    activeIndex: 0,
  };

  onPieEnter = (_: any, index: number) => {
    this.setState({
      activeIndex: index,
    });
  };

  render() {
    const pieData = this.props.data.map(item => ({
      name: item.directionId,
      value: item.count
    }));

    return (
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            activeIndex={this.state.activeIndex}
            activeShape={renderActiveShape}
            data={pieData}
            cx="50%"
            cy="50%"
            innerRadius={90}
            outerRadius={100}
            fill="#8884d8"
            dataKey="value"
            onMouseEnter={this.onPieEnter}
            paddingAngle={3} 
          />
        </PieChart>
      </ResponsiveContainer>
    );
  }
}