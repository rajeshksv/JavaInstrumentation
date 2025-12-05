package com.example.app;

import com.example.api.NetworkPlugin;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Create the plugin manager
        // We need to tell PF4J where to look for plugins.
        // In a real app, this might be a 'plugins' directory.
        // For this demo, we'll point to the 'demo-plugins' directory where we build them.
        // However, standard PF4J expects a specific structure or zip files.
        // For simplicity in development, we can point to the target directories if we want,
        // but the standard way is to have a 'plugins' folder.
        // Let's assume we run this from the root and plugins are in 'plugins' folder.
        
        System.setProperty("pf4j.pluginsDir", "plugins");
        
        PluginManager pluginManager = new DefaultPluginManager();
        
        // Load plugins
        pluginManager.loadPlugins();
        
        // Start plugins
        pluginManager.startPlugins();
        
        System.out.println("Plugind loaded: " + pluginManager.getPlugins().size());

        // Retrieve extensions for NetworkPlugin
        List<NetworkPlugin> networkPlugins = pluginManager.getExtensions(NetworkPlugin.class);
        System.out.println("Extensions found: " + networkPlugins.size());

        String testUrl = "http://google.com"; // Simple test URL

        for (NetworkPlugin plugin : networkPlugins) {
            System.out.println("Using plugin: " + plugin.getClass().getName());
            String content = plugin.fetchContent(testUrl);
            // Print first 100 chars to avoid spamming console
            System.out.println("Response: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
            System.out.println("---");
        }

        // Stop plugins
        pluginManager.stopPlugins();
    }
}
