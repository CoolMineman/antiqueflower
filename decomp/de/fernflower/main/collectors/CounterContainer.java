/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.collectors;

public final class CounterContainer {
    private int[] values = new int[]{1, 1, 1};

    public final void setCounter(int n, int n2) {
        this.values[n] = n2;
    }

    public final int getCounter() {
        return this.values[0];
    }

    public final int getCounterAndIncrement(int n) {
        int n2 = n;
        int n3 = this.values[n2];
        this.values[n2] = n3 + 1;
        return n3;
    }
}

