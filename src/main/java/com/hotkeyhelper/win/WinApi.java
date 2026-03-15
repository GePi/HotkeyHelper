package com.hotkeyhelper.win;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

public class WinApi {

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        /**
         * Retrieves a handle to the foreground window (the window with which the user is currently working).
         */
        HWND GetForegroundWindow();

        /**
         * Retrieves the identifier of the thread that created the specified window
         * and, optionally, the identifier of the process that created the window.
         */
        int GetWindowThreadProcessId(HWND hWnd, int[] lpdwProcessId);

        /**
         * Copies the text of the specified window's title bar into a buffer (Unicode version).
         */
        int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);

        /**
         * Retrieves the name of the class to which the specified window belongs (Unicode version).
         */
        int GetClassNameW(HWND hWnd, char[] lpClassName, int nMaxCount);
    }

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        int PROCESS_QUERY_LIMITED_INFORMATION = 0x1000;

        /**
         * Opens an existing local process object.
         */
        HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

        /**
         * Closes an open object handle.
         */
        boolean CloseHandle(HANDLE hObject);

        /**
         * Retrieves the full path of the executable file for the specified process (Unicode version).
         */
        boolean QueryFullProcessImageNameW(HANDLE hProcess, int dwFlags, char[] lpExeName, int[] lpdwSize);
    }
}
