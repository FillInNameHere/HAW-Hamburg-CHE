package com.brianstempin.vindiniumclient.util.fileservices;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created by Christian on 08.05.2016.
 */
public class FileServices implements IFileServices {
    @Override
    public String readFileToString(String path) {
        try {
            File file = new File(path);
            String input = Files.toString(file, Charsets.UTF_8);
            return input;
        } catch(IOException ioe) {
            return "No File found";
        }
    }

    @Override
    public boolean writeToFileFromString(String path, String content) {
        try {
            File file = new File(path);
            Files.write(content, file, Charsets.UTF_8);
        } catch(IOException ioe) {
            return false;
        }
        return true;
    }
}
