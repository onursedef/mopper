"use client";

import { useEffect, useState } from "react";
import { useTheme } from "./theme-provider";
import { Moon, Sun } from "lucide-react";

export default function ThemeToggle() {
    const { theme, setTheme } = useTheme();
    const [isDark, setIsDark] = useState(theme === "dark");
    const [isSpinning, setIsSpinning] = useState(false);

    function toggleTheme() {
        setIsDark(!isDark);
        setIsSpinning(true);
        setTheme(isDark ? "light" : "dark");
        console.log(isDark);
    }

    useEffect(() => {
        if (isSpinning) {
            const timer = setTimeout(() => setIsSpinning(false), 150);
            return () => clearTimeout(timer);
        }
    }, [isSpinning]);

    return (
        <button
            onClick={toggleTheme}
            className={`
                flex items-center justify-center gap-2 px-4 py-2 rounded-md
                font-medium text-sm transition-all duration-300 ease-out w-fit
                transform focus:outline-none focus:ring-2
                focus:ring-opacity-50 bg-blue-600 text-white 
                `}
                aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
        >
            <span className={`relative w-5 h-5 ${isSpinning ? 'animate-spin' : ''}`}>
                <span className={`absolute inset-0 transition-opacity duration-300 ease-in-out ${isDark ? 'opacity-0' : 'opacity-100' }`}>
                    <Sun className="w-5 h-5" />
                </span>
                <span className={`absolute inset-0 transition-opacity duration-300 ease-in-out ${isDark ? 'opacity-100' : 'opacity-0' }`}>
                    <Moon className="w-5 h-5" />
                </span>
            </span>
                {isDark ? 'Dark' : 'Light'} Theme
        </button>
    )
}