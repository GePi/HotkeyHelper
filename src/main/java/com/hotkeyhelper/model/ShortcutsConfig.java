package com.hotkeyhelper.model;

import java.util.List;

public class ShortcutsConfig {
    private List<AppConfig> apps;

    public ShortcutsConfig() {
    }

    public ShortcutsConfig(List<AppConfig> apps) {
        this.apps = apps;
    }

    public List<AppConfig> getApps() {
        return apps;
    }

    public void setApps(List<AppConfig> apps) {
        this.apps = apps;
    }
}
