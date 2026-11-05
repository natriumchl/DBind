package com.dbind.dbindmod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;


public class DependencyDownloader {
    private static final String version = "2.109.0";
    private static final String url = "https://dlang.org" + version + "/dmd." + version + ".windows.zip";
    private static final Path targetPath = Paths.get("dmd-" + version + ".zip");
}