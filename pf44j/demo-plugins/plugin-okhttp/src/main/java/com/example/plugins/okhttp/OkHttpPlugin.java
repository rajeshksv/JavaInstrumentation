package com.example.plugins.okhttp;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class OkHttpPlugin extends Plugin {

    public OkHttpPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("OkHttpPlugin.start()");
    }

    @Override
    public void stop() {
        System.out.println("OkHttpPlugin.stop()");
    }
}
