package com.brianstempin.vindiniumclient.util.fileservices;

/**
 * Created by Christian on 08.05.2016.
 */
public interface IFileServices {
    public String readFileToString(String path);
    public boolean writeToFileFromString(String path, String content);
}
