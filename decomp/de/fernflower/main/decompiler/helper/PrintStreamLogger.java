/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.decompiler.helper;

import de.fernflower.main.extern.IFernflowerLogger;
import de.fernflower.util.InterpreterUtil;
import java.io.PrintStream;

public final class PrintStreamLogger
implements IFernflowerLogger {
    private int severity;
    private int indent;
    private PrintStream stream;

    public PrintStreamLogger(int n, PrintStream printStream) {
        this.severity = n;
        this.indent = 0;
        this.stream = printStream;
    }

    public final void writeMessage(String string, int n) {
        this.writeMessage(string, n, this.indent);
    }

    public final void writeMessage(String string, int n, int n2) {
        if (n >= this.severity) {
            this.stream.println(String.valueOf(InterpreterUtil.getIndentString(n2)) + names[n] + ": " + string);
        }
    }

    public final void startClass(String string) {
        this.stream.println(String.valueOf(InterpreterUtil.getIndentString(this.indent++)) + "Processing class " + string + " ...");
    }

    public final void endClass() {
        this.stream.println(String.valueOf(InterpreterUtil.getIndentString(--this.indent)) + "... proceeded.");
    }

    public final void startWriteClass(String string) {
        this.stream.println(String.valueOf(InterpreterUtil.getIndentString(this.indent++)) + "Writing class " + string + " ...");
    }

    public final void endWriteClass() {
        this.stream.println(String.valueOf(InterpreterUtil.getIndentString(--this.indent)) + "... written.");
    }

    public final void startMethod(String string) {
        if (this.severity <= 2) {
            this.stream.println(String.valueOf(InterpreterUtil.getIndentString(this.indent)) + "Processing method " + string + " ...");
        }
    }

    public final void endMethod() {
        if (this.severity <= 2) {
            this.stream.println(String.valueOf(InterpreterUtil.getIndentString(this.indent)) + "... proceeded.");
        }
    }

    public final int getSeverity() {
        return this.severity;
    }

    public final void setSeverity(int n) {
        this.severity = n;
    }

    public final boolean getShowStacktrace() {
        return true;
    }
}

