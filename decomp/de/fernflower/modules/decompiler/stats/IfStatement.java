/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.IfExprent;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IfStatement
extends Statement {
    public static int IFTYPE_IFELSE = 1;
    public int iftype;
    private Statement ifstat;
    private Statement elsestat;
    private StatEdge ifedge;
    private StatEdge elseedge;
    private boolean negated = false;
    private boolean iffflag;
    private List headexprent = new ArrayList();

    private IfStatement() {
        this.type = 2;
        this.headexprent.add(null);
    }

    private IfStatement(Statement statement, int n, Statement object) {
        this();
        this.first = statement;
        this.stats.addWithKey(statement, statement.id);
        List list = statement.getSuccessorEdges(0x40000000);
        switch (n) {
            case 0: {
                this.ifstat = null;
                this.elsestat = null;
                break;
            }
            case 1: {
                this.ifstat = null;
                this.elsestat = null;
                object = (StatEdge)list.get(1);
                if (((StatEdge)object).getType() != 1) {
                    this.post = ((StatEdge)list.get(0)).getDestination();
                    break;
                }
                this.post = ((StatEdge)object).getDestination();
                this.negated = true;
                break;
            }
            case 2: {
                this.elsestat = ((StatEdge)list.get(0)).getDestination();
                this.ifstat = ((StatEdge)list.get(1)).getDestination();
                List list2 = this.ifstat.getSuccessorEdges(1);
                List list3 = this.elsestat.getSuccessorEdges(1);
                if (this.ifstat.getPredecessorEdges(1).size() > 1 || list2.size() > 1) {
                    this.post = this.ifstat;
                } else if (this.elsestat.getPredecessorEdges(1).size() > 1 || list3.size() > 1) {
                    this.post = this.elsestat;
                } else if (list2.size() == 0) {
                    this.post = this.elsestat;
                } else if (list3.size() == 0) {
                    this.post = this.ifstat;
                }
                if (this.ifstat == this.post) {
                    if (this.elsestat != this.post) {
                        this.ifstat = this.elsestat;
                        this.negated = true;
                    } else {
                        this.ifstat = null;
                    }
                    this.elsestat = null;
                } else if (this.elsestat == this.post) {
                    this.elsestat = null;
                } else {
                    this.post = object;
                }
                if (this.elsestat != null) break;
                n = 1;
            }
        }
        this.ifedge = (StatEdge)list.get(this.negated ? 0 : 1);
        this.elseedge = n == 2 ? (StatEdge)list.get(this.negated ? 1 : 0) : null;
        int n2 = this.iftype = n == 2 ? IFTYPE_IFELSE : 0;
        if (this.iftype == 0) {
            if (n == 0) {
                object = (StatEdge)list.get(0);
                statement.removeSuccessor((StatEdge)object);
                ((StatEdge)object).setSource(this);
                this.addSuccessor((StatEdge)object);
            } else if (n == 1) {
                object = (StatEdge)list.get(this.negated ? 1 : 0);
                statement.removeSuccessor((StatEdge)object);
            }
        }
        if (this.ifstat != null) {
            this.stats.addWithKey(this.ifstat, this.ifstat.id);
        }
        if (this.elsestat != null) {
            this.stats.addWithKey(this.elsestat, this.elsestat.id);
        }
        if (this.post == statement) {
            this.post = this;
        }
    }

    public static Statement isHead(Statement statement) {
        if (statement.type == 8 && statement.getLastBasicType() == 0) {
            ArrayList arrayList;
            int n = statement.getSuccessorEdges(1).size();
            Statement statement2 = null;
            boolean bl = n < 2;
            if (!bl && DecHelper.isChoiceStatement(statement, arrayList = new ArrayList())) {
                statement2 = (Statement)arrayList.remove(0);
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    Object var3_4 = null;
                    if (!((Statement)iterator.next()).isMonitorEnter()) continue;
                    return null;
                }
                bl = DecHelper.checkStatementExceptions(arrayList);
            }
            if (bl) {
                return new IfStatement(statement, n, statement2);
            }
        }
        return null;
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ExprProcessor.listToJava(((IfStatement)object).varDefinitions, n));
        stringBuffer.append(((IfStatement)object).first.toJava(n));
        if (((Statement)object).isLabeled()) {
            stringBuffer.append(String.valueOf(string) + "label" + ((IfStatement)object).id + ":\r\n");
        }
        stringBuffer.append(String.valueOf(string) + ((Exprent)((IfStatement)object).headexprent.get(0)).toJava(n) + " {\r\n");
        if (((IfStatement)object).ifstat == null) {
            stringBuffer.append(InterpreterUtil.getIndentString(n + 1));
            if (object.ifedge.explicit) {
                if (((IfStatement)object).ifedge.getType() == 4) {
                    stringBuffer.append("break");
                } else {
                    stringBuffer.append("continue");
                }
                if (object.ifedge.labeled) {
                    stringBuffer.append(" label" + object.ifedge.closure.id);
                }
            }
            stringBuffer.append(";\r\n");
        } else {
            stringBuffer.append(ExprProcessor.jmpWrapper(((IfStatement)object).ifstat, n + 1, true));
        }
        boolean bl = false;
        if (((IfStatement)object).elsestat != null) {
            Object object;
            if (object.elsestat.type == 2 && object.elsestat.varDefinitions.isEmpty() && ((IfStatement)object).elsestat.getFirst().getExprents().isEmpty() && !((IfStatement)object).elsestat.isLabeled() && (((IfStatement)object).elsestat.getSuccessorEdges(0x40000000).isEmpty() || !((StatEdge)object.elsestat.getSuccessorEdges((int)0x40000000).get((int)0)).explicit)) {
                object = ExprProcessor.jmpWrapper(((IfStatement)object).elsestat, n, false).substring(string.length());
                stringBuffer.append(String.valueOf(string) + "} else ");
                stringBuffer.append((String)object);
                bl = true;
            } else {
                object = ExprProcessor.jmpWrapper(((IfStatement)object).elsestat, n + 1, false);
                if (((String)object).length() > 0) {
                    stringBuffer.append(String.valueOf(string) + "} else {\r\n");
                    stringBuffer.append((String)object);
                }
            }
        }
        if (!bl) {
            stringBuffer.append(String.valueOf(string) + "}\r\n");
        }
        return stringBuffer.toString();
    }

    public final void initExprents() {
        IfExprent ifExprent = (IfExprent)this.first.getExprents().remove(this.first.getExprents().size() - 1);
        if (this.negated) {
            ifExprent = (IfExprent)ifExprent.copy();
            ifExprent.negateIf();
        }
        this.headexprent.set(0, ifExprent);
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

    public final void replaceStatement(Statement object, Statement object2) {
        super.replaceStatement((Statement)object, (Statement)object2);
        if (this.ifstat == object) {
            this.ifstat = object2;
        }
        if (this.elsestat == object) {
            this.elsestat = object2;
        }
        object = this.first.getSuccessorEdges(0x40000000);
        if (this.iftype == 0) {
            this.ifedge = (StatEdge)object.get(0);
            this.elseedge = null;
            return;
        }
        object2 = (StatEdge)object.get(0);
        object = (StatEdge)object.get(1);
        if (((StatEdge)object2).getDestination() == this.ifstat) {
            this.ifedge = object2;
            this.elseedge = object;
            return;
        }
        this.ifedge = object;
        this.elseedge = object2;
    }

    public final Statement getSimpleCopy() {
        IfStatement ifStatement = new IfStatement();
        new IfStatement().iftype = this.iftype;
        ifStatement.negated = this.negated;
        ifStatement.iffflag = this.iffflag;
        return ifStatement;
    }

    public final void initSimpleCopy() {
        this.first = (Statement)this.stats.get(0);
        List list = this.first.getSuccessorEdges(0x40000000);
        this.ifedge = (StatEdge)list.get(this.iftype == 0 || this.negated ? 0 : 1);
        if (this.stats.size() > 1) {
            this.ifstat = (Statement)this.stats.get(1);
        }
        if (this.iftype == IFTYPE_IFELSE) {
            this.elseedge = (StatEdge)list.get(this.negated ? 1 : 0);
            this.elsestat = (Statement)this.stats.get(2);
        }
    }

    public final Statement getElsestat() {
        return this.elsestat;
    }

    public final void setElsestat(Statement statement) {
        this.elsestat = statement;
    }

    public final Statement getIfstat() {
        return this.ifstat;
    }

    public final void setIfstat(Statement statement) {
        this.ifstat = statement;
    }

    public final boolean isNegated() {
        return this.negated;
    }

    public final void setNegated(boolean bl) {
        this.negated = bl;
    }

    public final List getHeadexprentList() {
        return this.headexprent;
    }

    public final IfExprent getHeadexprent() {
        return (IfExprent)this.headexprent.get(0);
    }

    public final void setElseEdge(StatEdge statEdge) {
        this.elseedge = statEdge;
    }

    public final void setIfEdge(StatEdge statEdge) {
        this.ifedge = statEdge;
    }

    public final StatEdge getIfEdge() {
        return this.ifedge;
    }

    public final StatEdge getElseEdge() {
        return this.elseedge;
    }
}

