package com.newcodor.apirequester.Utils;

import java.util.ArrayList;
import java.util.List;

public class StringChunkLoader {

    public static List<String> splitIntoChunks(String text,int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, text.length());
//            System.out.println(end);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }
}


