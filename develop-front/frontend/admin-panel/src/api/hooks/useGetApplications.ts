import { TResponseData } from "@/common/types/application";
import { UseQueryResult, useQuery } from "@tanstack/react-query"
import { getApplications } from "@/api/services/application";

const useGetApplications = (): UseQueryResult<TResponseData[]> => {
    return useQuery({
        queryKey: ['applications'],
        queryFn: getApplications,
        retry: 0,
    });
}

export default useGetApplications;