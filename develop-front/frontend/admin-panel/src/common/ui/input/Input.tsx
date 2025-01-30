import { InputHTMLAttributes, Ref, forwardRef } from "react";
import "@/common/ui/input/style.scss";
import Label from "@/common/ui/label/Label";

interface IInputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    isRequired?: boolean;
    containerClass?: string;
};

const Input = forwardRef<HTMLInputElement, IInputProps>(
    ({
        label,
        isRequired = false,
        containerClass = "",
        ...props
    }: IInputProps, ref: Ref<HTMLInputElement>) => {

        return (
            <div className={`input__container ${containerClass}`}>
                {label && <Label
                    name={props.name}
                    content={label}
                    isRequired={isRequired} />
                }

                <div className="input__wrapper">
                    <input
                        ref={ref}
                        {...props}
                        className={`input ${props.className ? props.className : ""}`}
                    />
                </div>
            </div>
        )
    }
);

export default Input;