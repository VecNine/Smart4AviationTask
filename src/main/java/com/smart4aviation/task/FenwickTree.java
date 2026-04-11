package com.smart4aviation.task;

/**
 * Implementacja drzewa Fernwick'a aby zmniejszyć złożoność opcji sumowania miejsc.
 * Wykorzystuje do tego tablic o typach prymitywnych, ze względu na potencjalną ogromną liczbę danych.
 * Inspiracja: https://www.youtube.com/watch?v=uSFzHCZ4E-8
 */
public class FenwickTree {

    private final long[] tree;
    private final long[] currentValues;
    private final int n;

    public FenwickTree(int n) {
        this.n = n;
        this.tree = new long[n+1];
        this.currentValues = new long[n+1];
    }

    public void add(int idx, long value) {
        if (idx < 1 || idx > n) return;
        for (; idx <= n; idx += idx & -idx) {
            tree[idx] += value;
        }
    }

    public long get(int idx) {
        return currentValues[idx];
    }

    public void set(int idx, long value) {
        long delta = value - currentValues[idx];
        this.add(idx, delta);
        currentValues[idx] = value;
    }

    public long query(int idx) {
        if (idx < 1) return 0;
        long sum = 0;
        for (; idx > 0; idx -= idx & -idx) {
            sum += tree[idx];
        }
        return sum;
    }

    public long queryRange(int i, int j) {
        if (i > j || i < 1) return 0;
        return query(j) - query(i - 1);
    }
}
