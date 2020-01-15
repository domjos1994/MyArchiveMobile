package de.domjos.myarchivelibrary.services;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONService {

    String readUrl(URL url) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        InputStream in = url.openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } finally {
            in.close();
        }

        return sb.toString();
    }

    String getString(JSONObject obj, String key) throws Exception {
        if(obj.has(key)) {
            return obj.getString(key);
        } else {
            return "";
        }
    }

    Integer getInt(JSONObject obj, String key) throws Exception {
        if(obj.has(key)) {
            return obj.getInt(key);
        } else {
            return 0;
        }
    }
}
