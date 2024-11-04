import Card from "./atomic/card";
import { useState } from "react";
import OrganizerModal from "./atomic/organizerModal";

export default function Content({ organizers, }: { organizers: any[] }) {
    const [selectedOrganizerId, setSelectedOrganizerId] = useState<number | null>(null);
    const [organizerInfo, setOrganizerInfo] = useState<any>(null);
    const [isOpen, setIsOpen] = useState(false);

    const toggleModal = async (index: number) => {
        setOrganizerInfo(organizers[index]);
        setSelectedOrganizerId(index);
        setIsOpen(!isOpen);
    }
    return (
        <>
            <div className="absolute top-[68px] 2xl:top-20 left-0 right-0 bottom-10 overflow-auto px-4 py-6">
                <div className="grid 3xl:grid-cols-12 2xl:grid-cols-10 xl:grid-cols-8 lg:grid-cols-6 md:grid-cols-5 sm:grid-cols-4 gap-3">
                    {organizers.length === 0 ? (
                        <div className="col-span-full text-center">No organizers found.</div>
                    ) : organizers.map((organizer, index) => {
                        return (
                            <button key={index} onClick={() => toggleModal(index)} className="focus:outline-none">
                                <Card organizer={organizer} id={index} />
                            </button>
                        )
                    }
                    )}
                </div>
            </div>
            <OrganizerModal id={selectedOrganizerId} organizer={organizerInfo} isOpen={isOpen} />
        </>
    )
}