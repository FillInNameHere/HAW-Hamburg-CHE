package com.brianstempin.vindiniumclient.util.vars;

import com.brianstempin.vindiniumclient.util.fileservices.FileServices;
import com.brianstempin.vindiniumclient.util.fileservices.IFileServices;
import com.brianstempin.vindiniumclient.util.vars.model.Vars;
import com.google.gson.Gson;

import java.io.File;

/**
 * Created by Christian on 08.05.2016.
 */
public class VarServices {
    String path = "file/vars.vfv";
    IFileServices fs;
    Gson gson;

    public VarServices() {
        this.fs = new FileServices();
        this.gson = new Gson();
    }

    public Vars getVars() {
        Vars res = null;
        String s = this.fs.readFileToString(this.path);
        res = gson.fromJson(s, Vars.class);
        return res;
    }
}
