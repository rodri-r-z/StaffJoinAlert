package me.rodrigo.staffsjoinalerts.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Http {
    public static String getFileContentByUrl(URL fileUrl) throws IOException {
        final StringBuilder data = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileUrl.openStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line).append("\n");
        }

        return data.toString();
    }
}
