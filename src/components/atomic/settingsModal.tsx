import { AlarmClock, X } from "lucide-react";
import { useEffect, useState } from "react";
import Icon from "@/assets/icon.svg";
import Logo from "@/assets/logo.svg";
import { GitHubLogoIcon, LinkedInLogoIcon } from "@radix-ui/react-icons";
import { getVersion, getTauriVersion } from "@tauri-apps/api/app";

export default function SettingsModal({
    isOpen,
    toggle
} : {
    isOpen: boolean,
    toggle: () => void
}) {
    // const [isOpen, setIsOpen] = useState(false);
    const [timerSelected, setTimerSelected] = useState(false);
    const [version, setVersion] = useState("");
    const [tauriVersion, setTauriVersion] = useState("");

    // const toggleModal = () => setIsOpen(!isOpen);

    useEffect(() => {
        const getMopperVersion = async () => {
            return await getVersion();
        }
        getMopperVersion().then((version) => {
            setVersion(version);
        });

        getTauriVersion().then((version) => {
            setTauriVersion(version);
        });

    }, []);

    return (
        <div className={`absolute left-0 h-full w-full bg-gray-950/35 z-50 flex items-center justify-center py-6 2xl:py-0 ${isOpen ? "" : "hidden"}`}>
            <div className="relative border-2 border-blue-600 bg-blue-900 2xl:w-3/6 2xl:h-5/6 w-4/6 h-full rounded-md">
                <div className="grid grid-cols-12 h-full">
                    <div className="col-span-3 flex flex-col border-r-2 border-blue-600 h-full w-full">
                        <button onClick={() => setTimerSelected(false)} className={`inline-flex items-center justify-center h-24 hover:bg-blue-600 ${timerSelected ? "bg-blue-800" : "bg-blue-600"}`}>
                            <div className="inline-flex gap-3 items-center">
                                <img src={Icon} alt="Mopper Icon" className="w-8 h-8" />
                                About Mopper
                            </div>
                        </button>
                        <button onClick={() => setTimerSelected(true)} className={`inline-flex items-center justify-center h-24 hover:bg-blue-600 ${timerSelected ? "bg-blue-600" : "bg-blue-800"}`}>
                            <AlarmClock className="w-6 h-6 mr-3" /> Timer Settings
                        </button>
                    </div>
                    <div className="col-span-9 w-full h-full">
                        <div className="p-8 flex flex-col gap-6">
                            <div className="absolute right-4 top-4">
                                <button onClick={toggle} className="focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md">
                                    <X className="w-5 h-5" />
                                </button>
                            </div>
                            {timerSelected ? (
                                <>
                                    <h1 className="text-xl font-bold">Timer Settings</h1>
                                    <p className="text-sm">Coming soon...</p>
                                </>
                            ) : (
                                <>
                                    <img src={Logo} max-width="100%" alt="Mopper Logo" />
                                    <div className="flex flex-col gap-3">
                                        <h1 className="text-xl font-bold">About Mopper</h1>
                                        <p className="text-sm">Mopper is an open-source, automated file organization tool built for flexibility and ease of use. Users can create customizable "organizers" to set up rules for file placement and movement, with a default run interval of every 5 minutes, which can be adjusted. Mopper also provides options to create, delete, or update organizers, empowering users to tailor their file management. Built with Tauri and Rust for backend robustness and React with Tailwind CSS for a responsive frontend, Mopper encourages contributions and forks, inviting users to extend and personalize their own versions.</p>
                                        <p>Onur Sedef, Creator of Mopper.</p>
                                    </div>
                                    <div className="inline-flex gap-3">
                                        <a href="#" target="_blank" rel="noopener noreferrer"><GitHubLogoIcon className="w-6 h-6" /></a>
                                        <a href="https://www.linkedin.com/in/onur-sedef" target="_blank" rel="noopener noreferrer"><LinkedInLogoIcon className="w-6 h-6" /></a>
                                    </div>
                                    <div className="absolute bottom-8 text-sm text-gray-300 flex flex-col gap-2">
                                        <p>Mopper Version: {version}</p>
                                        <p>Tauri Version: {tauriVersion}</p>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}