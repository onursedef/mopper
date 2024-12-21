use chrono::{DateTime, Utc};
use serde_json::json;
use std::{ffi::OsStr, sync::Arc, time::Duration};
use tokio::sync::Mutex;

use serde::{Deserialize, Serialize};
use tauri::{
    async_runtime::spawn,
    menu::{Menu, MenuItem},
    tray::{MouseButton, MouseButtonState, TrayIconBuilder, TrayIconEvent},
    Emitter, Event, Manager,
};
use tokio::sync::Notify;

// Learn more about Tauri commands at https://tauri.app/v1/guides/features/command
#[tauri::command]
fn greet(name: &str) -> String {
    format!("Hello, {}! You've been greeted from Rust!", name)
}

// create a type for organizer
#[derive(Serialize, Deserialize, Clone)]
struct Organizer {
    name: String,
    source_path: String,
    destination_path: String,
    extensions: Option<String>,
    regex: Option<String>,
}

#[derive(Serialize, Deserialize, Clone)]
struct OrganizerFile {
    name: String,
    size: u64,
    created_at: String,
    updated_at: String,
    is_file: bool,
}

#[derive(Serialize, Deserialize)]
struct Config {
    timer: u64,
    run_on_startup: bool,
}

#[tauri::command]
fn read_file() -> Vec<Organizer> {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    organizers
}

#[tauri::command]
fn get_organizer(index: i32) -> Organizer {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    organizers[index as usize].clone()
}

#[tauri::command]
fn get_organizer_files(index: i32) -> Vec<OrganizerFile> {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    let destination_path = organizers[index as usize].destination_path.clone();
    let files = std::fs::read_dir(destination_path).expect("Unable to read directory");
    let mut organizer_files: Vec<OrganizerFile> = Vec::new();
    for file in files {
        let file = file.unwrap();
        let path = file.path();
        let name = file.file_name().into_string().unwrap();
        let size = file.metadata().unwrap().len();
        let created_at: DateTime<Utc> = file.metadata().unwrap().created().unwrap().into();
        let created_at = created_at.to_rfc3339();
        let file_updated_at: DateTime<Utc> = file.metadata().unwrap().modified().unwrap().into();
        let updated_at = file_updated_at.to_rfc3339();
        let is_file = path.is_file();

        let organizer_file = OrganizerFile {
            name,
            size,
            created_at,
            updated_at,
            is_file,
        };

        organizer_files.push(organizer_file);
    }

    organizer_files
}

#[tauri::command]
fn add_organizer(organizer: Organizer) {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let mut organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    organizers.push(organizer);
    let new_data = serde_yaml::to_string(&organizers).expect("Unable to serialize data");
    std::fs::write("data/mopper", new_data).expect("Unable to write file");
}

#[tauri::command]
fn update_organizer(organizer: Organizer, index: i32) {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let mut organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    organizers[index as usize] = organizer;
    let new_data = serde_yaml::to_string(&organizers).expect("Unable to serialize data");
    std::fs::write("data/mopper", new_data).expect("Unable to write file");
}

#[tauri::command]
fn delete_organizer(index: i32) {
    let data = std::fs::read_to_string("data/mopper").expect("Unable to read file");
    let mut organizers: Vec<Organizer> = serde_yaml::from_str(&data).expect("Unable to parse file");
    organizers.remove(index as usize);
    let new_data = serde_yaml::to_string(&organizers).expect("Unable to serialize data");
    std::fs::write("data/mopper", new_data).expect("Unable to write file");
}

#[tauri::command]
async fn run_organizer() -> bool {
    let data = tokio::fs::read_to_string("data/mopper")
        .await
        .expect("Unable to read file");
    let organizers =
        serde_yaml::from_str::<Vec<Organizer>>(&data).expect("msg: Unable to parse file");

    for organizer in organizers {
        match tokio::fs::read_dir(&organizer.source_path.trim()).await {
            Ok(mut entries) => {
                while let Some(entry) = entries.next_entry().await.unwrap() {
                    let path = entry.path();
                    println!("Processing file: {:?}", path);
                    let file_name = path.file_name().and_then(OsStr::to_str).unwrap_or("");
                    if path.is_file() {
                        let extension = path.extension().and_then(OsStr::to_str).unwrap_or("");
                        println!("File extension: {:?}", extension);
                        let extensions = organizer.extensions.clone().unwrap_or("*".to_string());
                        let organizer_extensions = extensions.split(",").collect::<Vec<&str>>();
                        let organizer_extensions = organizer_extensions
                            .iter()
                            .map(|ext| ext.trim())
                            .collect::<Vec<&str>>();

                        println!("Organizer extensions: {:?}", organizer_extensions);

                        let destination_path_str = organizer.destination_path.trim();
                        let destination_path = std::path::Path::new(destination_path_str);
                        if !destination_path.exists() {
                            tokio::fs::create_dir_all(&organizer.destination_path.trim())
                                .await
                                .expect("Failed to create destination path");
                        }

                        println!("Destination path: {:?}", destination_path);

                        if organizer_extensions != ["*"]
                            && organizer_extensions.contains(&extension)
                        {
                            println!("Copying file: {:?}", path);
                            let destination_path = destination_path.join(file_name);
                            tokio::fs::rename(&path, &destination_path)
                                .await
                                .expect("Failed to copy file");
                        }
                        let regex = organizer.regex.clone().unwrap_or("".to_string());
                        if regex != "" {
                            let re = regex::Regex::new(&regex).unwrap();
                            if re.is_match(&path.to_str().unwrap()) {
                                let destination_path = destination_path.join(file_name);
                                tokio::fs::rename(&path, &destination_path)
                                    .await
                                    .expect("Failed to copy file");
                            }
                        }
                    }
                }
            }
            Err(e) => eprintln!("Failed to read source path: {}", e),
        }
    }

    true
}

#[tauri::command]
async fn set_config(config: Config) {
    let new_data = serde_yaml::to_string(&config).expect("Unable to serialize data");
    std::fs::write("data/mopper_config", new_data).expect("Unable to write file");
}

#[tauri::command]
async fn get_config() -> Config {
    let data = std::fs::read_to_string("data/mopper_config").expect("Unable to read file");
    let config: Config = serde_yaml::from_str(&data).expect("Unable to parse file");
    config
}

async fn organize_files(
    running: Arc<Mutex<bool>>,
    _notify: Arc<Notify>,
    app_handle: tauri::AppHandle,
) {
    let mut is_finished = false;
    loop {
        let running = running.lock().await;
        if !*running {
            break;
        }
        drop(running);
        is_finished = false;

        app_handle
            .emit("organize_files_task", json!({"finished": false}))
            .unwrap();
        println!("Running organizer task");
        let data = tokio::fs::read_to_string("data/mopper")
            .await
            .expect("Unable to read file");
        let organizers =
            serde_yaml::from_str::<Vec<Organizer>>(&data).expect("Unable to parse file");

        for organizer in organizers {
            match tokio::fs::read_dir(&organizer.source_path.trim()).await {
                Ok(mut entries) => {
                    while let Some(entry) = entries.next_entry().await.unwrap() {
                        let path = entry.path();
                        println!("Processing file: {:?}", path);
                        let file_name = path.file_name().and_then(OsStr::to_str).unwrap_or("");
                        if path.is_file() {
                            let extension = path.extension().and_then(OsStr::to_str).unwrap_or("");
                            println!("File extension: {:?}", extension);
                            let extensions =
                                organizer.extensions.clone().unwrap_or("*".to_string());
                            let organizer_extensions = extensions.split(",").collect::<Vec<&str>>();
                            let organizer_extensions = organizer_extensions
                                .iter()
                                .map(|ext| ext.trim())
                                .collect::<Vec<&str>>();

                            println!("Organizer extensions: {:?}", organizer_extensions);

                            let destination_path_str = organizer.destination_path.trim();
                            let destination_path = std::path::Path::new(destination_path_str);
                            if !destination_path.exists() {
                                tokio::fs::create_dir_all(&organizer.destination_path.trim())
                                    .await
                                    .expect("Failed to create destination path");
                            }

                            println!("Destination path: {:?}", destination_path);

                            if organizer_extensions != ["*"]
                                && organizer_extensions.contains(&extension)
                            {
                                println!("Copying file: {:?}", path);
                                let destination_path = destination_path.join(file_name);
                                tokio::fs::rename(&path, &destination_path)
                                    .await
                                    .expect("Failed to copy file");
                            }
                            let regex = organizer.regex.clone().unwrap_or("".to_string());
                            if regex != "" {
                                let re = regex::Regex::new(&regex).unwrap();
                                if re.is_match(&path.to_str().unwrap()) {
                                    let destination_path = destination_path.join(file_name);
                                    tokio::fs::rename(&path, &destination_path)
                                        .await
                                        .expect("Failed to copy file");
                                }
                            }
                        }
                    }
                }
                Err(e) => eprintln!("Failed to read source path: {}", e),
            }
        }

        println!("Organizer task is finished, sleeping for 5 minutes");
        tokio::time::sleep(Duration::from_secs(30)).await;
        app_handle
            .emit("organize_files_task", json!({"finished": true}))
            .unwrap();
        is_finished = true;
        let time = get_config().await.timer * 60;
        if is_finished {
            for i in 0..time + 1 {
                let total = time;
                tokio::time::sleep(Duration::from_secs(1)).await;
                app_handle
                    .emit("time_to_left", json!({"time": total - i}))
                    .unwrap();
            }
        }
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    let running = Arc::new(Mutex::new(true));
    let notify = Arc::new(Notify::new());

    tauri::Builder::default()
        // .plugin(tauri_plugin_autostart::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_shell::init())
        .invoke_handler(tauri::generate_handler![
            greet,
            read_file,
            get_organizer,
            add_organizer,
            update_organizer,
            delete_organizer,
            run_organizer,
            get_organizer_files,
            set_config,
            get_config
        ])
        .setup({
            let running = Arc::clone(&running);
            let notify = Arc::clone(&notify);
            move |app| {
                #[cfg(desktop)]
                {
                    use tauri_plugin_autostart::MacosLauncher;
                    use tauri_plugin_autostart::ManagerExt;

                    app.handle().plugin(tauri_plugin_autostart::init(MacosLauncher::LaunchAgent, Some(vec![""])));

                    let autostart_manager = app.autolaunch();

                    // read config 
                    let config = tauri::async_runtime::block_on(get_config());

                    if config.run_on_startup {
                        autostart_manager.enable().expect("Failed to enable autostart");
                        println!("registered for autostart? {}", autostart_manager.is_enabled().unwrap());
                    } else {
                        autostart_manager.disable().expect("Failed to disable autostart");
                    }
                }

                let run_i = MenuItem::with_id(app, "run", "Run", true, None::<&str>)?;
                let quit_i = MenuItem::with_id(app, "quit", "Quit", true, None::<&str>)?;
                let menu = Menu::with_items(app, &[&run_i, &quit_i])?;

                let tray = TrayIconBuilder::new()
                    .icon(app.default_window_icon().unwrap().clone())
                    .menu(&menu)
                    .menu_on_left_click(false)
                    .on_tray_icon_event(|tray, event| match event {
                        TrayIconEvent::Click {
                            button: MouseButton::Left,
                            button_state: MouseButtonState::Up,
                            id,
                            position,
                            rect,
                        } => {
                            println!("left click pressed and released");
                            // in this example, let's show and focus the main window when the tray is clicked
                            let app = tray.app_handle();
                            if let Some(window) = app.get_webview_window("main") {
                                let _ = window.show();
                                let _ = window.set_focus();
                            }
                        }
                        _ => {
                            println!("unhandled event {event:?}");
                        }
                    })
                    .on_menu_event(|app, event| match event.id.as_ref() {
                        "quit" => {
                            println!("Quit menu item clicked");
                            app.exit(0);
                        }
                        "run" => {
                            println!("Run menu item clicked");
                            spawn({
                                let app_handle = app.app_handle().clone();
                                async move {
                                    run_organizer().await;
                                }
                            });
                        }
                        _ => {}
                    })
                    .build(app)?;

                spawn({
                    let app_handle = app.handle().clone();
                    async move {
                        organize_files(
                            Arc::clone(&running),
                            Arc::clone(&notify),
                            app_handle.clone(),
                        )
                        .await;
                    }
                });
                Ok(())
            }
        })
        .on_window_event({
            move |window, event| match event {
                tauri::WindowEvent::CloseRequested { api, .. } => {
                    let _ = window.hide();
                    api.prevent_close();
                }
                _ => {}
            }
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
