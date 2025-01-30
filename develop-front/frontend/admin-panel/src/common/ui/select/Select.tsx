import Label from "@/common/ui/label/Label";
import { Ref, SelectHTMLAttributes, forwardRef } from "react";
import "@/common/ui/select/style.scss";
import { TOption } from "@/common/types/option";

interface ISelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
    label?: string;
    isRequired?: boolean;
    containerClass?: string;
    defaultValue?: string;
    options?: TOption[];
};

const Select = forwardRef<HTMLSelectElement, ISelectProps>(
    ({
        label,
        isRequired = true,
        containerClass = "",
        options,
        defaultValue,
        ...props
    }: ISelectProps, ref: Ref<HTMLSelectElement>) => {

        return (
            <div className={`select__container ${containerClass}`}>
                {label && <Label
                    name={props.name}
                    content={label}
                    isRequired={isRequired} />
                }

                <div className="select__wrapper">
                    <select
                        ref={ref}
                        {...props}
                        className={`select ${props.className ? props.className : ""}`}
                        defaultValue={defaultValue} 
                    >
                        {options?.map((item) => (
                            <option key={item.value} value={item.value}>{item.title}</option>
                        ))}
                    </select>
                </div>
            </div>
        )
    }
);

export default Select;