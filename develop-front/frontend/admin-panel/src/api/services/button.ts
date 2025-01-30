import { TButton } from "@/common/types/button";

const BASE_URL = 'https://6795c6debedc5d43a6c37035.mockapi.io';

export const getButtonActivity = async (type: string): Promise<TButton> => {

    let id;
    if(type == 'apply') id = 1;
    else if (type == 'pre_apply') id = 2;

    const res = await fetch(`${BASE_URL}/button/${id}`);

    if (!res.ok) {
        throw new Error;
    }

    const data = await res.json();
    return data;
}

export const updateButton = async (type: string, btn: TButton): Promise<TButton> => {

    let id;
    if(type == 'apply') id = 1;
    else if (type == 'pre_apply') id = 2;

    const res = await fetch(`${BASE_URL}/button/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(btn),
    });

    if (!res.ok) {
        throw new Error;
    }

    const data = await res.json();
    return data;
}