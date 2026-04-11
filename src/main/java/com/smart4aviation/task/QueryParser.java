package com.smart4aviation.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * Parsuje nam podane argumenty.
 * Robi to w sposób "dynamiczny", nie zapisując całej otrzymanej tabelki,
 * ze względu na ilość podawanych parametrów.
 */
public class QueryParser {
    private final BufferedReader reader;
    private StringTokenizer tokenizer;

    public QueryParser(InputStream is) {
        this.reader = new BufferedReader(new InputStreamReader(is));
    }

    public int[] initializeParameters() throws IOException {
        int n = nextInt();
        int q = nextInt();
        return new int[]{n, q};
    }

    public int[] initializeStartingFlights(int n) throws IOException {
        int[] flights = new int[n];
        for (int i = 0; i < n; i++) {
            flights[i] = nextInt();
        }
        return flights;
    }

    public String next() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreElements()) {
            String line = reader.readLine();
            if (line == null) {
                throw new NoSuchElementException("Koniec strumienia danych");
            }
            tokenizer = new StringTokenizer(line);
        }
        return tokenizer.nextToken();
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(next());
    }

    public long nextLong() throws IOException {
        return Long.parseLong(next());
    }
}
