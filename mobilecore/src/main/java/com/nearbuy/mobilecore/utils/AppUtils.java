package com.nearbuy.mobilecore.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by Ankur Singh on 14/04/16.
 */
public class AppUtils {
    private static final String TAG = LogUtils.makeLogTag(AppUtils.class);
    public static String convertStreamToString(InputStream is) {
        LogUtils.enter(TAG, LogUtils.getMethodName());
        /*
         * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuffer sb = new StringBuffer();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            LogUtils.info("Memory Exception", "Out of memory");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        LogUtils.info("Converted.", "" + new Date());
        LogUtils.exit(TAG, LogUtils.getMethodName());
        return sb.toString();
    }
    /**
     * Returns true if the given string is null or empty.
     */
    public static boolean isNullOrEmpty(final String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().equals("")) {
            return true;
        }
        return false;
    }
}
