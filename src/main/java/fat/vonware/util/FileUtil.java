package fat.vonware.util;

import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.*;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileUtil implements Util
{
    public static void appendTextFile(final String data, final String file) {
        try {
            final Path path = Paths.get(file, new String[0]);
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to write file: " + file);
        }
    }

    public static List<String> readTextFileAllLines(final String file) {
        try {
            final Path path = Paths.get(file, new String[0]);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to read file, creating new file: " + file);
            appendTextFile("", file);
            return Collections.emptyList();
        }
    }

    public static String getHWID() {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] bytes = messageDigest.digest((System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")).getBytes());
            final StringBuilder stringBuilder = new StringBuilder();
            for (final byte i : bytes) {
                final String hex = Integer.toHexString(0xFF & i);
                stringBuilder.append((hex.length() == 1) ? "0" : hex);
            }
            return stringBuilder.toString();
        }
        catch (Exception ignored) {
            return "Failed to get hwid";
        }
    }
}
