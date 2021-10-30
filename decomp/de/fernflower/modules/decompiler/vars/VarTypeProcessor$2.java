/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.vars.VarTypeProcessor;

final class VarTypeProcessor$2
implements DirectGraph$ExprentIterator {
    private /* synthetic */ VarTypeProcessor this$0;

    VarTypeProcessor$2(VarTypeProcessor varTypeProcessor) {
        this.this$0 = varTypeProcessor;
    }

    public final int processExprent(Exprent exprent) {
        if (VarTypeProcessor.access$0(this.this$0, exprent)) {
            return 0;
        }
        return 1;
    }
}

