// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::path;

const DATA_FOLDER: &str = "data";

fn main() {
    let data_folder = path::Path::new(DATA_FOLDER).exists();

    if !data_folder {
        std::fs::create_dir(DATA_FOLDER).expect("Failed to create data folder");
    }

    let mopper_db = path::Path::new(&format!("{}/mopper", DATA_FOLDER)).is_file();

    if !mopper_db {
        let writer = std::fs::File::create(format!("{}/mopper", DATA_FOLDER))
            .expect("Failed to create mopper config file");
        let organizers: Vec<String> = Vec::new();
        serde_yaml::to_writer(&writer, &organizers)
            .expect("Failed to write organizers to mopper config file");
    }
    mopper_lib::run()
}
