import { UseQueryResult, useQuery } from "@tanstack/react-query"
import { TUser } from "@/common/types/user";
import { getUserData } from "@/api/services/user";

const useGetUser = (id: string | number): UseQueryResult<TUser> => {
    return useQuery({
        queryKey: ['user', id],
        queryFn: () => getUserData(id),
        retry: 0,
    });
}

export default useGetUser;