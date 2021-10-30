/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import java.io.DataInputStream;
import java.io.InputStream;

public final class DataInputFullStream
extends DataInputStream {
    public DataInputFullStream(InputStream inputStream) {
        super(inputStream);
    }

    public final int readFull(byte[] byArray) {
        int n = byArray.length;
        byte[] byArray2 = new byte[n];
        int n2 = 0;
        int n3 = 0;
        do {
            if ((n3 = this.read(byArray2, 0, n - n2)) == -1) {
                return -1;
            }
            System.arraycopy(byArray2, 0, byArray, n2, n3);
        } while ((n2 += n3) != n);
        return n;
    }
}

