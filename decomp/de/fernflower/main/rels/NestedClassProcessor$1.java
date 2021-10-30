/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.main.rels.NestedClassProcessor$VarFieldPair;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class NestedClassProcessor$1
implements DirectGraph$ExprentIterator {
    private final /* synthetic */ HashMap val$mapVarMasks;
    private final /* synthetic */ ClassesProcessor$ClassNode val$node;
    private final /* synthetic */ HashMap val$mapVarFieldPairs;
    private final /* synthetic */ MethodWrapper val$meth;

    NestedClassProcessor$1(HashMap hashMap, ClassesProcessor$ClassNode classesProcessor$ClassNode, HashMap hashMap2, MethodWrapper methodWrapper) {
        this.val$mapVarMasks = hashMap;
        this.val$node = classesProcessor$ClassNode;
        this.val$mapVarFieldPairs = hashMap2;
        this.val$meth = methodWrapper;
    }

    public final int processExprent(Exprent exprent) {
        Object object = exprent.getAllExprents(true);
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            exprent = (Exprent)object.next();
            if (exprent.type != 10 || (exprent = ((NewExprent)exprent).getConstructor()) == null || !this.val$mapVarMasks.containsKey(((InvocationExprent)exprent).getClassname())) continue;
            String string = ((InvocationExprent)exprent).getClassname();
            ClassesProcessor$ClassNode classesProcessor$ClassNode = this.val$node.getClassNode(string);
            if (classesProcessor$ClassNode.type == 1) continue;
            List list = (List)((HashMap)this.val$mapVarMasks.get(string)).get(((InvocationExprent)exprent).getStringDescriptor());
            if (!this.val$mapVarFieldPairs.containsKey(string)) {
                this.val$mapVarFieldPairs.put(string, new HashMap());
            }
            ArrayList<NestedClassProcessor$VarFieldPair> arrayList = new ArrayList<NestedClassProcessor$VarFieldPair>();
            int n = 0;
            while (n < list.size()) {
                Object object2 = (Exprent)((InvocationExprent)exprent).getLstParameters().get(n);
                NestedClassProcessor$VarFieldPair nestedClassProcessor$VarFieldPair = null;
                if (((Exprent)object2).type == 12 && list.get(n) != null) {
                    object2 = new VarVersionPaar((VarExprent)object2);
                    nestedClassProcessor$VarFieldPair = new NestedClassProcessor$VarFieldPair(((NestedClassProcessor$VarFieldPair)list.get((int)n)).keyfield, (VarVersionPaar)object2);
                }
                arrayList.add(nestedClassProcessor$VarFieldPair);
                ++n;
            }
            ArrayList<NestedClassProcessor$VarFieldPair> arrayList2 = (ArrayList<NestedClassProcessor$VarFieldPair>)((HashMap)this.val$mapVarFieldPairs.get(string)).get(((InvocationExprent)exprent).getStringDescriptor());
            if (arrayList2 == null) {
                arrayList2 = arrayList;
            } else {
                int n2 = 0;
                while (n2 < arrayList2.size()) {
                    if (!InterpreterUtil.equalObjects(arrayList2.get(n2), arrayList.get(n2))) {
                        arrayList2.set(n2, null);
                    }
                    ++n2;
                }
            }
            ((HashMap)this.val$mapVarFieldPairs.get(string)).put(((InvocationExprent)exprent).getStringDescriptor(), arrayList2);
            classesProcessor$ClassNode.enclosingMethod = InterpreterUtil.makeUniqueKey(this.val$meth.methodStruct.getName(), this.val$meth.methodStruct.getDescriptor());
        }
        return 0;
    }
}

