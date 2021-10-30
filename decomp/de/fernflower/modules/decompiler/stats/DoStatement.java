/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class DoStatement
extends Statement {
    private int looptype;
    private List initExprent = new ArrayList();
    private List conditionExprent = new ArrayList();
    private List incExprent = new ArrayList();

    private DoStatement() {
        this.type = 5;
        this.looptype = 0;
        this.initExprent.add(null);
        this.conditionExprent.add(null);
        this.incExprent.add(null);
    }

    private DoStatement(Statement statement) {
        this();
        this.first = statement;
        this.stats.addWithKey(this.first, this.first.id);
    }

    public static Statement isHead(Statement statement) {
        if (statement.getLastBasicType() == 2 && !statement.isMonitorEnter()) {
            StatEdge statEdge = null;
            List list = statement.getSuccessorEdges(0x40000000);
            if (!list.isEmpty()) {
                statEdge = (StatEdge)list.get(0);
            }
            if (statEdge != null && statEdge.getType() == 1 && statEdge.getDestination() == statement) {
                return new DoStatement(statement);
            }
            if (statement.type != 5 && (statEdge == null || statEdge.getType() != 1) && statement.getContinueSet().contains(statement.getBasichead())) {
                return new DoStatement(statement);
            }
        }
        return null;
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ExprProcessor.listToJava(this.varDefinitions, n));
        if (this.isLabeled()) {
            stringBuffer.append(String.valueOf(string) + "label" + this.id + ":\r\n");
        }
        switch (this.looptype) {
            case 0: {
                stringBuffer.append(String.valueOf(string) + "while(true) {\r\n");
                stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
                stringBuffer.append(String.valueOf(string) + "}\r\n");
                break;
            }
            case 1: {
                stringBuffer.append(String.valueOf(string) + "do {\r\n");
                stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
                stringBuffer.append(String.valueOf(string) + "} while(" + ((Exprent)this.conditionExprent.get(0)).toJava(n) + ");\r\n");
                break;
            }
            case 2: {
                stringBuffer.append(String.valueOf(string) + "while(" + ((Exprent)this.conditionExprent.get(0)).toJava(n) + ") {\r\n");
                stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
                stringBuffer.append(String.valueOf(string) + "}\r\n");
                break;
            }
            case 3: {
                stringBuffer.append(String.valueOf(string) + "for(" + (this.initExprent.get(0) == null ? "" : ((Exprent)this.initExprent.get(0)).toJava(n)) + "; " + ((Exprent)this.conditionExprent.get(0)).toJava(n) + "; " + ((Exprent)this.incExprent.get(0)).toJava(n) + ") {\r\n");
                stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
                stringBuffer.append(String.valueOf(string) + "}\r\n");
            }
        }
        return stringBuffer.toString();
    }

    public final List getSequentialObjects() {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        switch (this.looptype) {
            case 3: {
                if (this.getInitExprent() != null) {
                    arrayList.add(this.getInitExprent());
                }
            }
            case 2: {
                arrayList.add(this.getConditionExprent());
            }
        }
        arrayList.add(this.first);
        switch (this.looptype) {
            case 1: {
                arrayList.add(this.getConditionExprent());
                break;
            }
            case 3: {
                arrayList.add(this.getIncExprent());
            }
        }
        return arrayList;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (this.initExprent.get(0) == exprent) {
            this.initExprent.set(0, exprent2);
        }
        if (this.conditionExprent.get(0) == exprent) {
            this.conditionExprent.set(0, exprent2);
        }
        if (this.incExprent.get(0) == exprent) {
            this.incExprent.set(0, exprent2);
        }
    }

    public final Statement getSimpleCopy() {
        return new DoStatement();
    }

    public final List getInitExprentList() {
        return this.initExprent;
    }

    public final List getConditionExprentList() {
        return this.conditionExprent;
    }

    public final List getIncExprentList() {
        return this.incExprent;
    }

    public final Exprent getConditionExprent() {
        return (Exprent)this.conditionExprent.get(0);
    }

    public final void setConditionExprent(Exprent exprent) {
        this.conditionExprent.set(0, exprent);
    }

    public final Exprent getIncExprent() {
        return (Exprent)this.incExprent.get(0);
    }

    public final void setIncExprent(Exprent exprent) {
        this.incExprent.set(0, exprent);
    }

    public final Exprent getInitExprent() {
        return (Exprent)this.initExprent.get(0);
    }

    public final void setInitExprent(Exprent exprent) {
        this.initExprent.set(0, exprent);
    }

    public final int getLooptype() {
        return this.looptype;
    }

    public final void setLooptype(int n) {
        this.looptype = n;
    }
}

