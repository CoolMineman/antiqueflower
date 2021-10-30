/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.Collection;
import java.util.HashSet;

public final class GeneralStatement
extends Statement {
    private GeneralStatement() {
        this.type = 0;
    }

    public GeneralStatement(Statement statement, Collection object, Statement statement2) {
        this();
        this.first = statement;
        this.stats.addWithKey(statement, statement.id);
        object = new HashSet(object);
        ((HashSet)object).remove(statement);
        object = ((HashSet)object).iterator();
        while (object.hasNext()) {
            statement = (Statement)object.next();
            this.stats.addWithKey(statement, statement.id);
        }
        this.post = statement2;
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuffer stringBuffer = new StringBuffer();
        if (this.isLabeled()) {
            stringBuffer.append(String.valueOf(string) + "label" + this.id + ":\r\n");
        }
        stringBuffer.append(String.valueOf(string) + "abstract statement {\r\n");
        int n2 = 0;
        while (n2 < this.stats.size()) {
            stringBuffer.append(((Statement)this.stats.get(n2)).toJava(n + 1));
            ++n2;
        }
        stringBuffer.append(String.valueOf(string) + "}");
        return stringBuffer.toString();
    }
}

