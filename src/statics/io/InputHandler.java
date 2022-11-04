package statics.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class InputHandler {
    public static final ArrayList<Integer> textCnt = new ArrayList<>();
    public static String CODE;

    private InputHandler(String finName) throws IOException {
        StringBuilder sb = new StringBuilder();
        textCnt.add(0);
        FileReader f = new FileReader(finName);
        BufferedReader reader = new BufferedReader(f);
        String b;
        while ((b = reader.readLine()) != null) {
            sb.append(b).append("\n");
            textCnt.add(sb.length());
        }
        CODE = sb.toString();
        reader.close();
    }

    public static void initiate(String finName) throws IOException {
        InputHandlerInstance.INSTANCE(finName);
    }

    private static class InputHandlerInstance {
        static void INSTANCE(String finName) throws IOException {
            new InputHandler(finName);
        }
    }
}
