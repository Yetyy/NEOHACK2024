
import { TUser } from "@/common/types/user";

const BASE_URL = 'https://6794fcf6aad755a134eaecd5.mockapi.io';

export const getUserData = async (id: string | number): Promise<TUser> => {
    const res = await fetch(`${BASE_URL}/user/${id}`);

    if (!res.ok) {
        throw new Error;
    }

    const data = await res.json();
    return data;
}