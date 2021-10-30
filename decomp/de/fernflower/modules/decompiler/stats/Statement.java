/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.code.InstructionSequence;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.StrongConnectivityHelper;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import de.fernflower.util.VBStyleCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Statement {
    public int type;
    public Integer id;
    private Map mapSuccEdges = new HashMap();
    private Map mapPredEdges = new HashMap();
    private Map mapSuccStates = new HashMap();
    private Map mapPredStates = new HashMap();
    protected VBStyleCollection stats = new VBStyleCollection();
    private Statement parent;
    protected Statement first;
    protected List exprents;
    private HashSet labelEdges = new HashSet();
    protected List varDefinitions = new ArrayList();
    private boolean copied = false;
    protected Statement post;
    protected int lastBasicType = 2;
    private boolean isMonitorEnter;
    private boolean containsMonitorExit;
    private HashSet continueSet = new HashSet();

    public Statement() {
        this.id = DecompilerContext.getCountercontainer().getCounterAndIncrement(0);
    }

    public final void clearTempInformation() {
        List list;
        Map map;
        this.post = null;
        this.continueSet = null;
        this.copied = false;
        this.isMonitorEnter = false;
        this.containsMonitorExit = false;
        Map[] mapArray = new Map[]{this.mapSuccEdges, this.mapPredEdges};
        int n = mapArray.length;
        int n2 = 0;
        while (n2 < n) {
            map = mapArray[n2];
            map.remove(2);
            list = (List)map.get(0x40000000);
            if (list != null) {
                map.put(Integer.MIN_VALUE, new ArrayList(list));
            } else {
                map.remove(Integer.MIN_VALUE);
            }
            ++n2;
        }
        mapArray = new Map[]{this.mapSuccStates, this.mapPredStates};
        n = mapArray.length;
        n2 = 0;
        while (n2 < n) {
            map = mapArray[n2];
            map.remove(2);
            list = (List)map.get(0x40000000);
            if (list != null) {
                map.put(Integer.MIN_VALUE, new ArrayList(list));
            } else {
                map.remove(Integer.MIN_VALUE);
            }
            ++n2;
        }
    }

    public final void collapseNodesToStatement(Statement statement) {
        Object object3 = null;
        Object object22 = statement.first;
        Statement statement2 = null;
        statement2 = statement.post;
        VBStyleCollection vBStyleCollection = statement.stats;
        if (statement2 != null) {
            for (Object object3 : statement2.getEdges(0x40000000, 0)) {
                if (!statement.containsStatementStrict(((StatEdge)object3).getSource())) continue;
                ((StatEdge)object3).getSource().changeEdgeType(1, (StatEdge)object3, 4);
                statement.addLabeledEdge((StatEdge)object3);
            }
        }
        for (Object object3 : ((Statement)object22).getEdges(Integer.MIN_VALUE, 0)) {
            if (((StatEdge)object3).getType() != 2 && statement.containsStatementStrict(((StatEdge)object3).getSource())) {
                ((StatEdge)object3).getSource().changeEdgeType(1, (StatEdge)object3, 8);
                statement.addLabeledEdge((StatEdge)object3);
            }
            ((Statement)object22).removePredecessor((StatEdge)object3);
            ((StatEdge)object3).getSource().changeEdgeNode(1, (StatEdge)object3, statement);
            Iterator iterator = object3;
            statement.addEdgeInternal(0, (StatEdge)((Object)iterator));
        }
        if (vBStyleCollection.containsKey(this.first.id)) {
            this.first = statement;
        }
        object3 = new HashSet(((Statement)object22).getNeighbours(2, 1));
        for (Object object4 : vBStyleCollection) {
            object3.retainAll(((Statement)object4).getNeighbours(2, 1));
        }
        if (!object3.isEmpty()) {
            for (Object object4 : ((Statement)object22).getEdges(2, 1)) {
                object22 = ((StatEdge)object4).getDestination();
                if (!object3.contains(object22) || vBStyleCollection.containsKey(((Statement)object22).id)) continue;
                statement.addSuccessor(new StatEdge(statement, (Statement)object22, ((StatEdge)object4).getException()));
            }
            for (Object object4 : vBStyleCollection) {
                for (Object object22 : ((Statement)object4).getEdges(2, 1)) {
                    if (!object3.contains(((StatEdge)object22).getDestination())) continue;
                    ((Statement)object4).removeSuccessor((StatEdge)object22);
                }
            }
        }
        if (statement2 != null && !statement.getNeighbours(2, 1).contains(statement2)) {
            statement.addSuccessor(new StatEdge(1, statement, statement2));
        }
        for (Object object4 : vBStyleCollection) {
            this.stats.removeWithKey(((Statement)object4).id);
        }
        this.stats.addWithKey(statement, statement.id);
        statement.setAllParent();
        statement.parent = this;
        statement.buildContinueSet();
        statement.buildMonitorFlags();
        if (statement.type == 6) {
            ((SwitchStatement)statement).sortEdgesAndNodes();
        }
    }

    public final void setAllParent() {
        Iterator iterator = this.stats.iterator();
        while (iterator.hasNext()) {
            ((Statement)iterator.next()).parent = this;
        }
    }

    public final void addLabeledEdge(StatEdge statEdge) {
        if (statEdge.closure != null) {
            statEdge.closure.labelEdges.remove(statEdge);
        }
        statEdge.closure = this;
        this.labelEdges.add(statEdge);
    }

    private void addEdgeDirectInternal(int n, StatEdge statEdge, int n2) {
        ArrayList<Statement> arrayList = n == 0 ? ((Statement)map).mapPredEdges : ((Statement)map).mapSuccEdges;
        Map map = n == 0 ? ((Statement)map).mapPredStates : ((Statement)map).mapSuccStates;
        ArrayList<StatEdge> arrayList2 = (ArrayList<StatEdge>)arrayList.get((Object)n2);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<StatEdge>();
            arrayList.put(n2, arrayList2);
        }
        arrayList2.add(statEdge);
        arrayList = (List)map.get(n2);
        if (arrayList == null) {
            arrayList = new ArrayList<Statement>();
            map.put(n2, arrayList);
        }
        arrayList.add(n == 0 ? statEdge.getSource() : statEdge.getDestination());
    }

    private void addEdgeInternal(int n, StatEdge statEdge) {
        int n2 = statEdge.getType();
        int[] nArray = n2 == 2 ? new int[]{Integer.MIN_VALUE, 2} : new int[]{Integer.MIN_VALUE, 0x40000000, n2};
        int[] nArray2 = nArray;
        int n3 = nArray.length;
        int n4 = 0;
        while (n4 < n3) {
            int n5 = nArray2[n4];
            this.addEdgeDirectInternal(n, statEdge, n5);
            ++n4;
        }
    }

    private void removeEdgeDirectInternal(int n, StatEdge statEdge, int n2) {
        int n3;
        Map map = n == 0 ? ((Statement)map2).mapPredEdges : ((Statement)map2).mapSuccEdges;
        Map map2 = n == 0 ? ((Statement)map2).mapPredStates : ((Statement)map2).mapSuccStates;
        List list = (List)map.get(n2);
        if (list != null && (n3 = list.indexOf(statEdge)) >= 0) {
            list.remove(n3);
            ((List)map2.get(n2)).remove(n3);
        }
    }

    private void removeEdgeInternal(int n, StatEdge statEdge) {
        int n2 = statEdge.getType();
        int[] nArray = n2 == 2 ? new int[]{Integer.MIN_VALUE, 2} : new int[]{Integer.MIN_VALUE, 0x40000000, n2};
        int[] nArray2 = nArray;
        int n3 = nArray.length;
        int n4 = 0;
        while (n4 < n3) {
            int n5 = nArray2[n4];
            this.removeEdgeDirectInternal(n, statEdge, n5);
            ++n4;
        }
    }

    public final void addPredecessor(StatEdge statEdge) {
        this.addEdgeInternal(0, statEdge);
    }

    public final void removePredecessor(StatEdge statEdge) {
        if (statEdge == null) {
            return;
        }
        this.removeEdgeInternal(0, statEdge);
    }

    public final void addSuccessor(StatEdge statEdge) {
        this.addEdgeInternal(1, statEdge);
        if (statEdge.closure != null) {
            statEdge.closure.labelEdges.add(statEdge);
        }
        statEdge.getDestination().addEdgeInternal(0, statEdge);
    }

    public final void removeSuccessor(StatEdge statEdge) {
        if (statEdge == null) {
            return;
        }
        this.removeEdgeInternal(1, statEdge);
        if (statEdge.closure != null) {
            statEdge.closure.labelEdges.remove(statEdge);
        }
        if (statEdge.getDestination() != null) {
            statEdge.getDestination().removePredecessor(statEdge);
        }
    }

    public final void removeAllSuccessors(Statement statement) {
        if (statement == null) {
            return;
        }
        StatEdge statEdge2 = null;
        for (StatEdge statEdge2 : this.getEdges(Integer.MIN_VALUE, 1)) {
            if (statEdge2.getDestination() != statement) continue;
            this.removeSuccessor(statEdge2);
        }
    }

    public final HashSet buildContinueSet() {
        this.continueSet.clear();
        for (Object object : this.stats) {
            this.continueSet.addAll(((Statement)object).buildContinueSet());
            if (object == this.first) continue;
            this.continueSet.remove(((Statement)object).getBasichead());
        }
        for (Object object : this.getEdges(8, 1)) {
            this.continueSet.add(((StatEdge)object).getDestination().getBasichead());
        }
        if (this.type == 5) {
            this.continueSet.remove(this.first.getBasichead());
        }
        return this.continueSet;
    }

    public final void buildMonitorFlags() {
        Statement statement2;
        Object object = this.stats.iterator();
        while (object.hasNext()) {
            statement2 = null;
            ((Statement)object.next()).buildMonitorFlags();
        }
        switch (this.type) {
            case 8: {
                statement2 = null;
                object = ((BasicBlockStatement)this).getBlock().getSeq();
                if (object == null || ((InstructionSequence)object).length() <= 0) break;
                int n = 0;
                while (n < ((InstructionSequence)object).length()) {
                    if (object.getInstr((int)n).opcode == 195) {
                        this.containsMonitorExit = true;
                        break;
                    }
                    ++n;
                }
                this.isMonitorEnter = object.getLastInstr().opcode == 194;
                return;
            }
            case 2: 
            case 15: {
                this.containsMonitorExit = false;
                for (Statement statement2 : this.stats) {
                    this.containsMonitorExit |= statement2.containsMonitorExit;
                }
                return;
            }
            case 0: 
            case 10: 
            case 13: {
                return;
            }
            default: {
                this.containsMonitorExit = false;
                for (Statement statement2 : this.stats) {
                    this.containsMonitorExit |= statement2.containsMonitorExit;
                }
            }
        }
    }

    public final List getReversePostOrderList() {
        Statement statement = ((Statement)arrayList).first;
        ArrayList arrayList = new ArrayList();
        Statement.addToReversePostOrderListIterative(statement, arrayList);
        return arrayList;
    }

    public final List getPostReversePostOrderList(List object) {
        HashSet hashSet;
        ArrayList arrayList = new ArrayList();
        if (object == null) {
            hashSet = null;
            object = StrongConnectivityHelper.getExitReps(new StrongConnectivityHelper(this).getComponents());
        }
        hashSet = new HashSet();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (Statement)iterator.next();
            this.addToPostReversePostOrderList((Statement)object, arrayList, hashSet);
        }
        if (arrayList.size() != this.stats.size()) {
            DecompilerContext.getLogger().writeMessage("computing post reverse post order failed!", 4);
            throw new RuntimeException("parsing failure!");
        }
        return arrayList;
    }

    public final boolean containsStatement(Statement statement) {
        return this == statement || this.containsStatementStrict(statement);
    }

    public final boolean containsStatementStrict(Statement statement) {
        if (this.stats.contains(statement)) {
            return true;
        }
        int n = 0;
        while (n < this.stats.size()) {
            if (((Statement)this.stats.get(n)).containsStatementStrict(statement)) {
                return true;
            }
            ++n;
        }
        return false;
    }

    public String toJava(int n) {
        throw new RuntimeException("not implemented");
    }

    public List getSequentialObjects() {
        return new ArrayList(this.stats);
    }

    public void initExprents() {
    }

    public void replaceExprent(Exprent exprent, Exprent exprent2) {
    }

    public Statement getSimpleCopy() {
        throw new RuntimeException("not implemented");
    }

    public void initSimpleCopy() {
        if (!this.stats.isEmpty()) {
            this.first = (Statement)this.stats.get(0);
        }
    }

    public void replaceStatement(Statement statement, Statement statement2) {
        Object object;
        StatEdge statEdge2 = null;
        for (StatEdge statEdge2 : statement.getEdges(Integer.MIN_VALUE, 0)) {
            statement.removePredecessor(statEdge2);
            statEdge2.getSource().changeEdgeNode(1, statEdge2, statement2);
            object = statEdge2;
            statement2.addEdgeInternal(0, (StatEdge)object);
        }
        for (StatEdge statEdge2 : statement.getEdges(Integer.MIN_VALUE, 1)) {
            statement.removeSuccessor(statEdge2);
            statEdge2.setSource(statement2);
            statement2.addSuccessor(statEdge2);
        }
        int n = this.stats.getIndexByKey(statement.id);
        this.stats.removeWithKey(statement.id);
        this.stats.addWithKeyAndIndex(n, statement2, statement2.id);
        object = this;
        Object var3_5 = null;
        statement2.parent = object;
        statement2.post = statement.post;
        if (this.first == statement) {
            this.first = statement2;
        }
        ArrayList arrayList = new ArrayList(statement.labelEdges);
        int n2 = arrayList.size() - 1;
        while (n2 >= 0) {
            object = (StatEdge)arrayList.get(n2);
            if (((StatEdge)object).getSource() != statement2) {
                statement2.addLabeledEdge((StatEdge)object);
            } else if (this == ((StatEdge)object).getDestination() || this.containsStatementStrict(((StatEdge)object).getDestination())) {
                ((StatEdge)object).closure = null;
            } else {
                this.addLabeledEdge((StatEdge)object);
            }
            --n2;
        }
        statement.labelEdges.clear();
    }

    private static void addToReversePostOrderListIterative(Statement statement, List list) {
        LinkedList<Statement> linkedList = new LinkedList<Statement>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        HashSet<Statement> hashSet = new HashSet<Statement>();
        linkedList.add(statement);
        linkedList2.add(0);
        while (!linkedList.isEmpty()) {
            statement = (Statement)linkedList.getLast();
            int n = (Integer)linkedList2.removeLast();
            hashSet.add(statement);
            List list2 = null;
            list2 = statement.getEdges(Integer.MIN_VALUE, 1);
            while (n < list2.size()) {
                StatEdge statEdge = (StatEdge)list2.get(n);
                Statement statement2 = statEdge.getDestination();
                if (!(hashSet.contains(statement2) || statEdge.getType() != 1 && statEdge.getType() != 2)) {
                    linkedList2.add(n + 1);
                    linkedList.add(statement2);
                    linkedList2.add(0);
                    break;
                }
                ++n;
            }
            if (n != list2.size()) continue;
            list.add(0, statement);
            linkedList.removeLast();
        }
    }

    private void addToPostReversePostOrderList(Statement statement, List list, HashSet hashSet) {
        if (hashSet.contains(statement)) {
            return;
        }
        hashSet.add(statement);
        Iterator iterator = statement.getEdges(3, 0).iterator();
        while (iterator.hasNext()) {
            Statement statement2 = null;
            statement2 = ((StatEdge)iterator.next()).getSource();
            if (hashSet.contains(statement2)) continue;
            this.addToPostReversePostOrderList(statement2, list, hashSet);
        }
        list.add(0, statement);
    }

    public final void changeEdgeNode(int n, StatEdge statEdge, Statement statement) {
        Map map = ((Statement)map2).mapSuccEdges;
        Map map2 = ((Statement)map2).mapSuccStates;
        int n2 = statEdge.getType();
        int[] nArray = n2 == 2 ? new int[]{Integer.MIN_VALUE, 2} : new int[]{Integer.MIN_VALUE, 0x40000000, n2};
        int[] nArray2 = nArray;
        int n3 = nArray.length;
        int n4 = 0;
        while (n4 < n3) {
            int n5;
            int n6 = nArray2[n4];
            List list = (List)map.get(n6);
            if (list != null && (n5 = list.indexOf(statEdge)) >= 0) {
                ((List)map2.get(n6)).set(n5, statement);
            }
            ++n4;
        }
        statEdge.setDestination(statement);
    }

    public final void changeEdgeType(int n, StatEdge statEdge, int n2) {
        int n3 = statEdge.getType();
        if (n3 == n2) {
            return;
        }
        if (n3 == 2 || n2 == 2) {
            throw new RuntimeException("Invalid edge type!");
        }
        this.removeEdgeDirectInternal(n, statEdge, n3);
        this.addEdgeDirectInternal(n, statEdge, n2);
        if (n == 1) {
            statEdge.getDestination().changeEdgeType(0, statEdge, n2);
        }
        statEdge.setType(n2);
    }

    private List getEdges(int n, int n2) {
        List list;
        Object object = this = n2 == 0 ? ((Statement)this).mapPredEdges : ((Statement)this).mapSuccEdges;
        if ((n & n - 1) == 0) {
            list = (List)this.get(n);
            list = list == null ? new ArrayList() : new ArrayList(list);
        } else {
            list = new ArrayList();
            int[] nArray = StatEdge.TYPES;
            int n3 = StatEdge.TYPES.length;
            int n4 = 0;
            while (n4 < n3) {
                List list2;
                int n5 = nArray[n4];
                if ((n & n5) != 0 && (list2 = (List)this.get(n5)) != null) {
                    list.addAll(list2);
                }
                ++n4;
            }
        }
        return list;
    }

    public final List getNeighbours(int n, int n2) {
        List list;
        Object object = this = n2 == 0 ? ((Statement)this).mapPredStates : ((Statement)this).mapSuccStates;
        if ((n & n - 1) == 0) {
            list = (List)this.get(n);
            list = list == null ? new ArrayList() : new ArrayList(list);
        } else {
            list = new ArrayList();
            int[] nArray = StatEdge.TYPES;
            int n3 = StatEdge.TYPES.length;
            int n4 = 0;
            while (n4 < n3) {
                List list2;
                int n5 = nArray[n4];
                if ((n & n5) != 0 && (list2 = (List)this.get(n5)) != null) {
                    list.addAll(list2);
                }
                ++n4;
            }
        }
        return list;
    }

    public final Set getNeighboursSet(int n, int n2) {
        return new HashSet(this.getNeighbours(n, n2));
    }

    public final List getSuccessorEdges(int n) {
        return this.getEdges(n, 1);
    }

    public final List getPredecessorEdges(int n) {
        return this.getEdges(n, 0);
    }

    public final List getAllSuccessorEdges() {
        return this.getEdges(Integer.MIN_VALUE, 1);
    }

    public final List getAllPredecessorEdges() {
        return this.getEdges(Integer.MIN_VALUE, 0);
    }

    public final Statement getFirst() {
        return this.first;
    }

    public final void setFirst(Statement statement) {
        this.first = statement;
    }

    public final VBStyleCollection getStats() {
        return this.stats;
    }

    public final int getLastBasicType() {
        return this.lastBasicType;
    }

    public final HashSet getContinueSet() {
        return this.continueSet;
    }

    public final boolean isContainsMonitorExit() {
        return this.containsMonitorExit;
    }

    public final boolean isMonitorEnter() {
        return this.isMonitorEnter;
    }

    public final BasicBlockStatement getBasichead() {
        if (this.type == 8) {
            return (BasicBlockStatement)this;
        }
        return this.first.getBasichead();
    }

    public final boolean isLabeled() {
        for (Object object : ((Statement)object).labelEdges) {
            if (!((StatEdge)object).labeled || !((StatEdge)object).explicit) continue;
            return true;
        }
        return false;
    }

    public final boolean hasBasicSuccEdge() {
        return this.type == 8 || this.type == 2 && ((IfStatement)this).iftype == 0 || this.type == 5 && ((DoStatement)this).getLooptype() != 0;
    }

    public final Statement getParent() {
        return this.parent;
    }

    public final void setParent(Statement statement) {
        this.parent = statement;
    }

    public final HashSet getLabelEdges() {
        return this.labelEdges;
    }

    public final List getVarDefinitions() {
        return this.varDefinitions;
    }

    public final List getExprents() {
        return this.exprents;
    }

    public final void setExprents(List list) {
        this.exprents = list;
    }

    public final boolean isCopied() {
        return this.copied;
    }

    public final void setCopied() {
        this.copied = true;
    }
}

