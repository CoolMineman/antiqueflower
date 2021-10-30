/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.util.ListStack;

public final class ExprentStack
extends ListStack {
    public ExprentStack() {
    }

    private ExprentStack(ListStack listStack) {
        super(listStack);
        this.pointer = listStack.getPointer();
    }

    public final Exprent push(Exprent exprent) {
        super.push(exprent);
        return exprent;
    }

    public final Exprent pop() {
        return (Exprent)this.remove(--this.pointer);
    }

    public final ExprentStack clone() {
        return new ExprentStack(this);
    }
}

