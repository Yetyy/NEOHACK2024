export type TContent = {
    id: string | number;
    first_name: string;
    last_name: string;
    date: string;
    position: string;
    status: string;
};

export type TResponseData = {
    id: number;
    userId: string; 
    directionId: string; 
    type: string; 
    status: string; 
    date: string;
}