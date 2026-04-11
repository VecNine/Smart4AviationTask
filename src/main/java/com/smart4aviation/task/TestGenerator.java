package com.smart4aviation.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TestGenerator {

    private final String targetDir;
    private final int n;
    private final int q;
    private final Random rand = new Random();

    public TestGenerator(String targetDir, int n, int q) {
        this.targetDir = targetDir;
        this.n = n;
        this.q = q;
    }

    /**
     * Zarządza procesem tworzenia plików.
     */
    public void run() throws IOException {
        int nextId = findNextTestId(targetDir);
        String testFileName = targetDir + "test" + nextId + ".txt";
        String odpFileName = targetDir + "odp" + nextId + ".txt";

        System.out.println("Generowanie testu nr " + nextId + " [" + n + " tras, " + q + " zapytań]");

        try (PrintWriter testOut = new PrintWriter(new FileWriter(testFileName));
             PrintWriter odpOut = new PrintWriter(new FileWriter(odpFileName))) {

            generateTestData(testOut, odpOut);
        }

        System.out.println("Wygenerowano: " + testFileName);
        System.out.println("Wygenerowano: " + odpFileName);
    }

    private void generateTestData(PrintWriter testOut, PrintWriter odpOut) {
        TrackState[] tracks = new TrackState[n + 1];
        for (int i = 1; i <= n; i++) tracks[i] = new TrackState();

        testOut.println(n + " " + q);
        generateInitialState(tracks, testOut);

        long currentTime = 0;
        for (int k = 0; k < q; k++) {
            currentTime += rand.nextInt(10) + 1;
            int type = rand.nextInt(4); // 0:C, 1:P, 2:A, 3:Q
            int i = rand.nextInt(n) + 1;

            boolean success = switch (type) {
                case 0 -> handleCancel(i, currentTime, tracks, testOut);
                case 1 -> handleUpdate(i, currentTime, tracks, testOut);
                case 2 -> handleAdd(i, currentTime, tracks, testOut);
                case 3 -> handleQuery(i, currentTime, tracks, testOut, odpOut);
                default -> false;
            };

            if (!success) k--;
        }
    }

    private void generateInitialState(TrackState[] tracks, PrintWriter testOut) {
        for (int i = 1; i <= n; i++) {
            if (rand.nextBoolean()) {
                int p = rand.nextInt(100) + 1;
                tracks[i].currentP = p;
                tracks[i].isActive = true;
                testOut.print(p);
            } else {
                testOut.print("0");
            }
            testOut.print(i == n ? "" : " ");
        }
        testOut.println();
    }

    private boolean handleCancel(int i, long time, TrackState[] tracks, PrintWriter out) {
        if (!tracks[i].isActive) return false;

        tracks[i].currentP = 0;
        tracks[i].totalHistory = 0;
        tracks[i].isActive = false;
        out.printf("C %d %d%n", i, time);
        return true;
    }

    private boolean handleUpdate(int i, long time, TrackState[] tracks, PrintWriter out) {
        if (!tracks[i].isActive) return false;

        int newP = rand.nextInt(100) + 1;
        tracks[i].totalHistory = tracks[i].calculateCurrentTotal(time);
        tracks[i].currentP = newP;
        tracks[i].lastUpdateTime = time;
        out.printf("P %d %d %d%n", i, newP, time);
        return true;
    }

    private boolean handleAdd(int i, long time, TrackState[] tracks, PrintWriter out) {
        if (tracks[i].isActive) return false;

        int newP = rand.nextInt(100) + 1;
        tracks[i].totalHistory = 0;
        tracks[i].currentP = newP;
        tracks[i].lastUpdateTime = time;
        tracks[i].isActive = true;
        out.printf("A %d %d %d%n", i, newP, time);
        return true;
    }

    private boolean handleQuery(int i, long time, TrackState[] tracks, PrintWriter out, PrintWriter odp) {
        int j = i + rand.nextInt(n - i + 1);
        long queryResult = 0;

        for (int m = i; m <= j; m++) {
            queryResult += tracks[m].calculateCurrentTotal(time);
        }

        out.printf("Q %d %d %d%n", i, j, time);
        odp.println(queryResult);
        return true;
    }

    private static int findNextTestId(String path) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.matches("test\\d+\\.txt"));
        if (files == null || files.length == 0) return 1;

        int maxId = 0;
        for (File f : files) {
            String idStr = f.getName().replaceAll("\\D", "");
            if (!idStr.isEmpty()) {
                maxId = Math.max(maxId, Integer.parseInt(idStr));
            }
        }
        return maxId + 1;
    }


    static void main(String[] args) {
        try {
            String path = "src/test/java/txt_tests/";
            TestGenerator generator = new TestGenerator(path, 1000, 1000);
            generator.run();
        } catch (IOException e) {
            System.err.println("Błąd podczas generowania plików: " + e.getMessage());
            e.printStackTrace();
        }
    }
}