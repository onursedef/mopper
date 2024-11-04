import { Plus, Cog, Play } from "lucide-react";
import { useState, useEffect } from "react";
import Logo from "@/assets/logo.svg";
import OrganizerModal from "./atomic/organizerModal";
import { listen } from "@tauri-apps/api/event";
import { invoke } from "@tauri-apps/api/core";
import SettingsModal from "./atomic/settingsModal";

export default function Navbar({ organizerCount }: { organizerCount: number }) {

    // const [dropdownOpen, setDropdownOpen] = useState(false);
    // const [isExport, setIsExport] = useState(false);
    const [addModelOpen, setAddModelOpen] = useState(false);
    const [isRunning, setIsRunning] = useState(false);
    const [settingsModalOpen, setSettingsModalOpen] = useState(false);

    // function dropdownToggle() {
    //     setDropdownOpen(!dropdownOpen);
    // }
    function newModelToggle() {
        setAddModelOpen(!addModelOpen);
    }

    const settingsModalToggle = () => setSettingsModalOpen(!settingsModalOpen);

    // function setExport(value: boolean) {
    //     setIsExport(value);
    //     setDropdownOpen(false);
    // }

    const runOrganizer = async () => {
        setIsRunning(true);
        const run = await invoke("run_organizer");
        console.log(run);
        setIsRunning(false);
    }

    // useEffect(() => {
    //     if (dropdownOpen) {
    //         document.getElementById("dropdown")?.classList.remove("hidden");
    //     } else {
    //         document.getElementById("dropdown")?.classList.add("hidden");
    //     }
    // }, [dropdownOpen])

    useEffect(() => {
        const unlisten = listen("organize_files_task", (event) => {
            let isFinished = event.payload as { finished: boolean };
            setIsRunning(!isFinished);
        });
    
        return () => {
            unlisten.then((unlistenFn) => unlistenFn());
        };

    }, []);

    return (
        <>
            <nav className="fixed border-b border-blue-800 bg-blue-950 text-white left-0 right-0 overflow-hidden select-none">
                <div className="flex items-center justify-between px-4 py-3 mx-auto sm:px-6 lg:px-8">
                    <div className="flex items-center flex-shrink-0">
                        <img src={Logo} alt="Logo" />
                    </div>
                    <div className="flex items-center flex-row gap-4">
                        <button
                            className="flex items-center justify-center gap-2 px-4 py-2 rounded-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2 focus:ring-opacity-50
                bg-blue-600 text-white hover:bg-blue-500
                focus:ring-primary" onClick={newModelToggle}
                        >
                            <Plus className="w-5 h-5" /> New Organizer
                        </button>
                        {/* <div className="flex items-center">
                            <button
                                className="flex items-center justify-center gap-2 px-4 py-2 rounded-l-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2 focus:ring-opacity-50
                bg-blue-600 text-white hover:bg-blue-500 border-r border-blue-400
                focus:ring-primary"
                            >
                                {isExport ?
                                    (
                                        <>
                                            <Download className="w-5 h-5" /> Export
                                        </>
                                    ) : (
                                        <>
                                            <Upload className="w-5 h-5" /> Import
                                        </>
                                    )}
                            </button>
                            <button
                                id="dropdownDefaultButton"
                                onClick={dropdownToggle}
                                className="flex items-center justify-center gap-2 p-2 rounded-r-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2 focus:ring-opacity-50
                bg-blue-600 text-white hover:bg-blue-500 border-l border-blue-400
                focus:ring-primary"
                            >
                                <ChevronDown className="w-5 h-5" />
                            </button>
                        </div> */}

                        <button
                            className="flex items-center justify-center gap-2 px-4 py-2 rounded-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2 focus:ring-opacity-50
                bg-blue-600 text-white hover:bg-blue-500
                focus:ring-primary"
                            onClick={settingsModalToggle}
                        >
                            <Cog className="w-5 h-5" /> Settings
                        </button>
                        <button
                            className={`flex items-center justify-center gap-2 px-4 py-2 rounded-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2 focus:ring-opacity-50
                bg-red-600 text-white
                focus:ring-primary` + (organizerCount === 0 ? " disabled:opacity-50 cursor-not-allowed" : "hover:bg-red-500")}
                            disabled={organizerCount === 0 && isRunning}
                            onClick={runOrganizer}
                        >
                            <Play className="w-5 h-5" /> Run
                        </button>
                    </div>
                </div>
            </nav>
            {/* <div id="dropdown" className="absolute top-16 right-40 z-20 bg-white divide-y divide-gray-900 rounded-lg shadow w-44 dark:bg-gray-700 h-[98px]">
                <ul className="py-2 text-sm text-gray-700 dark:text-gray-200" aria-labelledby="dropdownDefaultButton">
                    <li>
                        <button onClick={() => setExport(false)} className="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:white w-full text-left">
                            <span className="inline-flex gap-2">
                                <Upload className="w-5 h-5" /> Import
                            </span>
                        </button>
                    </li>
                    <li>
                        <button onClick={() => setExport(true)} className="absolute px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:white w-full text-left">
                            <span className="inline-flex gap-2">
                                <Download className="w-5 h-5" /> Export
                            </span>
                        </button>
                    </li>
                </ul>
            </div> */}
            <OrganizerModal isOpen={addModelOpen} organizer={null} id={null} />
            <SettingsModal isOpen={settingsModalOpen} toggle={settingsModalToggle} />
        </>
    )
}