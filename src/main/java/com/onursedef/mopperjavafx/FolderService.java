package com.onursedef.mopperjavafx;

import com.onursedef.mopperjavafx.model.Organizer;

import java.io.File;
import java.util.List;

interface IFolderService {
    void createFolders();
    void moveFiles();
}

public class FolderService implements IFolderService {
    @Override
    public void createFolders() {
        OrganizerService service = new OrganizerService();
        List<Organizer> organizers = service.GetAll();

        for (Organizer organizer : organizers) {
            File directory = new File(organizer.getPath() + "\\" + organizer.getName());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
    }

    @Override
    public void moveFiles() {
        OrganizerService service = new OrganizerService();
        List<Organizer> organizers = service.GetAll();

        for (Organizer organizer : organizers) {
            File sourceDirectory = new File(organizer.getPath());
            File targetDirectory = new File(organizer.getPath() + "\\" + organizer.getName());

            for (File file : sourceDirectory.listFiles()) {
                if (file.isFile() && organizer.getExtensions().contains(file.getName().substring(file.getName().lastIndexOf('.') + 1))) {
                    File newFile = new File(targetDirectory.getAbsolutePath() + "\\" + file.getName());
                    file.renameTo(newFile);
                }
            }
        }
    }
}
