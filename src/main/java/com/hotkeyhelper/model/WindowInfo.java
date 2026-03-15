package com.hotkeyhelper.model;

import java.util.Objects;

public class WindowInfo {
    private final String processName;
    private final String processPath;
    private final int processId;
    private final String windowTitle;
    private final String windowClass;

    public WindowInfo(String processName, String processPath, int processId, String windowTitle, String windowClass) {
        this.processName = processName;
        this.processPath = processPath;
        this.processId = processId;
        this.windowTitle = windowTitle;
        this.windowClass = windowClass;
    }

    public String getProcessName() {
        return processName;
    }

    public String getProcessPath() {
        return processPath;
    }

    public int getProcessId() {
        return processId;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public String getWindowClass() {
        return windowClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowInfo that = (WindowInfo) o;
        return processId == that.processId &&
                Objects.equals(processName, that.processName) &&
                Objects.equals(windowTitle, that.windowTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processName, processId, windowTitle);
    }

    @Override
    public String toString() {
        return "WindowInfo{" +
                "processName='" + processName + '\'' +
                ", processPath='" + processPath + '\'' +
                ", processId=" + processId +
                ", windowTitle='" + windowTitle + '\'' +
                ", windowClass='" + windowClass + '\'' +
                '}';
    }
}
