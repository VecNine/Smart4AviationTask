package com.smart4aviation.task;

import java.io.IOException;

/**
 * Solver dla podanego problemu.
 */
public class FlightProgram {

    private final int n;
    private final int q;
    private final FenwickTree raw;
    private final FenwickTree bias;
    private final QueryParser queryParser;

    private long lastTime;

    public FlightProgram(QueryParser parser) throws IOException {
        this.queryParser = parser;
        int[] parameters = queryParser.initializeParameters();
        this.n = parameters[0];
        this.q = parameters[1];

        if (parameters[0] <= 0 || parameters[1] < 0) {
            throw new IllegalArgumentException("Parametry n i q muszą być dodatnie!");
        }

        this.raw = new FenwickTree(n);
        this.bias = new FenwickTree(n);

        this.lastTime = 0;
    }

    public void initializeFlights() throws IOException {
        int[] start = queryParser.initializeStartingFlights(n);
        for (int i = 0; i < n; i++) {
            try {
                this.raw.set(i + 1, start[i]);
            }catch (NumberFormatException e) {
                throw new IllegalArgumentException("Błędnie podane wstępne loty! - Najprawdopodobiej źle podane 'n'");
            }catch (Exception e) {
                throw new IllegalArgumentException("Błąd w wstępnych lotach.");
            }
        }
    }

    public void solve() {
        for(int i = 0; i < q; i++) {
            try {
                this.nextQuery();
            } catch (NumberFormatException e) {
                System.err.println("Błąd formatu w linii, oczekiwano liczby.");
            } catch (java.util.NoSuchElementException e) {
                System.err.println("Niekompletna komenda.");
            } catch (IllegalArgumentException e) {
                System.err.println("Błąd: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Nieoczekiwany błąd");
            }
        }
    }

    private void nextQuery() throws IOException {
        String action = queryParser.next();

        switch (action) {
            case "C":
                this.caseC(queryParser.nextInt(), queryParser.nextLong());
                break;

            case "P":
                this.caseP(queryParser.nextInt(), queryParser.nextLong(), queryParser.nextLong());
                break;

            case "A":
                this.caseA(queryParser.nextInt(), queryParser.nextLong(), queryParser.nextLong());
                break;

            case "Q":
                long result = this.caseQ(queryParser.nextInt(), queryParser.nextInt(), queryParser.nextLong());
                System.out.println(result);
                break;

            default:
                throw new IllegalArgumentException("Zły argument!");
        }
    }

    public void caseC(int idx, long time) {
        this.checkTime(time);

        this.raw.set(idx, 0);
        this.bias.set(idx, 0);
    }

    public void caseP(int idx, long newValue, long time) {
        // tutaj tak naprawdę czas nic nie robi, ale dla treści zadania sprawdzam czy jest ustawiony chronologicznie
        this.checkTime(time);

        long oldRaw = this.raw.get(idx);
        long oldBias = this.bias.get(idx);

        long delta = newValue - oldRaw;
        long newBias = oldBias - (delta * time);

        this.raw.set(idx, newValue);
        this.bias.set(idx, newBias);
    }

    public void caseA(int idx, long newValue, long time) {
        this.checkTime(time);

        this.raw.set(idx, newValue);
        this.bias.set(idx, -(newValue * time));
    }

    public long caseQ(int i, int j, long time) {
        this.checkTime(time);

        long sumRaw = this.raw.queryRange(i, j);
        long sumBias = this.bias.queryRange(i, j);

        return (sumRaw * time) + sumBias;
    }

    public void checkTime(long time) {
        if (time < lastTime) {
            throw new IllegalArgumentException("Podany czas nie jest ustawiony chronologicznie!");
        }

        lastTime = time;
    }

}
