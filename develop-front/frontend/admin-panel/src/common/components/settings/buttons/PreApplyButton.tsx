import Input from "@/common/ui/input/Input";
import "@/common/components/settings/style.scss";
import Button from "@/common/ui/button/Button";
import useGetButton from "@/api/hooks/useGetButton";
import Loader from "@/common/ui/loader/Loader";
import { useEffect, useState } from "react";
import { useUpdateButton } from "@/api/hooks/useUpdateButton";

const PreApplyButton = () => {
    const { data, isLoading } = useGetButton('pre_apply');
    const { mutate, isPending } = useUpdateButton();

    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");

    useEffect(() => {
        if (data) {
            setStartDate(data.startDate || "");
            setEndDate(data.endDate || "");
        }
    }, [data]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (data && startDate && endDate) {
            mutate({
                type: 'pre_apply',
                button: {
                    ...data,
                    startDate: startDate,
                    endDate: endDate
                }
            });
        }
    };
    return (
        <form onSubmit={handleSubmit} className="setting-activity__wrapper">
            <p className="setting-activity__description">
                Период активности кнопки «Оставить предзаявку»
            </p>

            {isLoading
                ? <Loader style={{ height: '45px', width: '45px', margin: "16px" }} />
                : <div className="setting-activity__inputs-wrap">
                    <Input
                        label="Активна c"
                        type="date"
                        className="setting-activity__input"
                        value={startDate}
                        max={endDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />

                    <Input
                        label="Активна до"
                        type="date"
                        className="setting-activity__input"
                        value={endDate}
                        min={startDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                </div>
            }

            <Button classes="setting-activity__button" type="submit" disabled={isPending}>
                {isPending
                    ? <Loader style={{ height: '18px', width: '18px' }} />
                    : "Сохранить"
                }
            </Button>
        </form>
    )
};

export default PreApplyButton;