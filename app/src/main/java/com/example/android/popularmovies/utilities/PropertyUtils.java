package com.example.android.popularmovies.utilities;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    private static Properties getPropertiesFile(Context context, String filename)
    {
        Properties properties = new Properties();
        try {

            InputStream inputStream = context.getAssets().open(filename);
            properties.load(inputStream);
            inputStream.close();
            return properties;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getProperty(Context context, String filename, String key)
    {
        Properties properties = getPropertiesFile(context, filename);
        if(properties == null)
            return null;
        return properties.getProperty(key);
    }

}
