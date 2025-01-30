import "@/common/ui/modal/style.scss";
import { createPortal } from "react-dom";
import { ReactNode, useEffect, useState } from "react";
import Button from "@/common/ui/button/Button";
import close from '/public/images/close.svg';

interface IModalProps {
    isOpen: boolean;
    children: ReactNode;
    onClose: () => void;
    withCloseIcon?: boolean;
};

const Modal = ({ isOpen, children, onClose, withCloseIcon = true }: IModalProps) => {
    const [isVisible, setIsVisible] = useState(false);

    const closeModal = () => {
        setIsVisible(false);

        setTimeout(() => {
            onClose();
        }, 300);
    };

    useEffect(() => {
        if (isOpen) {
            setIsVisible(true);
        } else {
            setIsVisible(false);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    return createPortal(
        <div
            className={`modal__overlay ${isVisible ? "modal__overlay_open" : ""}`}
            onClick={closeModal}
            data-testid='overlay'
        >
            <div
                className={`modal__content ${isVisible ? "modal__content_open" : ""}`}
                onClick={(e) => e.stopPropagation()}
            >
                {withCloseIcon && (
                    <Button
                        classes="modal__close-btn"
                        onClick={closeModal}
                    >
                        <img src={close} alt='close' height={18} width={18}/>
                    </Button>
                )}
                {children}
            </div>
        </div>,
        document.getElementById('root')!
    )
};

export default Modal;