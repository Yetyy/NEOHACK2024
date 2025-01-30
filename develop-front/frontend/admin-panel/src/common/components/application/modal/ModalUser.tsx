
import "@/common/components/application/modal/style.scss";
import Button from "@/common/ui/button/Button";
import { ChangeEvent, useState } from "react";
import { TResponseData } from "@/common/types/application";
import useGetUser from "@/api/hooks/useGetUser";
import Loader from "@/common/ui/loader/Loader";
import { useUpdateAppStatus } from "@/api/hooks/useUpdateApp";
import Select from "@/common/ui/select/Select";
import { statusOption } from "@/common/arrays/statusOptions";

type TModalUserProps = {
    onClose?: () => void;
    application: TResponseData;
};

const ModalUser = ({ onClose, application }: TModalUserProps) => {
    const { data, isLoading } = useGetUser(application.userId);
    const { mutate } = useUpdateAppStatus();
    const [selectedStatus, setSelectedStatus] = useState('');

    const handleChange = (e: ChangeEvent<HTMLSelectElement>) => {
        setSelectedStatus(e.target.value);
    };

    const handleSave = () => {
        mutate({
            id: `${application.id}`,
            app: {
                ...application,
                status: selectedStatus
            }
        });
        onClose?.();
    };

    return (
        isLoading || !data
            ? <Loader />
            : <section className="user">
                <h2 className="user__title">Редактирование</h2>

                <ul className="user__list">
                    <li className="user__list-item">
                        <p className="user__item-name">Имя: </p>
                        <p className="user__item-value">{data.firstName}</p>
                    </li>

                    <li className="user__list-item">
                        <p className="user__item-name">Фамилия: </p>
                        <p className="user__item-value">{data.lastName}</p>
                    </li>

                    <li className="user__list-item">
                        <p className="user__item-name">Город: </p>
                        <p className="user__item-value">{data.city}</p>
                    </li>

                    <li className="user__list-item">
                        <p className="user__item-name">Почта: </p>
                        <p className="user__item-value">{data.email}</p>
                    </li>

                    <li className="user__list-item">
                        <p className="user__item-name">Телефон: </p>
                        <p className="user__item-value">{data.phoneNumber}</p>
                    </li>

                    <li className="user__list-item">
                        <p className="user__item-name">Статус: </p>
                        <div className="user__item-value">
                            <Select
                                onChange={handleChange}
                                options={statusOption}
                                defaultValue={application.status}
                                className="user__select"
                            />
                        </div>
                    </li>
                </ul>

                <Button classes="user__button" onClick={handleSave}>
                    Сохранить
                </Button>
            </section>
    )
};

export default ModalUser;