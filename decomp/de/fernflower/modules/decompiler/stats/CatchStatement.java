/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class CatchStatement
extends Statement {
    private List exctstrings = new ArrayList();
    private List vars = new ArrayList();

    private CatchStatement() {
        this.type = 7;
    }

    private CatchStatement(Statement object2, Statement statement, HashSet hashSet) {
        this();
        this.first = object2;
        this.stats.addWithKey(this.first, this.first.id);
        for (Object object2 : ((Statement)object2).getSuccessorEdges(2)) {
            Statement statement2 = ((StatEdge)object2).getDestination();
            if (!hashSet.contains(statement2)) continue;
            this.stats.addWithKey(statement2, statement2.id);
            this.exctstrings.add(((StatEdge)object2).getException());
            this.vars.add(new VarExprent(DecompilerContext.getCountercontainer().getCounterAndIncrement(2), new VarType(8, 0, ((StatEdge)object2).getException()), (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR")));
        }
        if (statement != null) {
            this.post = statement;
        }
    }

    public static Statement isHead(Statement statement) {
        if (statement.getLastBasicType() != 2) {
            return null;
        }
        HashSet hashSet = DecHelper.getUniquePredExceptions(statement);
        if (!hashSet.isEmpty()) {
            Object object;
            int n = 0;
            Object object2 = null;
            Object object32 = statement.getSuccessorEdges(0x40000000);
            if (!object32.isEmpty() && ((StatEdge)object32.get(0)).getType() == 1) {
                object2 = ((StatEdge)object32.get(0)).getDestination();
                n = 2;
            }
            for (Object object32 : statement.getSuccessorEdges(2)) {
                object = ((StatEdge)object32).getDestination();
                boolean bl = true;
                if (((StatEdge)object32).getException() != null && hashSet.contains(object) && ((Statement)object).getLastBasicType() == 2) {
                    object32 = ((Statement)object).getSuccessorEdges(0x40000000);
                    if (!object32.isEmpty() && ((StatEdge)object32.get(0)).getType() == 1) {
                        object32 = ((StatEdge)object32.get(0)).getDestination();
                        if (object2 == null) {
                            object2 = object32;
                        } else if (object2 != object32) {
                            bl = false;
                        }
                        if (bl) {
                            ++n;
                        }
                    }
                } else {
                    bl = false;
                }
                if (bl) continue;
                hashSet.remove(object);
            }
            if (n != 1 && !hashSet.isEmpty()) {
                object32 = new ArrayList<Statement>();
                object32.add(statement);
                object32.addAll(hashSet);
                object = object32.iterator();
                while (object.hasNext()) {
                    if (!((Statement)object.next()).isMonitorEnter()) continue;
                    return null;
                }
                if (DecHelper.checkStatementExceptions(object32)) {
                    return new CatchStatement(statement, (Statement)object2, hashSet);
                }
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
        stringBuffer.append(String.valueOf(string) + "try {\r\n");
        stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
        stringBuffer.append(String.valueOf(string) + "}");
        int n2 = 1;
        while (n2 < this.stats.size()) {
            stringBuffer.append(" catch (" + ((VarExprent)this.vars.get(n2 - 1)).toJava(n) + ") {\r\n" + ExprProcessor.jmpWrapper((Statement)this.stats.get(n2), n + 1, true) + string + "}");
            ++n2;
        }
        stringBuffer.append("\r\n");
        return stringBuffer.toString();
    }

    public final Statement getSimpleCopy() {
        CatchStatement catchStatement = new CatchStatement();
        for (Object object : ((CatchStatement)object).exctstrings) {
            catchStatement.exctstrings.add(object);
            catchStatement.vars.add(new VarExprent(DecompilerContext.getCountercontainer().getCounterAndIncrement(2), new VarType(8, 0, (String)object), (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR")));
        }
        return catchStatement;
    }

    public final List getVars() {
        return this.vars;
    }
}

