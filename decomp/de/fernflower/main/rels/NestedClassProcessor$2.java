/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.util.InterpreterUtil;
import java.util.HashMap;

final class NestedClassProcessor$2
implements DirectGraph$ExprentIterator {
    private final /* synthetic */ ClassesProcessor$ClassNode val$child;
    private final /* synthetic */ HashMap val$mapFieldsToNewVars;
    private final /* synthetic */ MethodWrapper val$meth;
    private final /* synthetic */ HashMap val$mapParamsToNewVars;

    NestedClassProcessor$2(ClassesProcessor$ClassNode classesProcessor$ClassNode, HashMap hashMap, MethodWrapper methodWrapper, HashMap hashMap2) {
        this.val$child = classesProcessor$ClassNode;
        this.val$mapFieldsToNewVars = hashMap;
        this.val$meth = methodWrapper;
        this.val$mapParamsToNewVars = hashMap2;
    }

    public final int processExprent(Exprent exprent) {
        Exprent exprent2;
        if (exprent.type == 2) {
            exprent2 = (AssignmentExprent)exprent;
            if (exprent2.getLeft().type == 5 && ((FieldExprent)(exprent2 = (FieldExprent)((AssignmentExprent)exprent2).getLeft())).getClassname().equals(this.val$child.classStruct.qualifiedName) && this.val$mapFieldsToNewVars.containsKey(InterpreterUtil.makeUniqueKey(((FieldExprent)exprent2).getName(), ((FieldExprent)exprent2).getDescriptor()))) {
                return 2;
            }
        }
        if (this.val$child.type == 2 && "<init>".equals(this.val$meth.methodStruct.getName()) && exprent.type == 8 && ((InvocationExprent)(exprent2 = (InvocationExprent)exprent)).getFunctype() == 2) {
            this.val$child.superInvocation = exprent2;
            return 2;
        }
        this.replaceExprent(exprent);
        return 0;
    }

    private Exprent replaceExprent(Exprent exprent) {
        int n;
        if (exprent.type == 12) {
            n = ((VarExprent)exprent).getIndex0();
            if (this.val$mapParamsToNewVars.containsKey(n)) {
                VarVersionPaar varVersionPaar = (VarVersionPaar)this.val$mapParamsToNewVars.get(n);
                this.val$meth.varproc.getExternvars().add(varVersionPaar);
                return new VarExprent(varVersionPaar.var, this.val$meth.varproc.getVarType(varVersionPaar), this.val$meth.varproc);
            }
        } else if (exprent.type == 5) {
            FieldExprent fieldExprent = (FieldExprent)exprent;
            Object object = InterpreterUtil.makeUniqueKey(fieldExprent.getName(), fieldExprent.getDescriptor());
            if (fieldExprent.getClassname().equals(this.val$child.classStruct.qualifiedName) && this.val$mapFieldsToNewVars.containsKey(object)) {
                VarVersionPaar varVersionPaar = (VarVersionPaar)this.val$mapFieldsToNewVars.get(object);
                this.val$meth.varproc.getExternvars().add(varVersionPaar);
                return new VarExprent(varVersionPaar.var, this.val$meth.varproc.getVarType(varVersionPaar), this.val$meth.varproc);
            }
        }
        n = 1;
        block0: while (n != 0) {
            n = 0;
            for (Object object : exprent.getAllExprents()) {
                Exprent exprent2 = this.replaceExprent((Exprent)object);
                if (exprent2 == null) continue;
                exprent.replaceExprent((Exprent)object, exprent2);
                n = 1;
                continue block0;
            }
        }
        return null;
    }
}

