/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class SequenceStatement
extends Statement {
    private SequenceStatement() {
        this.type = 15;
    }

    public SequenceStatement(List object) {
        this();
        this.lastBasicType = ((Statement)object.get(object.size() - 1)).getLastBasicType();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (Statement)iterator.next();
            this.stats.addWithKey(object, ((Statement)object).id);
        }
        this.first = (Statement)this.stats.get(0);
    }

    private SequenceStatement(Statement statement, Statement object) {
        this(Arrays.asList(statement, object));
        object = ((Statement)object).getSuccessorEdges(0x40000000);
        if (!object.isEmpty() && ((StatEdge)(object = (StatEdge)object.get(0))).getType() == 1 && ((StatEdge)object).getDestination() != statement) {
            this.post = ((StatEdge)object).getDestination();
        }
    }

    public static Statement isHead2Block(Statement statement) {
        if (statement.getLastBasicType() != 2) {
            return null;
        }
        Object object = null;
        List list = statement.getSuccessorEdges(0x40000000);
        if (!list.isEmpty()) {
            object = (StatEdge)list.get(0);
        }
        if (object != null && ((StatEdge)object).getType() == 1 && (object = ((StatEdge)object).getDestination()) != statement && ((Statement)object).getPredecessorEdges(1).size() == 1 && !((Statement)object).isMonitorEnter() && ((Statement)object).getLastBasicType() == 2 && DecHelper.checkStatementExceptions(Arrays.asList(statement, object))) {
            return new SequenceStatement(statement, (Statement)object);
        }
        return null;
    }

    public final String toJava(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        String string = null;
        boolean bl = this.isLabeled();
        stringBuilder.append(ExprProcessor.listToJava(this.varDefinitions, n));
        if (bl) {
            string = InterpreterUtil.getIndentString(n);
            ++n;
            stringBuilder.append(String.valueOf(string) + "label" + this.id + ": {\r\n");
        }
        boolean bl2 = false;
        int n2 = 0;
        while (n2 < this.stats.size()) {
            Statement statement = (Statement)this.stats.get(n2);
            if (n2 > 0 && bl2) {
                stringBuilder.append("\r\n");
            }
            String string2 = ExprProcessor.jmpWrapper(statement, n, false);
            stringBuilder.append(string2);
            bl2 = string2.trim().length() > 0;
            ++n2;
        }
        if (bl) {
            stringBuilder.append(String.valueOf(string) + "}\r\n");
        }
        return stringBuilder.toString();
    }

    public final Statement getSimpleCopy() {
        return new SequenceStatement();
    }
}

