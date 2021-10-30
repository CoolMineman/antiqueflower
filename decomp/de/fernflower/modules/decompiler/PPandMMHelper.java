/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.struct.gen.VarType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public final class PPandMMHelper {
    private boolean exprentReplaced;

    public final boolean findPPandMM(RootStatement hashSet) {
        LinkedList<DirectNode> linkedList = null;
        hashSet = new FlattenStatementsHelper().buildDirectGraph((RootStatement)((Object)hashSet));
        linkedList = new LinkedList<DirectNode>();
        linkedList.add(((DirectGraph)hashSet).first);
        hashSet = new HashSet<DirectNode>();
        boolean bl = false;
        while (!linkedList.isEmpty()) {
            DirectNode directNode = (DirectNode)linkedList.removeFirst();
            if (hashSet.contains(directNode)) continue;
            hashSet.add(directNode);
            bl |= this.processExprentList(directNode.exprents);
            linkedList.addAll(directNode.succs);
        }
        return bl;
    }

    private boolean processExprentList(List list) {
        boolean bl = false;
        int n = 0;
        while (n < list.size()) {
            Exprent exprent = (Exprent)list.get(n);
            this.exprentReplaced = false;
            if ((exprent = this.processExprentRecursive(exprent)) != null) {
                list.set(n, exprent);
                bl = true;
                --n;
            }
            bl |= this.exprentReplaced;
            ++n;
        }
        return bl;
    }

    private Exprent processExprentRecursive(Exprent exprent) {
        Object object;
        boolean bl = true;
        block0: while (bl) {
            bl = false;
            for (Object object2 : exprent.getAllExprents()) {
                object = this.processExprentRecursive((Exprent)object2);
                if (object == null) continue;
                exprent.replaceExprent((Exprent)object2, (Exprent)object);
                bl = true;
                this.exprentReplaced = true;
                continue block0;
            }
        }
        if (exprent.type == 2) {
            Object object2;
            object2 = (AssignmentExprent)exprent;
            if (object2.getRight().type == 6) {
                Object object3 = (FunctionExprent)((AssignmentExprent)object2).getRight();
                object = null;
                if (((FunctionExprent)object3).getFunctype() >= 14 && ((FunctionExprent)object3).getFunctype() <= 28) {
                    object = ((FunctionExprent)object3).getSimpleCastType();
                    if (((Exprent)object3.getLstOperands().get((int)0)).type == 6) {
                        object3 = (FunctionExprent)((FunctionExprent)object3).getLstOperands().get(0);
                    } else {
                        return null;
                    }
                }
                if (((FunctionExprent)object3).getFunctype() == 0 || ((FunctionExprent)object3).getFunctype() == 1) {
                    exprent = (Exprent)((FunctionExprent)object3).getLstOperands().get(0);
                    Exprent exprent2 = (Exprent)((FunctionExprent)object3).getLstOperands().get(1);
                    if (exprent2.type != 3 && exprent.type == 3 && ((FunctionExprent)object3).getFunctype() == 0) {
                        exprent = exprent2;
                        exprent2 = (Exprent)((FunctionExprent)object3).getLstOperands().get(0);
                    }
                    if (exprent2.type == 3 && ((ConstExprent)exprent2).hasValueOne()) {
                        exprent2 = ((AssignmentExprent)object2).getLeft();
                        object2 = exprent.getExprType();
                        if (exprent2.equals(exprent) && (object == null || ((VarType)object).equals(object2))) {
                            exprent = new FunctionExprent(((FunctionExprent)object3).getFunctype() == 0 ? 35 : 33, Arrays.asList(exprent));
                            ((FunctionExprent)exprent).setImplicitType((VarType)object2);
                            this.exprentReplaced = true;
                            return exprent;
                        }
                    }
                }
            }
        }
        return null;
    }
}

