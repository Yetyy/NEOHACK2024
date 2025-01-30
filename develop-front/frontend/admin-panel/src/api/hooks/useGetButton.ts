import { UseQueryResult, useQuery } from "@tanstack/react-query"
import { TButton } from "@/common/types/button";
import { getButtonActivity } from "@/api/services/button";

const useGetButton = (type: string): UseQueryResult<TButton> => {
    return useQuery({
        queryKey: ['button', type],
        queryFn: () => getButtonActivity(type),
        retry: 0,
    });
}

export default useGetButton;