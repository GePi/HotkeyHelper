package com.hotkeyhelper.model;

public class AppConfig {
    private String processName;
    private String windowTitle;
    private String htmlFile;

    public AppConfig() {
    }

    public AppConfig(String processName, String windowTitle, String htmlFile) {
        this.processName = processName;
        this.windowTitle = windowTitle;
        this.htmlFile = htmlFile;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getHtmlFile() {
        return htmlFile;
    }

    public void setHtmlFile(String htmlFile) {
        this.htmlFile = htmlFile;
    }

    public boolean matches(WindowInfo windowInfo) {
        if (!processName.equalsIgnoreCase(windowInfo.getProcessName())) {
            return false;
        }

        if (windowTitle == null || windowTitle.isEmpty()) {
            return true;
        }

        try {
            return java.util.regex.Pattern.compile(windowTitle, java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(windowInfo.getWindowTitle())
                    .find();
        } catch (java.util.regex.PatternSyntaxException e) {
            return windowInfo.getWindowTitle().contains(windowTitle);
        }
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "processName='" + processName + '\'' +
                ", windowTitle='" + windowTitle + '\'' +
                ", htmlFile='" + htmlFile + '\'' +
                '}';
    }
}
