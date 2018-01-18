package ru.synesis.kipod.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author arseny.kovalchuk
 *
 */
public class Hostname {

    public static String get() {
        String OS = System.getProperty("os.name").toLowerCase();
        String res = null;
        if (OS.indexOf("win") >= 0) {
            res = System.getenv("COMPUTERNAME");
        } else {
            if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
                res = System.getenv("HOSTNAME");
                if (res == null) {
                    Path p = Paths.get("/etc/hostname");
                    if (Files.exists(p)) {
                        try {
                            res = Files.readAllLines(p).get(0);
                        } catch (IOException e) {
                            //
                        }
                    }
                }
            }
        }
        return res;
    }
    
}
