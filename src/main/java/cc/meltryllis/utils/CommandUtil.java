package cc.meltryllis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CommandUtil {
    public static String exec(String command) {
        String[] cmdarray = new String[]{
                "cmd",
                "/c",
                command
        };
        try {
            Process process = Runtime.getRuntime().exec(cmdarray);
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            String line;
            StringBuilder inputResult = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                inputResult.append(line).append("\n");
            }
            return inputResult.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(exec("java -version").length());
    }
}
