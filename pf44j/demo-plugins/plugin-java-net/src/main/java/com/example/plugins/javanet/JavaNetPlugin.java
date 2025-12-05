package com.example.plugins.javanet;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class JavaNetPlugin extends Plugin {

    public JavaNetPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("JavaNetPlugin.start()");
    }

    @Override
    public void stop() {
        System.out.println("JavaNetPlugin.stop()");
    }
}
