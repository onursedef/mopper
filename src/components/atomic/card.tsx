import { Folder, FolderPlus } from "lucide-react";
import OrganizerModal from "./organizerModal";
import { useState } from "react";
import { invoke } from "@tauri-apps/api/core";

type CardProps = {
    organizer: any;
    id: number;
}

export default function Card({
    organizer,
    id
}: CardProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [organizerInfo, setOrganizerInfo] = useState<any>(null);

    const toggleModal = async () => {
        let organizerInfo = await invoke("get_organizer_files", { index: id });
        console.log(organizerInfo);
        setOrganizerInfo(organizerInfo);
        setIsOpen(!isOpen);
    }
    return (
        <>
            <div className="flex flex-col gap-3 transition-all bg-blue-600 border-blue-700 hover:bg-blue-500 border rounded-lg lg:w-40 lg:h-56 sm:w-36 sm:h-52 xl:w-36 xl:h-52 2xl:w-40 3xl:w-48 3xl:h-64 4xl:w-[200px] 4xl:h-72 cursor-pointer" onClick={toggleModal}>
                <div className={`flex flex-col items-center justify-center text-center px-2 h-full w-full ${(organizer.image != null || organizer.image != typeof undefined ? "bg-[url(" + organizer.image + ")]" : "")}`}>
                    {organizer.image == null || organizer.image == typeof undefined ? <Folder className="w-20 h-20 text-blue-900 mb-2" /> : ""}
                    <span className="text-md select-none">{organizer.name}</span>
                </div>
            </div>
            {/* <OrganizerModal id={id} organizer={organizer} isOpen={isOpen} toggle={toggleModal} /> */}
        </>
    )
}