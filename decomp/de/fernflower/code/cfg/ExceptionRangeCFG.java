/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code.cfg;

import de.fernflower.code.cfg.BasicBlock;
import java.util.ArrayList;
import java.util.List;

public final class ExceptionRangeCFG {
    private List protectedRange = new ArrayList();
    private BasicBlock handler;
    private String exceptionType;

    public ExceptionRangeCFG(List list, BasicBlock basicBlock, String string) {
        this.protectedRange = list;
        this.handler = basicBlock;
        this.exceptionType = string;
    }

    public final boolean isCircular() {
        return this.protectedRange.contains(this.handler);
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("exceptionType: " + this.exceptionType + "\n");
        stringBuffer.append("handler: " + this.handler.id + "\n");
        stringBuffer.append("range: ");
        int n = 0;
        while (n < this.protectedRange.size()) {
            stringBuffer.append(String.valueOf(((BasicBlock)this.protectedRange.get((int)n)).id) + " ");
            ++n;
        }
        stringBuffer.append("\n");
        return stringBuffer.toString();
    }

    public final BasicBlock getHandler() {
        return this.handler;
    }

    public final void setHandler(BasicBlock basicBlock) {
        this.handler = basicBlock;
    }

    public final List getProtectedRange() {
        return this.protectedRange;
    }

    public final String getExceptionType() {
        return this.exceptionType;
    }
}

