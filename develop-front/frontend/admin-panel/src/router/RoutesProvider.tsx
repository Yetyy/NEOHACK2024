
import Applications from "@/modules/applications/Applications";
import Dashboard from "@/modules/dashboard/Dashboard";
import MainPage from "@/modules/mainPage/MainPage";
import Settings from "@/modules/settings/Settings";
import {
    Route,
    createBrowserRouter,
    createRoutesFromElements,
    RouterProvider,
    Navigate,
} from "react-router-dom";

export default function RoutesProvider() {
    const provider = createBrowserRouter(
        createRoutesFromElements(
            <>
                <Route element={<MainPage />}>
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/applications" element={<Applications />} />
                    <Route path="/settings" element={<Settings />} />
                    <Route path="*" element={<div>Not found</div>} />
                </Route>
            </>
        )
    );

    return (
        <RouterProvider router={provider} />
    );
}
