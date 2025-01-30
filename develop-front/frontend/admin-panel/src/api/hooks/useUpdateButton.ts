import { useMutation } from '@tanstack/react-query';
import { TButton } from '@/common/types/button';
import { updateButton } from '@/api/services/button';

type TUpdateButton = {
    type: string;
    button: TButton;
};

export const useUpdateButton = () => {
    return useMutation<TButton, Error, TUpdateButton>({
        mutationFn: ({ type, button }) => updateButton(type, button),
        onSuccess: () => {
            alert("Успешно сохранено");
        },
        onError: () => {
            alert('Ошибка обновения');
        },
    });
};