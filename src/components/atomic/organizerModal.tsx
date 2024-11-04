import { invoke } from "@tauri-apps/api/core";
import { Plus, Save, Trash, X } from "lucide-react";
import { useEffect, useState } from "react";
import { open } from '@tauri-apps/plugin-dialog';
import { desktopDir } from '@tauri-apps/api/path';

export default function OrganizerModal({ organizer, id, isOpen, toggle }: { organizer: any | null, id: number | null, isOpen: boolean, toggle: () => void }) {
    const [name, setName] = useState(organizer?.name || "");
    const [sourcePath, setSourcePath] = useState(organizer?.source_path || "");
    const [destPath, setDestPath] = useState(organizer?.destination_path || "");
    const [extensions, setExtensions] = useState(organizer?.extensions || "");
    const [regex, setRegex] = useState(organizer?.regex || "");
    const [isShowingFiles, setIsShowingFiles] = useState(false);
    const [organizerInfo, setOrganizerInfo] = useState<any>(null);

    const addNewOrganizer = async () => {
        var organizer = {
            name,
            source_path: sourcePath,
            destination_path: destPath,
            extensions,
            regex
        };
        console.log(organizer);
        invoke('add_organizer', { organizer });
        toggle();

        // reload document
        document.location.reload();
    }

    const updateOrganizer = async (key: number) => {
        var organizer = {
            name,
            source_path: sourcePath,
            destination_path: destPath,
            extensions,
            regex
        };
        console.log(organizer);
        invoke('update_organizer', { organizer, index: key });

        // reload document
        document.location.reload();
        toggle();
    }

    const deleteOrganizer = async (key: number) => {
        console.log(key);
        invoke('delete_organizer', { index: key });

        // reload document
        document.location.reload();
        toggle();
    }

    const selectSourcePath = async () => {
        const sourcePathSelection = await open({
            multiple: false,
            directory: true,
            defaultPath: sourcePath != "" ? sourcePath : await desktopDir()
        });

        console.log(sourcePathSelection);
        setSourcePath(sourcePathSelection ?? "");
        console.log('sourcePath', sourcePath);
    }

    const selectDestinationPath = async () => {
        const destinationPath = await open({
            multiple: false,
            directory: true,
            defaultPath: destPath != "" ? destPath : await desktopDir()
        });

        setDestPath(destinationPath ?? "");
    }

    useEffect(() => {
        // const getOrganizerInfo = async () => {
        //     return await invoke("get_organizer_files", { index: id });
        // }
        // getOrganizerInfo().then((info) => {
        //     setOrganizerInfo(info);
        // });
        if (isOpen) {
            const getOrganizerInfo = async () => {
                if (id != null) {
                    return await invoke("get_organizer_files", { index: id });
                }
            }

            getOrganizerInfo().then((info) => {
                setOrganizerInfo(info);
            });
            console.log(organizerInfo);
        }
    });

    return (
        <>
            {isOpen ? (
                <>
                    {organizer == null || organizer == typeof undefined ? (
                        <div className={"select-none absolute left-0 h-full w-full bg-gray-900/35 z-50 flex items-center justify-center " + (isOpen ? "" : "hidden")}>
                            <div className="relative border-blue-800 bg-blue-950 w-[642px] h-[642px] 2xl:w-[1080px] 2xl:h-[642px] rounded-lg p-6">
                                <div className="inline-flex items-center justify-between w-full">
                                    <h1 className="text-2xl font-semibold text-white">New Organizer</h1>
                                    <button className="focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md" onClick={toggle}>
                                        <X className="w-6 h-6 text-white" />
                                    </button>
                                </div>
                                <div className="flex flex-col gap-4 mt-6">
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="name">Name <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <input type="text" onChange={(e) => setName(e.target.value)} className="outline-none bg-[#2C5282] p-2 w-full rounded-md placeholder:text-gray-300 border-none" placeholder="e.g. Documents" />
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="path">Source Path <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <div className="inline-flex gap-2 items-center w-full">
                                            <button className="bg-blue-600 py-2 rounded-md w-36" onClick={selectSourcePath}>Select Folder</button>
                                            <input type="text" onChange={(e) => setSourcePath(e.target.value)} value={sourcePath} className="outline-none border-none p-2 rounded-md placeholder:text-gray-300 bg-[#2C5282] w-full" placeholder="e.g. /home/user/documents" />
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="path">Destination Path <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <div className="inline-flex gap-2 items-center w-full">
                                            <button className="bg-blue-600 py-2 rounded-md w-36" onClick={selectDestinationPath}>Select Folder</button>
                                            <input type="text" onChange={(e) => setDestPath(e.target.value)} value={destPath} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. /home/user/documents" />
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="extensions">Extensions</label>
                                        <input type="text" onChange={(e) => setExtensions(e.target.value)} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. doc, docx, pdf, xlsx" />
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="regex">Regex</label>
                                        <input type="text" onChange={(e) => setRegex(e.target.value)} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. ^(?=.*mopper).*\.rs$" />
                                    </div>
                                </div>
                                <div className="absolute bottom-6 right-6 mt-auto ml-auto">
                                    <div className="inline-flex gap-4 items-center">
                                        <button className="inline-flex items-center focus:outline-none p-2 bg-none border-2 border-red-600 text-white hover:bg-red-500 transition-all rounded-md" onClick={toggle}>
                                            <Trash className="w-5 h-5 mr-2" /> Cancel
                                        </button>
                                        <button onClick={addNewOrganizer} className="inline-flex items-center focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md">
                                            <Save className="w-5 h-5 mr-2" /> Save
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className={"select-none absolute bottom-0 left-0 top-0 h-full w-full bg-gray-900/35 z-[9999] flex items-center justify-center  2xl:py-0 " + (isOpen ? "" : "hidden")}>
                            <div className="relative border-blue-800 bg-blue-950 xl:w-3/6 3xl:h-5/6 w-4/6 h-full rounded-lg p-6">
                                <div className="inline-flex items-center justify-between w-full">
                                    <h1 className="text-2xl font-semibold text-white">Update Organizer - {organizer.name}</h1>
                                    <button className="focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md" onClick={toggle}>
                                        <X className="w-6 h-6 text-white" />
                                    </button>
                                </div>
                                <div className="flex flex-col 2xl:gap-4 gap-2 3xl:mt-6 mt-2 overflow-y-scroll">
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="name">Name <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <input type="text" onChange={(e) => setName(e.target.value)} value={name} className="outline-none bg-[#2C5282] p-2 w-full rounded-md placeholder:text-gray-300 border-none" placeholder="e.g. Documents" />
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="path">Source Path <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <div className="inline-flex gap-2 items-center w-full">
                                            <button className="bg-blue-600 py-2 rounded-md w-36" onClick={selectSourcePath}>Select Folder</button>
                                            <input type="text" onChange={(e) => setSourcePath(e.target.value)} value={sourcePath} className="outline-none border-none p-2 rounded-md placeholder:text-gray-300 bg-[#2C5282] w-full" placeholder="e.g. /home/user/documents" />
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="path">Destination Path <span className="text-red-500 text-xs align-top">* required</span></label>
                                        <div className="inline-flex gap-2 items-center w-full">
                                            <button className="bg-blue-600 py-2 rounded-md w-36" onClick={selectDestinationPath}>Select Folder</button>
                                            <input type="text" onChange={(e) => setDestPath(e.target.value)} value={destPath} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. /home/user/documents" />
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="extensions">Extensions</label>
                                        <input type="text" onChange={(e) => setExtensions(e.target.value)} value={extensions} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. doc, docx, pdf, xlsx" />
                                    </div>
                                    <div className="flex flex-col items-start gap-2">
                                        <label htmlFor="regex">Regex</label>
                                        <input type="text" onChange={(e) => setRegex(e.target.value)} value={regex} className="outline-none border-none p-2 w-full rounded-md placeholder:text-gray-300 bg-[#2C5282]" placeholder="e.g. ^(?=.*mopper).*\.rs$" />
                                    </div>
                                    <div className="border border-gray-300 hidden 2xl:block"></div>
                                    <div className="flex-col items-start gap-2 bg-blue-800 rounded-md 3xl:max-h-96 max-h-60 overflow-y-scroll relative hidden 2xl:flex">
                                        <div className="sticky bg-blue-800/35 backdrop-blur-md p-4 top-0 right-0 left-0 inline-flex w-full items-center justify-between">
                                            <p className="transition-all">{isShowingFiles ? "Hide Files" : "Show Files"}</p>
                                            <button onClick={() => setIsShowingFiles(!isShowingFiles)}>
                                                {isShowingFiles ? <Plus className="w-5 h-5 transform rotate-[225deg] duration-300 transition-all" /> : <Plus className="w-5 h-5 transition-all duration-300" />}
                                            </button>
                                        </div>
                                        <table className={"w-full mt-4 transition-all " + (isShowingFiles ? "" : "hidden")}>
                                            <tbody>
                                                {organizerInfo != null ? organizerInfo.map((file: any, index: number) => (
                                                    <>
                                                        {index === 0 && organizerInfo.length === index + 1 ? (
                                                            <tr key={index} className=" hover:bg-blue-900 transition-colors text-center">
                                                                <td className="p-2 rounded-l-md">{file.name}</td>
                                                                <td className="p-2">{(file.size / 1024 / 1024).toFixed(2)} mb</td>
                                                                <td className="p-2">{file.is_file ? "File" : "Folder"}</td>
                                                                <td className="p-2">{new Date(file.updated_at).toLocaleString()}</td>
                                                                <td className="p-2 rounded-r-md">{new Date(file.created_at).toLocaleString()}</td>
                                                            </tr>
                                                        ) : index === 0 ? (
                                                            <tr key={index} className=" hover:bg-blue-900 transition-colors text-center">
                                                                <td className="p-2 rounded-tl-md">{file.name}</td>
                                                                <td className="p-2">{(file.size / 1024 / 1024).toFixed(2)} mb</td>
                                                                <td className="p-2">{file.is_file ? "File" : "Folder"}</td>
                                                                <td className="p-2">{new Date(file.updated_at).toLocaleString()}</td>
                                                                <td className="p-2 rounded-tr-md">{new Date(file.created_at).toLocaleString()}</td>
                                                            </tr>
                                                        ) : organizerInfo.length === index + 1 ? (
                                                            <tr key={index} className=" hover:bg-blue-900 transition-colors text-center">
                                                                <td className="p-2 rounded-bl-md">{file.name}</td>
                                                                <td className="p-2">{(file.size / 1024 / 1024).toFixed(2)} mb</td>
                                                                <td className="p-2">{file.is_file ? "File" : "Folder"}</td>
                                                                <td className="p-2">{new Date(file.updated_at).toLocaleString()}</td>
                                                                <td className="p-2 rounded-br-md">{new Date(file.created_at).toLocaleString()}</td>
                                                            </tr>
                                                        ) : (
                                                            <tr key={index} className=" hover:bg-blue-900 transition-colors text-center">
                                                                <td className="p-2">{file.name}</td>
                                                                <td className="p-2">{(file.size / 1024 / 1024).toFixed(2)} mb</td>
                                                                <td className="p-2">{file.is_file ? "File" : "Folder"}</td>
                                                                <td className="p-2">{new Date(file.updated_at).toLocaleString()}</td>
                                                                <td className="p-2">{new Date(file.created_at).toLocaleString()}</td>
                                                            </tr>
                                                        )}
                                                    </>
                                                )) : (
                                                    <tr>
                                                        <td colSpan={5} className="text-center p-4 text-gray-400">No files to display</td>
                                                    </tr>
                                                )}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div className="absolute bottom-6 right-6 mt-auto ml-auto">
                                    <div className="inline-flex gap-4 items-center">
                                        <button className="inline-flex items-center focus:outline-none p-2 bg-none border-2 bg-red-600 text-white hover:bg-red-500 transition-all rounded-md" onClick={() => deleteOrganizer(id!)}>
                                            <Trash className="w-5 h-5 mr-2" /> Delete
                                        </button>
                                        <button onClick={() => updateOrganizer(id!)} className="inline-flex items-center focus:outline-none p-2 bg-blue-600 text-white hover:bg-blue-500 transition-all rounded-md">
                                            <Save className="w-5 h-5 mr-2" /> Save
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div >
                    )
                    }
                </>
            ) : (<> </>)}
        </>
    )
}