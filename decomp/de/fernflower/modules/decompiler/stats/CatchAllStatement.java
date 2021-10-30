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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class CatchAllStatement
extends Statement {
    private Statement handler;
    private boolean isFinally;
    private VarExprent monitor;
    private List vars = new ArrayList();

    private CatchAllStatement() {
        this.type = 12;
    }

    private CatchAllStatement(Statement object, Statement statement) {
        this();
        this.first = object;
        this.stats.addWithKey(object, ((Statement)object).id);
        this.handler = statement;
        this.stats.addWithKey(statement, statement.id);
        object = ((Statement)object).getSuccessorEdges(0x40000000);
        if (!object.isEmpty() && ((StatEdge)(object = (StatEdge)object.get(0))).getType() == 1) {
            this.post = ((StatEdge)object).getDestination();
        }
        this.vars.add(new VarExprent(DecompilerContext.getCountercontainer().getCounterAndIncrement(2), new VarType(8, 0, "java/lang/Throwable"), (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR")));
    }

    public static Statement isHead(Statement statement) {
        if (statement.getLastBasicType() != 2) {
            return null;
        }
        HashSet hashSet = DecHelper.getUniquePredExceptions(statement);
        if (hashSet.size() != 1) {
            return null;
        }
        for (Object object : statement.getSuccessorEdges(2)) {
            Statement statement2 = ((StatEdge)object).getDestination();
            if (((StatEdge)object).getException() != null || !hashSet.contains(statement2) || statement2.getLastBasicType() != 2 || !(object = statement2.getSuccessorEdges(0x40000000)).isEmpty() && ((StatEdge)object.get(0)).getType() == 1) continue;
            if (statement.isMonitorEnter() || statement2.isMonitorEnter()) {
                return null;
            }
            if (!DecHelper.checkStatementExceptions(Arrays.asList(statement, statement2))) continue;
            return new CatchAllStatement(statement, statement2);
        }
        return null;
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        String string2 = null;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ExprProcessor.listToJava(this.varDefinitions, n));
        boolean bl = this.isLabeled();
        if (bl) {
            stringBuffer.append(String.valueOf(string) + "label" + this.id + ":\r\n");
        }
        List list = this.first.getSuccessorEdges(0x40000000);
        if (this.first.type == 7 && this.first.varDefinitions.isEmpty() && this.isFinally && !bl && !this.first.isLabeled() && (list.isEmpty() || !((StatEdge)list.get((int)0)).explicit)) {
            String string3 = ExprProcessor.jmpWrapper(this.first, n, true);
            string3 = string3.substring(0, string3.length() - 2);
            stringBuffer.append(string3);
        } else {
            stringBuffer.append(String.valueOf(string) + "try {\r\n");
            stringBuffer.append(ExprProcessor.jmpWrapper(this.first, n + 1, true));
            stringBuffer.append(String.valueOf(string) + "}");
        }
        stringBuffer.append(String.valueOf(this.isFinally ? " finally" : " catch (" + ((VarExprent)this.vars.get(0)).toJava(n) + ")") + " {\r\n");
        if (this.monitor != null) {
            string2 = InterpreterUtil.getIndentString(n + 1);
            stringBuffer.append(String.valueOf(string2) + "if(" + this.monitor.toJava(n) + ") {\r\n");
        }
        stringBuffer.append(ExprProcessor.jmpWrapper(this.handler, n + 1 + (this.monitor != null ? 1 : 0), true));
        if (this.monitor != null) {
            stringBuffer.append(String.valueOf(string2) + "}\r\n");
        }
        stringBuffer.append(String.valueOf(string) + "}\r\n");
        return stringBuffer.toString();
    }

    public final void replaceStatement(Statement statement, Statement statement2) {
        if (this.handler == statement) {
            this.handler = statement2;
        }
        super.replaceStatement(statement, statement2);
    }

    public final Statement getSimpleCopy() {
        CatchAllStatement catchAllStatement = new CatchAllStatement();
        new CatchAllStatement().isFinally = this.isFinally;
        if (this.monitor != null) {
            catchAllStatement.monitor = new VarExprent(DecompilerContext.getCountercontainer().getCounterAndIncrement(2), VarType.VARTYPE_INT, (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR"));
        }
        if (!this.vars.isEmpty()) {
            this.vars.add(new VarExprent(DecompilerContext.getCountercontainer().getCounterAndIncrement(2), new VarType(8, 0, "java/lang/Throwable"), (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR")));
        }
        return catchAllStatement;
    }

    public final void initSimpleCopy() {
        this.first = (Statement)this.stats.get(0);
        this.handler = (Statement)this.stats.get(1);
    }

    public final Statement getHandler() {
        return this.handler;
    }

    public final boolean isFinally() {
        return this.isFinally;
    }

    public final void setFinally() {
        this.isFinally = true;
    }

    public final VarExprent getMonitor() {
        return this.monitor;
    }

    public final void setMonitor(VarExprent varExprent) {
        this.monitor = varExprent;
    }

    public final List getVars() {
        return this.vars;
    }
}

