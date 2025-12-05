package com.example.api;

import org.pf4j.ExtensionPoint;

public interface NetworkPlugin extends ExtensionPoint {

    String fetchContent(String url);

}
