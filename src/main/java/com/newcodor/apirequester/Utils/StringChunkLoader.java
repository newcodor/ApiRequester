package com.newcodor.apirequester.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

    public static Iterator<String> splitChunkIterator(final String text,int chunkSize) {
        return new Iterator<String>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < text.length();
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                // 计算下一块的结束位置
                int endIndex = Math.min(currentIndex + chunkSize, text.length());
                String chunk = text.substring(currentIndex, endIndex);

                // 更新 currentIndex
                currentIndex = endIndex;

                return chunk;
            }
        };
    }
}


