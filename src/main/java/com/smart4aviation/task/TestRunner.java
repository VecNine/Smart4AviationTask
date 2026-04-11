package com.smart4aviation.task;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

public class TestRunner {

    private String testPath;
    private File folder;
    private File[] testFiles;
    private int passed = 0;
    private int total = 0;

    public TestRunner(String testPath) {
        this.testPath = testPath;
        folder = new File(testPath);
    }

    public void run() {
        if (listAllFiles()) {
            testLoop();
        }
    }

    private void testLoop() {
        System.out.println("Testy z lokalizacji: " + testPath);
        System.out.println("================================================================");

        for (File testFile : testFiles) {
            test(testFile);
        }

        double score = (total > 0) ? ((double) passed / total) * 100 : 0;
        System.out.println("================================================================");
        System.out.printf("Wynik końcowy: %d/%d (%.1f%%)\n", passed, total, score);
    }

    private void test(File testFile) {
        String testName = testFile.getName();
        String id = testName.replaceAll("\\D", "");
        File expectedOutputFile = new File(folder, "odp" + id + ".txt");

        if (!expectedOutputFile.exists()) {
            System.out.println("Pominięto " + testName + " (brak pliku odpowiedzi odp" + id + ".txt)");
            return;
        }

        total++;

        try {
            if (runTest(testFile, expectedOutputFile)) {
                System.out.println("Test " + id + ": PASSED");
                passed++;
            } else {
                System.out.println("Test " + id + ": FAILED");
            }
        } catch (Exception e) {
            System.out.println("Test " + id + ": ERROR (" + e.getMessage() + ")");
            e.printStackTrace();
        }
    }

    private boolean listAllFiles() {
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Błąd: Folder " + testPath + " nie istnieje!");
            return false;
        }

        testFiles = folder.listFiles((dir, name) -> name.matches("test\\d+\\.txt"));

        if (testFiles == null || testFiles.length == 0) {
            System.out.println("Nie znaleziono plików testowych w: " + testPath);
            return false;
        }

        Arrays.sort(testFiles, Comparator.comparingInt(f ->
                Integer.parseInt(f.getName().replaceAll("\\D", ""))));
        return true;
    }


    private static boolean runTest(File input, File expected) throws IOException {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try (InputStream is = new FileInputStream(input)) {
            QueryParser parser = new QueryParser(is);
            FlightProgram program = new FlightProgram(parser);
            program.initializeFlights();
            program.solve();
        } finally {
            System.setOut(originalOut);
        }

        String actual = outContent.toString().trim().replace("\r\n", "\n");
        String expectedContent = Files.readString(expected.toPath()).trim().replace("\r\n", "\n");

        return actual.equals(expectedContent);
    }


    static void main(String[] args) {
        TestRunner testRunner = new TestRunner("./src/test/java/txt_tests");
        testRunner.run();
    }


}