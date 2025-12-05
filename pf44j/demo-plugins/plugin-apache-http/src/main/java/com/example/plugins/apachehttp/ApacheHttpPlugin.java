package com.example.plugins.apachehttp;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class ApacheHttpPlugin extends Plugin {

    public ApacheHttpPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("ApacheHttpPlugin.start()");
    }

    @Override
    public void stop() {
        System.out.println("ApacheHttpPlugin.stop()");
    }
}
