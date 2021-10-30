/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import java.util.ArrayList;
import java.util.List;

public final class ExceptionTable {
    private List handlers = new ArrayList();

    public ExceptionTable() {
    }

    public ExceptionTable(List list) {
        this.handlers = list;
    }

    public final List getHandlers() {
        return this.handlers;
    }
}

