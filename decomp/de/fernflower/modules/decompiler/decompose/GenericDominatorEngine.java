/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.decompose.IGraph;
import de.fernflower.modules.decompiler.decompose.IGraphNode;
import de.fernflower.util.VBStyleCollection;
import java.util.List;
import java.util.Set;

public final class GenericDominatorEngine {
    private IGraph graph;
    private VBStyleCollection colOrderedIDoms = new VBStyleCollection();
    private Set setRoots;

    public GenericDominatorEngine(IGraph iGraph) {
        this.graph = iGraph;
    }

    /*
     * Handled impossible loop by duplicating code
     * Enabled aggressive block sorting
     */
    public final void orderNodes() {
        boolean bl;
        Object object = this;
        this.setRoots = this.graph.getRoots();
        for (IGraphNode iGraphNode22 : ((GenericDominatorEngine)object).graph.getReversePostOrderList()) {
            ((GenericDominatorEngine)object).colOrderedIDoms.addWithKey(null, iGraphNode22);
        }
        List list = this.colOrderedIDoms.getLstKeys();
        do {
            bl = false;
            for (IGraphNode iGraphNode : list) {
                IGraphNode iGraphNode22;
                block13: {
                    object = null;
                    if (this.setRoots.contains(iGraphNode)) break block13;
                    for (IGraphNode iGraphNode22 : iGraphNode.getPredecessors()) {
                        Object object2;
                        block12: {
                            block17: {
                                int n;
                                int n2;
                                VBStyleCollection vBStyleCollection;
                                block16: {
                                    block15: {
                                        block14: {
                                            if (this.colOrderedIDoms.getWithKey(iGraphNode22) == null) continue;
                                            vBStyleCollection = this.colOrderedIDoms;
                                            if (object != null) break block14;
                                            object2 = iGraphNode22;
                                            break block12;
                                        }
                                        if (iGraphNode22 != null) break block15;
                                        object2 = object;
                                        break block12;
                                    }
                                    n2 = vBStyleCollection.getIndexByKey(object);
                                    if (!true) break block16;
                                    n = vBStyleCollection.getIndexByKey(iGraphNode22);
                                    if (n2 == n) break block17;
                                }
                                do {
                                    Object object3;
                                    if (n2 > n) {
                                        object3 = object;
                                        if (object3 == (object = (IGraphNode)vBStyleCollection.getWithKey(object))) {
                                            object2 = null;
                                            break block12;
                                        } else {
                                            n2 = vBStyleCollection.getIndexByKey(object);
                                            continue;
                                        }
                                    }
                                    object3 = iGraphNode22;
                                    if (object3 == (iGraphNode22 = (IGraphNode)vBStyleCollection.getWithKey(iGraphNode22))) {
                                        object2 = null;
                                        break block12;
                                    }
                                    n = vBStyleCollection.getIndexByKey(iGraphNode22);
                                } while (n2 != n);
                            }
                            object2 = object = object;
                        }
                        if (object2 == null) break;
                    }
                }
                if (object == null) {
                    object = iGraphNode;
                }
                if (object.equals(iGraphNode22 = (IGraphNode)this.colOrderedIDoms.putWithKey(object, iGraphNode))) continue;
                bl = true;
            }
        } while (bl);
    }

    public final boolean isDominator(IGraphNode iGraphNode, IGraphNode iGraphNode2) {
        while (!iGraphNode.equals(iGraphNode2)) {
            IGraphNode iGraphNode3 = (IGraphNode)this.colOrderedIDoms.getWithKey(iGraphNode);
            if (iGraphNode3 == iGraphNode) {
                return false;
            }
            if (iGraphNode3 == null) {
                throw new RuntimeException("Inconsistent idom sequence discovered!");
            }
            iGraphNode = iGraphNode3;
        }
        return true;
    }
}

