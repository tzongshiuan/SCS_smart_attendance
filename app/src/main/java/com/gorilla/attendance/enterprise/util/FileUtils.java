package com.gorilla.attendance.enterprise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by ggshao on 2017/2/7.
 */

public class FileUtils {
    static final String TAG = "FileUtils";

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
        } catch (IOException e) {
            LOG.E(TAG, e.toString());
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        File fl = new File(filePath);
        FileInputStream fin;
        String ret = null;

        try {
            fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);

            //Make sure you close all streams.
            fin.close();
        } catch (Exception e) {
            LOG.E(TAG, e.toString());
            e.printStackTrace();
        }

        return ret;
    }

    public static boolean writeToFile(String xml, String path) {
        boolean result = false;
        File chF = null;
        try {
            chF = new File(path);
            if (chF.exists()) {
                chF.delete();
                chF.createNewFile();
            } else {
                chF.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(chF);
            byte bbb[] = xml.getBytes("UTF-8");
            for (int i = 0; i < bbb.length; i++) {
                fos.write(bbb[i]);
            }
            fos.close();
            result = true;
            LOG.V(TAG, "Write to file " + chF.getAbsolutePath() + " OK");
        } catch (Exception e) {
            LOG.E(TAG, "Error occurs in writing to file " + chF.getAbsolutePath(), e);
        }

        return result;
    }

    public static boolean renameFile(String originName, String newName) {
        boolean success = false;
        File from = new File(originName);
        File to = new File(newName);
        if (from.exists())
            success = from.renameTo(to);

        return success;
    }
}
