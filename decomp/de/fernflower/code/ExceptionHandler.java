/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

public final class ExceptionHandler {
    public int from = 0;
    public int to = 0;
    public int handler = 0;
    public int from_instr = 0;
    public int to_instr = 0;
    public int handler_instr = 0;
    public String exceptionClass = null;

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("from: " + this.from + " to: " + this.to + " handler: " + this.handler + "\n");
        stringBuffer.append("from_instr: " + this.from_instr + " to_instr: " + this.to_instr + " handler_instr: " + this.handler_instr + "\n");
        stringBuffer.append("exceptionClass: " + this.exceptionClass + "\n");
        return stringBuffer.toString();
    }
}

