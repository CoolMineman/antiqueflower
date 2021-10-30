/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class SynchronizedStatement
extends Statement {
    private Statement body;
    private List headexprent = new ArrayList();

    public SynchronizedStatement() {
        this.type = 10;
        this.headexprent.add(null);
    }

    public SynchronizedStatement(Statement object, Statement statement, Statement statement2) {
        this();
        this.first = object;
        this.stats.addWithKey(object, ((Statement)object).id);
        this.body = statement;
        this.stats.addWithKey(statement, statement.id);
        this.stats.addWithKey(statement2, statement2.id);
        object = statement.getSuccessorEdges(0x40000000);
        if (!object.isEmpty() && ((StatEdge)(object = (StatEdge)object.get(0))).getType() == 1) {
            this.post = ((StatEdge)object).getDestination();
        }
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ExprProcessor.listToJava(this.varDefinitions, n));
        stringBuffer.append(this.first.toJava(n));
        if (this.isLabeled()) {
            stringBuffer.append(String.valueOf(string) + "label" + this.id + ":\r\n");
        }
        stringBuffer.append(String.valueOf(string) + ((Exprent)this.headexprent.get(0)).toJava(n) + " {\r\n");
        stringBuffer.append(ExprProcessor.jmpWrapper(this.body, n + 1, true));
        stringBuffer.append(String.valueOf(string) + "}\r\n");
        return stringBuffer.toString();
    }

    public final void initExprents() {
        this.headexprent.set(0, (Exprent)this.first.getExprents().remove(this.first.getExprents().size() - 1));
    }

    public final List getSequentialObjects() {
        ArrayList arrayList = new ArrayList(this.stats);
        arrayList.add(1, this.headexprent.get(0));
        return arrayList;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (this.headexprent.get(0) == exprent) {
            this.headexprent.set(0, exprent2);
        }
    }

    public final void replaceStatement(Statement statement, Statement statement2) {
        if (this.body == statement) {
            this.body = statement2;
        }
        super.replaceStatement(statement, statement2);
    }

    public final void removeExc() {
        Statement statement = (Statement)this.stats.get(2);
        SequenceHelper.destroyStatementContent(statement, true);
        this.stats.removeWithKey(statement.id);
    }

    public final Statement getSimpleCopy() {
        return new SynchronizedStatement();
    }

    public final void initSimpleCopy() {
        this.first = (Statement)this.stats.get(0);
        this.body = (Statement)this.stats.get(1);
    }

    public final Statement getBody() {
        return this.body;
    }

    public final List getHeadexprentList() {
        return this.headexprent;
    }
}

