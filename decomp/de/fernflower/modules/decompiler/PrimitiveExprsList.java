/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.ExprentStack;
import java.util.ArrayList;
import java.util.List;

public final class PrimitiveExprsList {
    private List lstExprents = new ArrayList();
    private ExprentStack stack = new ExprentStack();

    public final PrimitiveExprsList copyStack() {
        PrimitiveExprsList primitiveExprsList = new PrimitiveExprsList();
        new PrimitiveExprsList().stack = this.stack.clone();
        return primitiveExprsList;
    }

    public final List getLstExprents() {
        return this.lstExprents;
    }

    public final ExprentStack getStack() {
        return this.stack;
    }
}

