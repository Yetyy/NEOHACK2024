import "@/common/components/application/style.scss";
import { TResponseData } from "@/common/types/application";
import Table from "@/common/ui/table/Table";
import { useEffect, useState } from "react";
import Sidebar from "@/common/components/sidebar/Sidebar";
import ModalUser from "@/common/components/application/modal/ModalUser";
import Modal from "@/common/ui/modal/Modal";
import useGetApplications from "@/api/hooks/useGetApplications";
import Loader from "@/common/ui/loader/Loader";
import { applicationHeader } from "@/common/arrays/tableheaders";

const Application = () => {
    const { data, isLoading, isSuccess } = useGetApplications();
    const [content, setContent] = useState<TResponseData[]>([]);
    const [selectedApp, setSelectedApp] = useState<TResponseData>();
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (data && isSuccess) {
            setContent(data)
        }
    }, [data, isSuccess]);

    const handleOpen = (id: number | string) => {
        const application = data?.find((item) => item.id == id);
        setSelectedApp(application);
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <section className="application">
            <h2 className="application__title">Заявки</h2>

            <div className="application__wrapper">
                <Sidebar setData={setContent} />

                {isLoading
                    ? <Loader />
                    : <div className="application__content">
                        <Table<TResponseData>
                            header={applicationHeader}
                            content={content}
                            onOpen={handleOpen}
                        />
                    </div>
                }
            </div>

            <Modal isOpen={open} onClose={handleClose}>
                {selectedApp
                    ? <ModalUser
                        onClose={handleClose}
                        application={selectedApp}
                    />
                    : "Ошибка"
                }
            </Modal>
        </section>
    )
};

export default Application;