import { useEffect, useState } from "react";
import Content from "./components/content";
import Navbar from "./components/navbar";
import Titlebar from "./components/titlebar";
import { invoke } from "@tauri-apps/api/core";
import { listen } from "@tauri-apps/api/event";

function App() {
  const [organizers, setOrganizers] = useState<any[]>([]);
  const [isFinished, setIsFinished] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);

  const getOrganizers = async () => {
    return invoke('read_file') as Promise<any[]>;
  };

  useEffect(() => {
    const fetchOrganizers = async () => {
      const organizers = await getOrganizers();
      setOrganizers(organizers);
    };
    fetchOrganizers();

    const listener = listen('organize_files_task', (event) => {
      const payload = event.payload as { finished: boolean };
      if (payload.finished) {
        setIsFinished(true);
        updateOrganizers();
      } else {
        setIsFinished(false);
      }
    })

    const listener2 = listen("time_to_left", (event) => {
      const payload = event.payload as { time: number };
      setTimeLeft(payload.time);
  });

    return () => {
      listener.then((listenerFn) => listenerFn());
      listener2.then((listenerFn) => listenerFn());
    }

  }, []);

  const updateOrganizers = async () => {
    const organizers = await getOrganizers();
    setOrganizers(organizers);
  };

  const calculateTimeLeft = (time: number) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`;
  }

  return (
    <div className="h-screen overflow-hidden select-none">
      <Titlebar />
      <div className="absolute top-11 left-0 right-0 bottom-0 overflow-auto">
        <Navbar organizerCount={organizers.length} readFile={updateOrganizers} />
        <Content organizers={organizers} />
        <div className="absolute bottom-0 left-0 right-0 bg-blue-900 border-t border-blue-600 px-3 py-1 max-h-10">
          <div className="inline-flex gap-4 items-center justify-end text-sm">
            <p>Status: <span className={isFinished ? `text-red-500` : `text-green-500`}>{isFinished === false ? "Active" : "Inactive"}</span></p>
            <p>Time left to next run: <span>{timeLeft === 0 ? "--:--" : calculateTimeLeft(timeLeft)}</span></p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;