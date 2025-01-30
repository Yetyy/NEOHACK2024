import "@/common/ui/accordion/style.scss";
import arrowUp from "/public/images/arrow_up.svg";
import { ReactNode } from "react";

interface IAccordionProps {
    summary: string;
    children: ReactNode;
    name?: string;
    detailsClass?: string;
    summaryClass?: string;
    textClass?: string;
};

const Accordion = ({ summary, children, name, detailsClass = '', summaryClass = '', textClass = '' }: IAccordionProps) => {
    return (
        <div className="accordion">
            <details name={name} className={"accordion__details " + detailsClass}>
                <summary className={"accordion__summary " + summaryClass}>
                    {summary}
                    <img src={arrowUp} className="accordion__icon" alt="arrow" />
                </summary>
            </details>

            <div className="accordion__content" role="definition">
                <div className={"accordion__content-body" + textClass}>
                    {children}
                </div>
            </div>
        </div>
    )
};

export default Accordion;