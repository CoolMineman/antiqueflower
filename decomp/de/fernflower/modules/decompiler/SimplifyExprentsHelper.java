/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.IfHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.ArrayExprent;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.MonitorExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.SSAConstructorSparseEx;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class SimplifyExprentsHelper {
    private boolean firstInvocation;

    public SimplifyExprentsHelper(boolean bl) {
        this.firstInvocation = bl;
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    public final boolean buildIff(Statement var1_1, HashSet var2_2, SSAConstructorSparseEx var3_3) {
        block10: {
            block7: {
                var4_4 = false;
                if (var1_1.getExprents() != null) break block7;
                do {
                    var5_5 = false;
                    for (Object var6_7 : var1_1.getStats()) {
                        block9: {
                            block8: {
                                var4_4 |= this.buildIff((Statement)var6_7, var2_2, var3_3);
                                var5_5 = IfHelper.mergeIfs((Statement)var6_7, var2_2);
                                if (var5_5) break;
                                v0 = var6_7;
                                var6_7 = var3_3;
                                var5_6 = v0;
                                if (v0.type != 2 || var5_6.getExprents() != null) ** GOTO lbl-1000
                                var5_6 = (IfStatement)var5_6;
                                if (var5_6.iftype != IfStatement.IFTYPE_IFELSE) ** GOTO lbl-1000
                                var8_9 = var5_6.getIfstat();
                                var9_10 = var5_6.getElsestat();
                                if (var8_9.getExprents() == null || var8_9.getExprents().size() != 1 || var9_10.getExprents() == null || var9_10.getExprents().size() != 1 || var8_9.getAllSuccessorEdges().size() != 1 || var9_10.getAllSuccessorEdges().size() != 1 || ((StatEdge)var8_9.getAllSuccessorEdges().get(0)).getDestination() != ((StatEdge)var9_10.getAllSuccessorEdges().get(0)).getDestination()) ** GOTO lbl-1000
                                var10_11 = (Exprent)var8_9.getExprents().get(0);
                                var9_10 = (Exprent)var9_10.getExprents().get(0);
                                if (var10_11.type != 2 || var9_10.type != 2) break block8;
                                var10_11 = (AssignmentExprent)var10_11;
                                var9_10 = (AssignmentExprent)var9_10;
                                if (var10_11.getLeft().type != 12 || var9_10.getLeft().type != 12) ** GOTO lbl-1000
                                var11_12 = (VarExprent)var10_11.getLeft();
                                var12_13 = (VarExprent)var9_10.getLeft();
                                if (var11_12.getIndex0() != var12_13.getIndex0() || !var11_12.isStack()) ** GOTO lbl-1000
                                var13_14 = false;
                                for (Object var6_7 : var6_7.getPhi().entrySet()) {
                                    if (((VarVersionPaar)var6_7.getKey()).var != var11_12.getIndex0() || !((FastSparseSetFactory$FastSparseSet)var6_7.getValue()).contains(var11_12.getVersion()) || !((FastSparseSetFactory$FastSparseSet)var6_7.getValue()).contains(var12_13.getVersion())) continue;
                                    var13_14 = true;
                                    break;
                                }
                                if (!var13_14) ** GOTO lbl-1000
                                var6_7 = new ArrayList<E>();
                                var6_7.addAll(var5_6.getFirst().getExprents());
                                var6_7.add(new AssignmentExprent((Exprent)var11_12, new FunctionExprent(36, Arrays.asList(new Exprent[]{var5_6.getHeadexprent().getCondition(), var10_11.getRight(), var9_10.getRight()}))));
                                var5_6.setExprents((List)var6_7);
                                if (var5_6.getAllSuccessorEdges().isEmpty()) {
                                    var14_15 = (StatEdge)var8_9.getAllSuccessorEdges().get(0);
                                    var6_7 = new StatEdge(var14_15.getType(), var5_6, var14_15.getDestination());
                                    var5_6.addSuccessor((StatEdge)var6_7);
                                    if (var14_15.closure != null) {
                                        var14_15.closure.addLabeledEdge((StatEdge)var6_7);
                                    }
                                }
                                SequenceHelper.destroyAndFlattenStatement(var5_6);
                                v1 = true;
                                break block9;
                            }
                            if (var10_11.type != 4 || var9_10.type != 4) ** GOTO lbl-1000
                            var10_11 = (ExitExprent)var10_11;
                            var9_10 = (ExitExprent)var9_10;
                            if (var10_11.getExittype() == var9_10.getExittype() && var10_11.getValue() != null && var9_10.getValue() != null && var10_11.getExittype() == 0 && (var10_11.getExittype() != 1 || var10_11.getValue().getExprType().equals(var9_10.getValue().getExprType()))) {
                                var11_12 = new ArrayList<E>();
                                var11_12.addAll(var5_6.getFirst().getExprents());
                                var11_12.add(new ExitExprent(var10_11.getExittype(), new FunctionExprent(36, Arrays.asList(new Exprent[]{var5_6.getHeadexprent().getCondition(), var10_11.getValue(), var9_10.getValue()})), var10_11.getRettype()));
                                var5_6.setExprents((List)var11_12);
                                var12_13 = (StatEdge)var8_9.getAllSuccessorEdges().get(0);
                                var5_6.addSuccessor(new StatEdge(4, var5_6, var12_13.getDestination(), var12_13.closure == var5_6 ? var5_6.getParent() : var12_13.closure));
                                SequenceHelper.destroyAndFlattenStatement(var5_6);
                                v1 = true;
                            } else lbl-1000:
                            // 8 sources

                            {
                                v1 = var5_5 = false;
                            }
                        }
                        if (v1) break;
                    }
                    var4_4 |= var5_5;
                } while (var5_5);
                break block10;
            }
            var4_4 = false | this.simplifyStackVarsExprents(var1_1.getExprents());
        }
        return var4_4;
    }

    /*
     * Unable to fully structure code
     */
    private boolean simplifyStackVarsExprents(List var1_1) {
        var2_2 = false;
        var3_3 = 0;
        while (var3_3 < var1_1.size()) {
            block40: {
                block39: {
                    var4_4 = (Exprent)var1_1.get(var3_3);
                    var5_5 = SimplifyExprentsHelper.isSimpleConstructorInvocation(var4_4);
                    if (var5_5 != null) {
                        var1_1.set(var3_3, var5_5);
                        var2_2 = true;
                        continue;
                    }
                    var6_6 = var4_4;
                    if (var6_6.type == 9 && (var7_9 = (MonitorExprent)var6_6).getMontype() == 1 && var7_9.getValue().type == 12 && ((VarExprent)var7_9.getValue()).isStack() == false) {
                        var1_1.remove(var3_3);
                        var2_2 = true;
                        continue;
                    }
                    var6_6 = var4_4;
                    if (var6_6.type != 2) ** GOTO lbl-1000
                    var7_9 = (AssignmentExprent)var6_6;
                    if (var7_9.getLeft().type != 12 || var7_9.getRight().type != 12) ** GOTO lbl-1000
                    var8_15 = (VarExprent)var7_9.getLeft();
                    var9_16 = (VarExprent)var7_9.getRight();
                    if (var8_15.getIndex0() == var9_16.getIndex0() && var8_15.isStack() && var9_16.isStack()) {
                        v0 = true;
                    } else lbl-1000:
                    // 3 sources

                    {
                        v0 = false;
                    }
                    if (v0) {
                        var1_1.remove(var3_3);
                        var2_2 = true;
                        continue;
                    }
                    if (var3_3 == var1_1.size() - 1) break;
                    var5_5 = (Exprent)var1_1.get(var3_3 + 1);
                    var7_10 = var3_3;
                    var6_6 = var1_1;
                    var8_15 = (Exprent)var6_6.get(var7_10);
                    if (var8_15.type == 2) {
                        var9_16 = (AssignmentExprent)var8_15;
                        if (var9_16.getLeft().type == 12 && var9_16.getRight().type == 10) {
                            var10_17 = (NewExprent)var9_16.getRight();
                            var11_18 = var10_17.getNewtype();
                            var8_15 = new VarVersionPaar((VarExprent)var9_16.getLeft());
                            if (var11_18.type == 8 && var11_18.arraydim == 0 && var10_17.getConstructor() == null) {
                                var12_19 = new HashSet<VarVersionPaar>();
                                var13_21 = var7_10 + 1;
                                while (var13_21 < var6_6.size()) {
                                    var14_23 = (Exprent)var6_6.get(var13_21);
                                    if (var14_23.type == 8 && (var15_24 = (InvocationExprent)var14_23).getFunctype() == 2 && var15_24.getInstance().type == 12 && var9_16.getLeft().equals(var15_24.getInstance())) {
                                        var16_26 = var14_23.getAllVariables();
                                        var16_26.remove(var8_15);
                                        var16_26.retainAll(var12_19);
                                        if (var16_26.isEmpty()) {
                                            var10_17.setConstructor((InvocationExprent)var15_24);
                                            var15_24.setInstance(null);
                                            if (!var12_19.isEmpty()) {
                                                var6_6.add(var7_10 + 1, var9_16.copy());
                                                var6_6.remove(var13_21 + 1);
                                            } else {
                                                var6_6.set(var13_21, var9_16.copy());
                                            }
                                            v1 = true;
                                            break block39;
                                        }
                                    }
                                    var15_25 = false;
                                    if (var14_23.type == 2) {
                                        var16_26 = (AssignmentExprent)var14_23;
                                        if (var16_26.getLeft().type == 12 && var16_26.getRight().type == 12) {
                                            var12_19.add(new VarVersionPaar((VarExprent)var16_26.getLeft()));
                                            var15_25 = true;
                                        }
                                    }
                                    if (!var15_25) {
                                        var16_26 = var14_23.getAllVariables();
                                        if (var16_26.contains(var8_15)) break;
                                        var12_19.addAll((Collection<VarVersionPaar>)var16_26);
                                    }
                                    ++var13_21;
                                }
                            }
                        }
                    }
                    v1 = false;
                }
                if (v1) {
                    var1_1.remove(var3_3);
                    var2_2 = true;
                    continue;
                }
                if (DecompilerContext.getOption("rgn") && SimplifyExprentsHelper.isQualifiedNewGetClass(var4_4, var5_5)) {
                    var1_1.remove(var3_3);
                    var2_2 = true;
                    continue;
                }
                var6_7 = SimplifyExprentsHelper.isArrayInitializer(var1_1, var3_3);
                if (var6_7 > 0) {
                    var7_10 = 0;
                    while (var7_10 < var6_7) {
                        var1_1.remove(var3_3 + 1);
                        ++var7_10;
                    }
                    var2_2 = true;
                    continue;
                }
                var7_11 = var5_5;
                var6_8 = var4_4;
                if (var6_8.type != 2) ** GOTO lbl-1000
                var8_15 = (AssignmentExprent)var6_8;
                if (var8_15.getRight().type != 10 || var8_15.getLeft().type != 12 || (var9_16 = (NewExprent)var8_15.getRight()).getLstArrayElements().isEmpty()) ** GOTO lbl-1000
                var10_17 = (VarExprent)var8_15.getLeft();
                if (var7_11.type != 2) ** GOTO lbl-1000
                var11_18 = (AssignmentExprent)var7_11;
                if (var11_18.getLeft().type != 1) ** GOTO lbl-1000
                var8_15 = (ArrayExprent)var11_18.getLeft();
                if (var8_15.getArray().type != 12 || !var10_17.equals(var8_15.getArray()) || var8_15.getIndex().type != 3 || (var12_20 = ((ConstExprent)var8_15.getIndex()).getIntValue()) >= var9_16.getLstArrayElements().size()) ** GOTO lbl-1000
                var13_22 = (Exprent)var9_16.getLstArrayElements().get(var12_20);
                if (var13_22.type != 3) ** GOTO lbl-1000
                var14_23 = (ConstExprent)var13_22;
                var15_24 = var9_16.getNewtype().copy();
                var15_24.decArrayDim();
                var16_26 = ExprProcessor.getDefaultArrayValue((VarType)var15_24);
                if (var14_23.equals(var16_26) && !(var6_8 = var11_18.getRight()).containsExprent(var10_17)) {
                    var9_16.getLstArrayElements().set(var12_20, var6_8);
                    if (var6_8.type == 10) {
                        var6_8 = (NewExprent)var6_8;
                        var7_12 = false;
                        if (var9_16.getNewtype().arraydim > 1 && !var6_8.getLstArrayElements().isEmpty()) {
                            var6_8.setDirectArrayInit();
                        }
                    }
                    v2 = true;
                } else lbl-1000:
                // 7 sources

                {
                    v2 = false;
                }
                if (v2) {
                    var1_1.remove(var3_3 + 1);
                    var2_2 = true;
                    continue;
                }
                var6_8 = var4_4;
                if (var6_8.type != 2) ** GOTO lbl-1000
                var7_13 = (AssignmentExprent)var6_8;
                if (var7_13.getRight().type != 6 || (var8_15 = (FunctionExprent)var7_13.getRight()).getFunctype() != 0 && var8_15.getFunctype() != 1) ** GOTO lbl-1000
                var9_16 = (Exprent)var8_15.getLstOperands().get(0);
                var10_17 = (Exprent)var8_15.getLstOperands().get(1);
                if (var10_17.type != 3 && var9_16.type == 3 && var8_15.getFunctype() == 0) {
                    var9_16 = var10_17;
                    var10_17 = (Exprent)var8_15.getLstOperands().get(0);
                }
                if (var10_17.type != 3 || !((ConstExprent)var10_17).hasValueOne()) ** GOTO lbl-1000
                var11_18 = var7_13.getLeft();
                if (var11_18.type != 12 && var11_18.equals(var9_16)) {
                    var8_15 = new FunctionExprent(var8_15.getFunctype() == 0 ? 35 : 33, Arrays.asList(new Exprent[]{var9_16}));
                    var8_15.setImplicitType(VarType.VARTYPE_INT);
                    v3 = var8_15;
                } else lbl-1000:
                // 4 sources

                {
                    v3 = var7_14 = null;
                }
                if (v3 != null) {
                    var1_1.set(var3_3, var7_14);
                    var2_2 = true;
                    continue;
                }
                var7_14 = var5_5;
                var6_8 = var4_4;
                if (var6_8.type != 2 || var7_14.type != 6) ** GOTO lbl-1000
                var8_15 = (AssignmentExprent)var6_8;
                var9_16 = (FunctionExprent)var7_14;
                if ((var9_16.getFunctype() == 33 || var9_16.getFunctype() == 35) && ((Exprent)var9_16.getLstOperands().get(0)).equals(var8_15.getRight())) {
                    if (var9_16.getFunctype() == 33) {
                        var9_16.setFunctype(32);
                    } else {
                        var9_16.setFunctype(34);
                    }
                    var8_15.setRight(var9_16);
                    v4 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v4 = false;
                }
                if (v4) {
                    var1_1.remove(var3_3 + 1);
                    var2_2 = true;
                    continue;
                }
                if (SimplifyExprentsHelper.isStackAssignement(var4_4, var5_5)) {
                    var1_1.remove(var3_3 + 1);
                    var2_2 = true;
                    continue;
                }
                if (this.firstInvocation) break block40;
                var7_14 = var5_5;
                var6_8 = var4_4;
                if (var6_8.type != 2 || var7_14.type != 2) ** GOTO lbl-1000
                var8_15 = (AssignmentExprent)var6_8;
                var9_16 = (AssignmentExprent)var7_14;
                if (var8_15.getLeft().type == 12 && var9_16.getRight().type == 12 && var8_15.getLeft().equals(var9_16.getRight()) && ((VarExprent)var8_15.getLeft()).isStack() && (var9_16.getLeft().type != 12 || !((VarExprent)var9_16.getLeft()).isStack())) {
                    var8_15.setRight(new AssignmentExprent(var9_16.getLeft(), var8_15.getRight()));
                    v5 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = false;
                }
                if (v5) {
                    var1_1.remove(var3_3 + 1);
                    var2_2 = true;
                    continue;
                }
            }
            ++var3_3;
        }
        return var2_2;
    }

    private static int isArrayInitializer(List list, int n) {
        Object object = (Exprent)list.get(n);
        if (((Exprent)object).type == 2) {
            object = (AssignmentExprent)object;
            if (object.getRight().type == 10 && object.getLeft().type == 12) {
                NewExprent newExprent = (NewExprent)((AssignmentExprent)object).getRight();
                if (newExprent.getExprType().arraydim > 0 && newExprent.getLstDims().size() == 1 && newExprent.getLstArrayElements().isEmpty() && ((Exprent)newExprent.getLstDims().get((int)0)).type == 3) {
                    Object n4;
                    Object object2;
                    int exprent = (Integer)((ConstExprent)newExprent.getLstDims().get(0)).getValue();
                    if (exprent == 0) {
                        return 0;
                    }
                    object = (VarExprent)((AssignmentExprent)object).getLeft();
                    HashMap<Integer, Exprent> hashMap = new HashMap<Integer, Exprent>();
                    int n2 = 1;
                    while (n + n2 < list.size() && n2 <= exprent) {
                        boolean d = false;
                        Exprent exprent2 = (Exprent)list.get(n + n2);
                        if (exprent2.type == 2) {
                            object2 = (AssignmentExprent)exprent2;
                            if (object2.getLeft().type == 1) {
                                int n3;
                                n4 = (ArrayExprent)((AssignmentExprent)object2).getLeft();
                                if (n4.getArray().type == 12 && ((VarExprent)object).equals(((ArrayExprent)n4).getArray()) && n4.getIndex().type == 3 && (n3 = ((ConstExprent)((ArrayExprent)n4).getIndex()).getIntValue()) < exprent && !hashMap.containsKey(n3) && !((AssignmentExprent)object2).getRight().containsExprent((Exprent)object)) {
                                    hashMap.put(n3, ((AssignmentExprent)object2).getRight());
                                    d = true;
                                }
                            }
                        }
                        if (!d) break;
                        ++n2;
                    }
                    double d = (double)hashMap.size() / (double)exprent;
                    if (((VarExprent)object).isStack() && d > 0.0 || exprent <= 7 && d >= 0.3 || exprent > 7 && d >= 0.7) {
                        object2 = new ArrayList();
                        n4 = newExprent.getNewtype().copy();
                        ((VarType)n4).decArrayDim();
                        n4 = ExprProcessor.getDefaultArrayValue((VarType)n4);
                        int n5 = 0;
                        while (n5 < exprent) {
                            object2.add(((ConstExprent)n4).copy());
                            ++n5;
                        }
                        n5 = newExprent.getNewtype().arraydim;
                        for (Map.Entry object4 : hashMap.entrySet()) {
                            Exprent exprent3 = (Exprent)object4.getValue();
                            object2.set((Integer)object4.getKey(), exprent3);
                            if (exprent3.type != 10) continue;
                            NewExprent newExprent2 = (NewExprent)exprent3;
                            if (n5 <= 1 || newExprent2.getLstArrayElements().isEmpty()) continue;
                            newExprent2.setDirectArrayInit();
                        }
                        newExprent.setLstArrayElements((List)object2);
                        return hashMap.size();
                    }
                }
            }
        }
        return 0;
    }

    private static boolean isStackAssignement(Exprent exprent, Exprent exprent2) {
        if (exprent.type == 2 && exprent2.type == 2) {
            exprent = (AssignmentExprent)exprent;
            exprent2 = (AssignmentExprent)exprent2;
            while (true) {
                if (((AssignmentExprent)exprent).getRight().equals(((AssignmentExprent)exprent2).getRight()) && exprent.getLeft().type == 12 && ((VarExprent)((AssignmentExprent)exprent).getLeft()).isStack() && (exprent2.getLeft().type != 12 || !((VarExprent)((AssignmentExprent)exprent2).getLeft()).isStack()) && !((AssignmentExprent)exprent2).getLeft().containsExprent(((AssignmentExprent)exprent).getLeft())) {
                    ((AssignmentExprent)exprent).setRight(exprent2);
                    return true;
                }
                if (exprent.getRight().type != 2) break;
                exprent = (AssignmentExprent)((AssignmentExprent)exprent).getRight();
            }
        }
        return false;
    }

    private static boolean isQualifiedNewGetClass(Exprent exprent, Exprent object) {
        if (exprent.type == 8 && !((InvocationExprent)(exprent = (InvocationExprent)exprent)).isStatic() && exprent.getInstance().type == 12 && ((InvocationExprent)exprent).getName().equals("getClass") && ((InvocationExprent)exprent).getStringDescriptor().equals("()Ljava/lang/Class;")) {
            Object object2 = ((Exprent)object).getAllExprents();
            object2.add(object);
            object2 = object2.iterator();
            while (object2.hasNext()) {
                object = (Exprent)object2.next();
                if (((Exprent)object).type != 10 || ((NewExprent)(object = (NewExprent)object)).getConstructor() == null || ((NewExprent)object).getConstructor().getLstParameters().isEmpty() || !((Exprent)((NewExprent)object).getConstructor().getLstParameters().get(0)).equals(((InvocationExprent)exprent).getInstance())) continue;
                object = object.getNewtype().value;
                object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(object);
                if (object == null || ((ClassesProcessor$ClassNode)object).type == 0) continue;
                return true;
            }
        }
        return false;
    }

    private static Exprent isSimpleConstructorInvocation(Exprent exprent) {
        Exprent exprent22 = null;
        for (Exprent exprent22 : exprent.getAllExprents()) {
            Exprent exprent3 = SimplifyExprentsHelper.isSimpleConstructorInvocation(exprent22);
            if (exprent3 == null) continue;
            exprent.replaceExprent(exprent22, exprent3);
        }
        if (exprent.type == 8 && ((InvocationExprent)(exprent22 = (InvocationExprent)exprent)).getFunctype() == 2 && exprent22.getInstance().type == 10) {
            NewExprent newExprent = (NewExprent)((InvocationExprent)exprent22).getInstance();
            newExprent.setConstructor((InvocationExprent)exprent22);
            ((InvocationExprent)exprent22).setInstance(null);
            return newExprent;
        }
        return null;
    }
}

