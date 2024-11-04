import Logo from "@/assets/icon.svg";
import { getCurrentWindow } from "@tauri-apps/api/window";
import { Copy, Minus, Square, X } from "lucide-react";
import { useEffect, useState } from "react";

export default function Titlebar() {

    const [maximized, setMaximized] = useState(false);
    const appWindow = getCurrentWindow();

    useEffect(() => {
        // Disable the default right-click context menu
        const handleContextMenu = (event: Event) => {
            event.preventDefault();
        };
        document.addEventListener('contextmenu', handleContextMenu);

        const handleMinimize = () => appWindow.minimize();
        const handleMaximizeToggle = async () => {
            const isMax = await appWindow.isMaximized();
            if (isMax) {
                appWindow.unmaximize();
                setMaximized(false);
            } else {
                appWindow.maximize();
                setMaximized(true);
            }
        };

        document.getElementById("titlebar-minimize")?.addEventListener("click", handleMinimize);
        document.getElementById("titlebar-maximize")?.addEventListener("click", handleMaximizeToggle);

        const init = async () => {
            const isMax = await appWindow.isMaximized();
            setMaximized(isMax);
        };

        init();

        return () => {
            document.removeEventListener('contextmenu', handleContextMenu);
            document.getElementById("titlebar-minimize")?.removeEventListener("click", handleMinimize);
            document.getElementById("titlebar-maximize")?.removeEventListener("click", handleMaximizeToggle);
        };
    }, [appWindow]);

    return (
        <div data-tauri-drag-region className="w-full bg-blue-950 fixed flex justify-between items-center top-0 left-0 right-0 h-11 p-0">
            <div className="inline-flex items-center gap-2 px-4 py-2 select-none">
                <img src={Logo} alt="Mopper App Icon" className="w-5 h-5" />
                <span className="text-white text-sm">Mopper</span>
            </div>
            <div className="inline-flex items-center">
                <div id="titlebar-minimize" className="inline-flex items-center justify-center text-white hover:bg-blue-800 w-12 h-9 transition-all rounded-bl-sm rounded-tl-sm" aria-label="Minimize">
                    <Minus className="w-4 h-4" />
                </div>
                <div id="titlebar-maximize" className="inline-flex items-center justify-center text-white hover:bg-blue-800 w-12 h-9 transition-all" aria-label="Maximize">
                    {maximized ? (
                        <Copy className="w-4 h-4 transform -scale-x-100" />
                    ) : (
                        <Square className="w-4 h-4" />
                    )}
                </div>
                <button onClick={() => appWindow.close()} id="titlebar-close" className="inline-flex items-center justify-center text-white hover:bg-red-500 w-12 h-9 transition-all" aria-label="Close">
                    <X className="w-4 h-4" />
                </button>
            </div>
        </div>
    )
}
