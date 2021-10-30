/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.deobfuscator;

import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.modules.decompiler.decompose.IGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ExceptionDeobfuscator$1
implements IGraph {
    private final /* synthetic */ ControlFlowGraph val$graph;

    ExceptionDeobfuscator$1(ControlFlowGraph controlFlowGraph) {
        this.val$graph = controlFlowGraph;
    }

    public final List getReversePostOrderList() {
        return this.val$graph.getReversePostOrder();
    }

    public final Set getRoots() {
        return new HashSet(Arrays.asList(this.val$graph.getFirst()));
    }
}

