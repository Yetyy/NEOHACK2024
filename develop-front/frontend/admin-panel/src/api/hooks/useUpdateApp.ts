import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TResponseData } from '@/common/types/application';
import { updateAppStatus } from '@/api/services/application';

type TUpdateAppStatus = {
    id: string;
    app: TResponseData;
};

export const useUpdateAppStatus = () => {
    const queryClient = useQueryClient();  

    return useMutation<TResponseData, Error, TUpdateAppStatus>({
        mutationFn: ({ id, app }) => updateAppStatus(id, app),
        onSuccess: (updatedData) => {
            queryClient.setQueryData<TResponseData[]>(['applications'], (oldData) => {
                if (!oldData) return [updatedData];
        
                return oldData.map((item) => 
                    item.id === updatedData.id ? updatedData : item
                );
            });
        },
        onError: () => {
            alert('Ошибка обновления');
        },
    });
};