package com.hotkeyhelper.service;

import com.hotkeyhelper.model.WindowInfo;
import com.hotkeyhelper.win.WinApi;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WindowTracker {

    private static final int POLL_INTERVAL_MS = 300;
    private final ScheduledExecutorService executor;
    private WindowInfo lastWindowInfo = null;
    private final Consumer<WindowInfo> onWindowChange;

    public WindowTracker(Consumer<WindowInfo> onWindowChange) {
        this.onWindowChange = onWindowChange;
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "WindowTracker");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        executor.scheduleAtFixedRate(this::checkForegroundWindow, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void checkForegroundWindow() {
        try {
            WindowInfo windowInfo = getForegroundWindowInfo();

            if (windowInfo != null && !windowInfo.equals(lastWindowInfo)) {
                lastWindowInfo = windowInfo;
                onWindowChange.accept(windowInfo);
            }
        } catch (Exception e) {
            System.err.println("Error tracking foreground window: " + e.getMessage());
        }
    }

    private WindowInfo getForegroundWindowInfo() {
        HWND hwnd = WinApi.User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return null;
        }

        // Get window title (Unicode)
        char[] titleChars = new char[512];
        int titleLength = WinApi.User32.INSTANCE.GetWindowTextW(hwnd, titleChars, titleChars.length);
        String windowTitle = titleLength > 0 ? new String(titleChars, 0, titleLength) : "";

        // Get window class (Unicode)
        char[] classChars = new char[256];
        int classLength = WinApi.User32.INSTANCE.GetClassNameW(hwnd, classChars, classChars.length);
        String windowClass = classLength > 0 ? new String(classChars, 0, classLength) : "";

        // Get process ID
        int[] processId = new int[1];
        WinApi.User32.INSTANCE.GetWindowThreadProcessId(hwnd, processId);

        if (processId[0] == 0) {
            return null;
        }

        HANDLE hProcess = WinApi.Kernel32.INSTANCE.OpenProcess(
                WinApi.Kernel32.PROCESS_QUERY_LIMITED_INFORMATION,
                false,
                processId[0]
        );

        if (hProcess == null) {
            return null;
        }

        String processPath = "";
        String processName = "";

        try {
            char[] fileNameChars = new char[1024];
            int[] size = new int[]{fileNameChars.length};

            boolean success = WinApi.Kernel32.INSTANCE.QueryFullProcessImageNameW(
                    hProcess,
                    0,
                    fileNameChars,
                    size
            );

            if (success && size[0] > 0) {
                processPath = new String(fileNameChars, 0, size[0]);
                processName = Paths.get(processPath).getFileName().toString();
            }
        } catch (Throwable e) {
            System.err.println("Error getting process name: " + e.getMessage());
        } finally {
            WinApi.Kernel32.INSTANCE.CloseHandle(hProcess);
        }

        if (processName.isEmpty()) {
            return null;
        }

        return new WindowInfo(processName, processPath, processId[0], windowTitle, windowClass);
    }
}
