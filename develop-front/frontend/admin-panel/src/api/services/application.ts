import { TResponseData } from "@/common/types/application";

const BASE_URL = 'https://6794fcf6aad755a134eaecd5.mockapi.io';

export const getApplications = async (): Promise<TResponseData[]> => {
    const res = await fetch(`${BASE_URL}/application`);

    if (!res.ok) {
        throw new Error;
    }

    const data = await res.json();
    return data;
}

export const updateAppStatus = async (id: string | number, app: TResponseData): Promise<TResponseData> => {
    const res = await fetch(`${BASE_URL}/application/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(app),
    });

    if (!res.ok) {
        throw new Error;
    }

    const data = await res.json();
    return data;
}