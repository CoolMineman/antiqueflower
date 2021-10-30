/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.cfg.BasicBlock;
import java.util.ArrayList;
import java.util.List;

final class FinallyProcessor$1BlockStackEntry {
    public BasicBlock blockCatch;
    public BasicBlock blockSample;
    public List lstStoreVars;

    public FinallyProcessor$1BlockStackEntry(BasicBlock basicBlock, BasicBlock basicBlock2, List list) {
        this.blockCatch = basicBlock;
        this.blockSample = basicBlock2;
        this.lstStoreVars = new ArrayList(list);
    }
}

