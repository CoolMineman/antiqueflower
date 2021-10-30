/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.SecondaryFunctionsHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.AssertExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.struct.StructField;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class AssertProcessor {
    private static final VarType CLASS_ASSERTION_ERROR = new VarType(8, 0, "java/lang/AssertionError");

    public static void findAssertionField(ClassWrapper classWrapper) {
        Object object;
        boolean bl;
        Object object2;
        block4: {
            object2 = classWrapper;
            bl = DecompilerContext.getOption("nns");
            for (Object object3 : ((ClassWrapper)object2).getClassStruct().getFields()) {
                boolean bl2;
                Object object4 = InterpreterUtil.makeUniqueKey(((StructField)object3).getName(), ((StructField)object3).getDescriptor());
                if (!((ClassWrapper)object2).getStaticFieldInitializers().containsKey(object4)) continue;
                int n = ((StructField)object3).access_flags;
                boolean bl3 = bl2 = (n & 0x1000) != 0 || ((StructField)object3).getAttributes().containsKey("Synthetic");
                if ((n & 8) == 0 || (n & 0x10) == 0 || !bl2 && !bl) continue;
                FieldDescriptor fieldDescriptor = FieldDescriptor.parseDescriptor(((StructField)object3).getDescriptor());
                if (!VarType.VARTYPE_BOOLEAN.equals(fieldDescriptor.type)) continue;
                object4 = (Exprent)((ClassWrapper)object2).getStaticFieldInitializers().getWithKey(object4);
                if (((Exprent)object4).type != 6 || ((FunctionExprent)(object4 = (FunctionExprent)object4)).getFunctype() != 12 || ((Exprent)object4.getLstOperands().get((int)0)).type != 8 || ((InvocationExprent)(object4 = (InvocationExprent)((FunctionExprent)object4).getLstOperands().get(0))).getInstance() == null || object4.getInstance().type != 3 || !"desiredAssertionStatus".equals(((InvocationExprent)object4).getName()) || !"java/lang/Class".equals(((InvocationExprent)object4).getClassname()) || !((InvocationExprent)object4).getLstParameters().isEmpty() || !VarType.VARTYPE_CLASS.equals(((ConstExprent)(object4 = (ConstExprent)((InvocationExprent)object4).getInstance())).getConsttype()) || !object2.getClassStruct().qualifiedName.equals(((ConstExprent)object4).getValue())) continue;
                object = object3;
                break block4;
            }
            object = object2 = null;
        }
        if (object != null) {
            object2 = InterpreterUtil.makeUniqueKey(((StructField)object2).getName(), ((StructField)object2).getDescriptor());
            bl = false;
            Iterator iterator = classWrapper.getMethods().iterator();
            while (iterator.hasNext()) {
                Object object3;
                object3 = null;
                object3 = ((MethodWrapper)iterator.next()).root;
                if (object3 == null) continue;
                bl |= AssertProcessor.replaceAssertion((Statement)object3, classWrapper.getClassStruct().qualifiedName, (String)object2);
            }
            if (bl) {
                classWrapper.getHideMembers().add(object2);
            }
        }
    }

    private static boolean replaceAssertion(Statement statement, String objectArray, String string) {
        boolean bl = false;
        for (Statement statement2 : statement.getStats()) {
            bl |= AssertProcessor.replaceAssertion(statement2, (String)objectArray, string);
        }
        boolean bl2 = true;
        while (bl2) {
            bl2 = false;
            for (Object object : statement.getStats()) {
                boolean bl3;
                Object object2;
                if (((Statement)object).type != 2) continue;
                Object object3 = string;
                Object object4 = objectArray;
                IfStatement ifStatement = (IfStatement)object;
                object = statement;
                Object object5 = null;
                object5 = ifStatement.getIfstat();
                if (object5 == null || ((Statement)object5).getExprents() == null || ((Statement)object5).getExprents().size() != 1) {
                    object2 = null;
                } else {
                    object5 = (Exprent)((Statement)object5).getExprents().get(0);
                    object2 = object5 = ((Exprent)object5).type == 4 && ((ExitExprent)(object5 = (ExitExprent)object5)).getExittype() == 1 && object5.getValue().type == 10 && CLASS_ASSERTION_ERROR.equals(((NewExprent)(object5 = (NewExprent)((ExitExprent)object5).getValue())).getNewtype()) && ((NewExprent)object5).getConstructor() != null ? ((NewExprent)object5).getConstructor() : null;
                }
                if (object2 == null) {
                    bl3 = false;
                } else {
                    object4 = AssertProcessor.getAssertionExprent(ifStatement.getHeadexprent().getCondition().copy(), (String)object4, (String)object3);
                    if (!((Boolean)object4[1]).booleanValue()) {
                        bl3 = false;
                    } else {
                        object3 = new ArrayList();
                        FunctionExprent functionExprent = null;
                        Exprent exprent = null;
                        if (object4[0] != null) {
                            functionExprent = new FunctionExprent(12, Arrays.asList((Exprent)object4[0]));
                            exprent = SecondaryFunctionsHelper.propagateBoolNot(functionExprent);
                        }
                        object3.add(exprent == null ? functionExprent : exprent);
                        if (!((InvocationExprent)object5).getLstParameters().isEmpty()) {
                            object3.add((Exprent)((InvocationExprent)object5).getLstParameters().get(0));
                        }
                        object4 = new AssertExprent((List)object3);
                        object3 = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
                        ((Statement)object3).setExprents(Arrays.asList(object4));
                        object4 = ifStatement.getFirst();
                        if (ifStatement.iftype == IfStatement.IFTYPE_IFELSE || ((Statement)object4).getExprents() != null && !((Statement)object4).getExprents().isEmpty()) {
                            ((Statement)object4).removeSuccessor(ifStatement.getIfEdge());
                            ((Statement)object4).removeSuccessor(ifStatement.getElseEdge());
                            object5 = new ArrayList<Object>();
                            if (((Statement)object4).getExprents() != null && !((Statement)object4).getExprents().isEmpty()) {
                                object5.add(object4);
                            }
                            object5.add(object3);
                            if (ifStatement.iftype == IfStatement.IFTYPE_IFELSE) {
                                object5.add(ifStatement.getElsestat());
                            }
                            object4 = new SequenceStatement((List)object5);
                            ((Statement)object4).setAllParent();
                            int n = 0;
                            while (n < ((Statement)object4).getStats().size() - 1) {
                                ((Statement)((Statement)object4).getStats().get(n)).addSuccessor(new StatEdge(1, (Statement)((Statement)object4).getStats().get(n), (Statement)((Statement)object4).getStats().get(n + 1)));
                                ++n;
                            }
                            if (ifStatement.iftype == IfStatement.IFTYPE_IFELSE) {
                                Object object6 = null;
                                object6 = ifStatement.getElsestat().getAllSuccessorEdges();
                                if (!object6.isEmpty()) {
                                    object6 = (StatEdge)object6.get(0);
                                    if (((StatEdge)object6).closure == ifStatement) {
                                        ((Statement)object4).addLabeledEdge((StatEdge)object6);
                                    }
                                }
                            }
                            object3 = object4;
                        }
                        ((Statement)object3).getVarDefinitions().addAll(ifStatement.getVarDefinitions());
                        ((Statement)object).replaceStatement(ifStatement, (Statement)object3);
                        bl3 = true;
                    }
                }
                if (!bl3) continue;
                bl2 = true;
                break;
            }
            bl |= bl2;
        }
        return bl;
    }

    private static Object[] getAssertionExprent(Exprent exprent, String string, String string2) {
        if (exprent.type == 6) {
            FunctionExprent functionExprent = (FunctionExprent)exprent;
            if (functionExprent.getFunctype() == 48) {
                Exprent exprent2;
                int n = 0;
                while (n < 2) {
                    exprent2 = null;
                    if (AssertProcessor.isAssertionField((Exprent)functionExprent.getLstOperands().get(n), string, string2)) {
                        return new Object[]{functionExprent.getLstOperands().get(1 - n), true};
                    }
                    ++n;
                }
                n = 0;
                while (n < 2) {
                    exprent2 = (Exprent)functionExprent.getLstOperands().get(n);
                    Object[] objectArray = AssertProcessor.getAssertionExprent(exprent2, string, string2);
                    if (((Boolean)objectArray[1]).booleanValue()) {
                        if (exprent2 != objectArray[0]) {
                            functionExprent.getLstOperands().set(n, (Exprent)objectArray[0]);
                        }
                        return new Object[]{functionExprent, true};
                    }
                    ++n;
                }
            } else if (AssertProcessor.isAssertionField(functionExprent, string, string2)) {
                Object[] objectArray = new Object[2];
                objectArray[1] = true;
                return objectArray;
            }
        }
        return new Object[]{exprent, false};
    }

    private static boolean isAssertionField(Exprent exprent, String string, String string2) {
        return exprent.type == 6 && ((FunctionExprent)(exprent = (FunctionExprent)exprent)).getFunctype() == 12 && ((Exprent)exprent.getLstOperands().get((int)0)).type == 5 && string.equals(((FieldExprent)(exprent = (FieldExprent)((FunctionExprent)exprent).getLstOperands().get(0))).getClassname()) && string2.equals(InterpreterUtil.makeUniqueKey(((FieldExprent)exprent).getName(), ((FieldExprent)exprent).getDescriptor()));
    }
}

