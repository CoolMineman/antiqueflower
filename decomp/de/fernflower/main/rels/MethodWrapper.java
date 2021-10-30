/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.struct.StructMethod;
import java.util.HashSet;
import java.util.List;

public final class MethodWrapper {
    public RootStatement root;
    public VarProcessor varproc;
    public StructMethod methodStruct;
    public CounterContainer counter;
    private DirectGraph graph;
    public List signatureFields;
    public boolean decompiledWithErrors;
    public HashSet setOuterVarNames = new HashSet();

    public MethodWrapper(RootStatement rootStatement, VarProcessor varProcessor, StructMethod structMethod, CounterContainer counterContainer) {
        this.root = rootStatement;
        this.varproc = varProcessor;
        this.methodStruct = structMethod;
        this.counter = counterContainer;
    }

    public final DirectGraph getOrBuildGraph() {
        if (this.graph == null && this.root != null) {
            FlattenStatementsHelper flattenStatementsHelper = new FlattenStatementsHelper();
            this.graph = flattenStatementsHelper.buildDirectGraph(this.root);
        }
        return this.graph;
    }
}

