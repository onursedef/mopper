import { AlarmClock, Settings, X } from "lucide-react";
import { useEffect, useState } from "react";
import Icon from "@/assets/icon.svg";
import Logo from "@/assets/logo.svg";
import { GitHubLogoIcon, LinkedInLogoIcon } from "@radix-ui/react-icons";
import { getVersion, getTauriVersion } from "@tauri-apps/api/app";
import { invoke } from "@tauri-apps/api/core";

export default function SettingsModal({
    isOpen,
    toggle
} : {
    isOpen: boolean,
    toggle: () => void
}) {
    // const [isOpen, setIsOpen] = useState(false);
    const [settingsSelected, setSettingsSelected] = useState(false);
    const [version, setVersion] = useState("");
    const [tauriVersion, setTauriVersion] = useState("");
    const [interval, setInterval] = useState(5);
    const [runOnStartup, setRunOnStartup] = useState(false);

    const setConfig = () => {
        if (interval < 1) {
            return;
        }
        const config = {
            timer: interval,
            run_on_startup: runOnStartup
        }
        invoke("set_config", { config });
        document.location.reload();
    }

    const getConfig = async () => {
        return invoke('get_config') as Promise<any>;
    };

    useEffect(() => {
        const fetchConfig = async () => {
            const { timer, run_on_startup } = await getConfig();
            setInterval(timer);
            setRunOnStartup(run_on_startup);
          };
          fetchConfig();
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
                        <button onClick={() => setSettingsSelected(false)} className={`inline-flex items-center justify-center h-24 hover:bg-blue-600 ${settingsSelected ? "bg-blue-800" : "bg-blue-600"}`}>
                            <div className="inline-flex gap-3 items-center">
                                <img src={Icon} alt="Mopper Icon" className="w-8 h-8" />
                                About Mopper
                            </div>
                        </button>
                        <button onClick={() => setSettingsSelected(true)} className={`inline-flex items-center justify-center h-24 hover:bg-blue-600 ${settingsSelected ? "bg-blue-600" : "bg-blue-800"}`}>
                            <Settings className="w-6 h-6 mr-3" /> Settings
                        </button>
                    </div>
                    <div className="col-span-9 w-full h-full">
                        <div className="p-8 flex flex-col gap-6">
                            <div className="absolute right-4 top-4">
                                <button onClick={toggle} className="focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md">
                                    <X className="w-5 h-5" />
                                </button>
                            </div>
                            {settingsSelected ? (
                                <>
                                    <h1 className="text-xl font-bold">Settings</h1>
                                    <div className="flex flex-col gap-3">
                                        <label htmlFor="interval" className="text-sm">Interval (minutes)</label>
                                        <input type="number" id="interval" className="p-2 bg-gray-800 text-white rounded-md" value={interval} onChange={(e) => setInterval(parseInt(e.target.value))} />
                                    </div>
                                    {/* run app on startup */}
                                    <div className="flex flex-row-reverse gap-3 items-center mr-auto">
                                        <label htmlFor="startup" className="text-sm">Run on startup</label>
                                        <input type="checkbox" id="startup" className="w-5 h-5 bg-gray-800 rounded-sm outline-blue-400" checked={runOnStartup} onChange={(e) => setRunOnStartup(e.target.checked)} />
                                    </div>
                                    <button className="p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md" onClick={setConfig}>Save</button>
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