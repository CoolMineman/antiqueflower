/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$1StatementStackEntry;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$Edge;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$FinallyPathWrapper;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$StackEntry;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import de.fernflower.modules.decompiler.stats.SynchronizedStatement;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class FlattenStatementsHelper {
    private HashMap mapDestinationNodes = new HashMap();
    private List listEdges = new ArrayList();
    private HashMap mapShortRangeFinallyPathIds = new HashMap();
    private HashMap mapLongRangeFinallyPathIds = new HashMap();
    private HashMap mapPosIfBranch = new HashMap();
    private DirectGraph graph;
    private RootStatement root;

    public final DirectGraph buildDirectGraph(RootStatement rootStatement) {
        this.root = rootStatement;
        this.graph = new DirectGraph();
        Object object = this;
        Object object2 = new LinkedList<FlattenStatementsHelper$1StatementStackEntry>();
        ((LinkedList)object2).add(new FlattenStatementsHelper$1StatementStackEntry(((FlattenStatementsHelper)object).root, new LinkedList(), null));
        block15: while (!((AbstractCollection)object2).isEmpty()) {
            Object object32;
            FlattenStatementsHelper$1StatementStackEntry flattenStatementsHelper$1StatementStackEntry = (FlattenStatementsHelper$1StatementStackEntry)((LinkedList)object2).removeFirst();
            Object object4 = flattenStatementsHelper$1StatementStackEntry.statement;
            LinkedList<FlattenStatementsHelper$StackEntry> linkedList = flattenStatementsHelper$1StatementStackEntry.stackFinally;
            int n = flattenStatementsHelper$1StatementStackEntry.statementIndex;
            Object object5 = null;
            DirectNode directNode = null;
            List<StatEdge> list = new ArrayList();
            Object object6 = null;
            if (flattenStatementsHelper$1StatementStackEntry.succEdges == null) {
                block0 : switch (((Statement)object4).type) {
                    case 8: {
                        object5 = new DirectNode((Statement)object4, (BasicBlockStatement)object4);
                        if (((Statement)object4).getExprents() != null) {
                            ((DirectNode)object5).exprents = ((Statement)object4).getExprents();
                        }
                        object.graph.nodes.putWithKey(object5, ((DirectNode)object5).id);
                        String[] stringArray = new String[2];
                        stringArray[0] = ((DirectNode)object5).id;
                        ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, stringArray);
                        list.addAll(((Statement)object4).getSuccessorEdges(0x40000000));
                        object6 = object5;
                        List list2 = flattenStatementsHelper$1StatementStackEntry.tailExprents;
                        if (list2 != null) {
                            directNode = new DirectNode(2, (Statement)object4, ((Statement)object4).id + "_tail");
                            new DirectNode(2, (Statement)object4, ((Statement)object4).id + "_tail").exprents = list2;
                            object.graph.nodes.putWithKey(directNode, directNode.id);
                            String[] stringArray2 = new String[2];
                            stringArray2[0] = directNode.id;
                            ((FlattenStatementsHelper)object).mapDestinationNodes.put(-((Statement)object4).id.intValue(), stringArray2);
                            ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(((DirectNode)object5).id, -((Statement)object4).id.intValue(), 1));
                            object6 = directNode;
                        }
                        if (((Statement)object4).getLastBasicType() != 0) break;
                        ((FlattenStatementsHelper)object).mapPosIfBranch.put(((DirectNode)object6).id, ((StatEdge)list.get((int)0)).getDestination().id.toString());
                        break;
                    }
                    case 7: 
                    case 12: {
                        directNode = new DirectNode(6, (Statement)object4, ((Statement)object4).id + "_try");
                        String[] stringArray = new String[2];
                        stringArray[0] = directNode.id;
                        ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, stringArray);
                        object.graph.nodes.putWithKey(directNode, directNode.id);
                        object5 = new LinkedList();
                        for (Statement statement : ((Statement)object4).getStats()) {
                            ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(directNode.id, statement.id, 1));
                            LinkedList<FlattenStatementsHelper$StackEntry> linkedList2 = linkedList;
                            if (((Statement)object4).type == 12 && ((CatchAllStatement)object4).isFinally()) {
                                linkedList2 = new LinkedList<FlattenStatementsHelper$StackEntry>(linkedList);
                                if (statement == ((Statement)object4).getFirst()) {
                                    linkedList2.add(new FlattenStatementsHelper$StackEntry((CatchAllStatement)object4, Boolean.FALSE));
                                } else {
                                    linkedList2.add(new FlattenStatementsHelper$StackEntry((CatchAllStatement)object4, Boolean.TRUE, 4, ((FlattenStatementsHelper)object).root.getDummyExit(), statement, statement, directNode, directNode, true));
                                }
                            }
                            ((LinkedList)object5).add(new FlattenStatementsHelper$1StatementStackEntry(statement, linkedList2, null));
                        }
                        ((LinkedList)object2).addAll(0, object5);
                        break;
                    }
                    case 5: {
                        Object object7;
                        if (n == 0) {
                            flattenStatementsHelper$1StatementStackEntry.statementIndex = 1;
                            ((LinkedList)object2).addFirst(flattenStatementsHelper$1StatementStackEntry);
                            ((LinkedList)object2).addFirst(new FlattenStatementsHelper$1StatementStackEntry(((Statement)object4).getFirst(), linkedList, null));
                            continue block15;
                        }
                        directNode = (DirectNode)object.graph.nodes.getWithKey(((String[])((FlattenStatementsHelper)object).mapDestinationNodes.get(object4.getFirst().id))[0]);
                        DoStatement doStatement = (DoStatement)object4;
                        int n2 = doStatement.getLooptype();
                        if (n2 == 0) {
                            ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, new String[]{directNode.id, directNode.id});
                            break;
                        }
                        list.add((StatEdge)((Statement)object4).getSuccessorEdges(0x40000000).get(0));
                        switch (n2) {
                            case 1: 
                            case 2: {
                                object5 = new DirectNode(4, (Statement)object4, ((Statement)object4).id + "_cond");
                                new DirectNode(4, (Statement)object4, ((Statement)object4).id + "_cond").exprents = doStatement.getConditionExprentList();
                                object.graph.nodes.putWithKey(object5, ((DirectNode)object5).id);
                                ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(((DirectNode)object5).id, object4.getFirst().id, 1));
                                if (n2 == 2) {
                                    ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, new String[]{((DirectNode)object5).id, ((DirectNode)object5).id});
                                } else {
                                    ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, new String[]{directNode.id, ((DirectNode)object5).id});
                                    boolean bl = false;
                                    for (Object object32 : ((FlattenStatementsHelper)object).listEdges) {
                                        if (!((FlattenStatementsHelper$Edge)object32).statid.equals(((Statement)object4).id) || ((FlattenStatementsHelper$Edge)object32).edgetype != 8) continue;
                                        bl = true;
                                        break;
                                    }
                                    if (!bl) {
                                        ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(directNode.id, ((Statement)object4).id, 8));
                                    }
                                }
                                object7 = object5;
                                break block0;
                            }
                            case 3: {
                                DirectNode directNode2 = new DirectNode(3, (Statement)object4, ((Statement)object4).id + "_init");
                                if (doStatement.getInitExprent() != null) {
                                    directNode2.exprents = doStatement.getInitExprentList();
                                }
                                object.graph.nodes.putWithKey(directNode2, directNode2.id);
                                object32 = new DirectNode(4, (Statement)object4, ((Statement)object4).id + "_cond");
                                new DirectNode(4, (Statement)object4, ((Statement)object4).id + "_cond").exprents = doStatement.getConditionExprentList();
                                object.graph.nodes.putWithKey(object32, ((DirectNode)object32).id);
                                DirectNode directNode3 = new DirectNode(5, (Statement)object4, ((Statement)object4).id + "_inc");
                                new DirectNode(5, (Statement)object4, ((Statement)object4).id + "_inc").exprents = doStatement.getIncExprentList();
                                object.graph.nodes.putWithKey(directNode3, directNode3.id);
                                ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, new String[]{directNode2.id, directNode3.id});
                                String[] stringArray = new String[2];
                                stringArray[0] = ((DirectNode)object32).id;
                                ((FlattenStatementsHelper)object).mapDestinationNodes.put(-((Statement)object4).id.intValue(), stringArray);
                                ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(((DirectNode)object32).id, object4.getFirst().id, 1));
                                ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(directNode2.id, -((Statement)object4).id.intValue(), 1));
                                ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(directNode3.id, -((Statement)object4).id.intValue(), 1));
                                boolean bl = false;
                                for (FlattenStatementsHelper$Edge flattenStatementsHelper$Edge : ((FlattenStatementsHelper)object).listEdges) {
                                    if (!flattenStatementsHelper$Edge.statid.equals(((Statement)object4).id) || flattenStatementsHelper$Edge.edgetype != 8) continue;
                                    bl = true;
                                    break;
                                }
                                if (!bl) {
                                    ((FlattenStatementsHelper)object).listEdges.add(new FlattenStatementsHelper$Edge(directNode.id, ((Statement)object4).id, 8));
                                }
                                object6 = object32;
                            }
                        }
                        break;
                    }
                    case 2: 
                    case 6: 
                    case 10: 
                    case 13: 
                    case 15: {
                        int n3 = ((Statement)object4).getStats().size();
                        if (((Statement)object4).type == 10) {
                            n3 = 2;
                        }
                        if (n > n3) break;
                        object32 = null;
                        switch (((Statement)object4).type) {
                            case 10: {
                                object32 = ((SynchronizedStatement)object4).getHeadexprentList();
                                break;
                            }
                            case 6: {
                                object32 = ((SwitchStatement)object4).getHeadexprentList();
                                break;
                            }
                            case 2: {
                                object32 = ((IfStatement)object4).getHeadexprentList();
                            }
                        }
                        int n4 = n;
                        if (n4 < n3) {
                            flattenStatementsHelper$1StatementStackEntry.statementIndex = n4 + 1;
                            ((LinkedList)object2).addFirst(flattenStatementsHelper$1StatementStackEntry);
                            ((LinkedList)object2).addFirst(new FlattenStatementsHelper$1StatementStackEntry((Statement)((Statement)object4).getStats().get(n4), linkedList, (List)(n4 == 0 && object32 != null && object32.get(0) != null ? object32 : null)));
                            continue block15;
                        }
                        object5 = (DirectNode)object.graph.nodes.getWithKey(((String[])((FlattenStatementsHelper)object).mapDestinationNodes.get(object4.getFirst().id))[0]);
                        String[] stringArray = new String[2];
                        stringArray[0] = ((DirectNode)object5).id;
                        ((FlattenStatementsHelper)object).mapDestinationNodes.put(((Statement)object4).id, stringArray);
                        if (((Statement)object4).type != 2 || ((IfStatement)object4).iftype != 0) break;
                        list.add((StatEdge)((Statement)object4).getSuccessorEdges(0x40000000).get(0));
                        Object object7 = object6 = object32.get(0) == null ? object5 : (DirectNode)object.graph.nodes.getWithKey(String.valueOf(((DirectNode)object5).id) + "_tail");
                    }
                }
            }
            if (object6 == null) continue;
            if (flattenStatementsHelper$1StatementStackEntry.succEdges != null) {
                list = flattenStatementsHelper$1StatementStackEntry.succEdges;
            }
            n = flattenStatementsHelper$1StatementStackEntry.edgeIndex;
            while (n < list.size()) {
                boolean bl;
                StatEdge statEdge = (StatEdge)list.get(n);
                object5 = new LinkedList(linkedList);
                int n5 = statEdge.getType();
                Object object8 = statEdge.getDestination();
                Object object9 = object6;
                object32 = object6;
                Statement statement = null;
                Statement statement2 = null;
                boolean bl2 = false;
                boolean bl3 = false;
                do {
                    object4 = null;
                    if (!((AbstractCollection)object5).isEmpty()) {
                        object4 = (FlattenStatementsHelper$StackEntry)((LinkedList)object5).getLast();
                    }
                    bl = true;
                    if (object4 == null) {
                        super.saveEdge((DirectNode)object6, (Statement)object8, n5, (DirectNode)(bl3 ? object9 : null), (DirectNode)object32, statement, statement2, bl2);
                        continue;
                    }
                    CatchAllStatement catchAllStatement = ((FlattenStatementsHelper$StackEntry)object4).catchstatement;
                    if (((FlattenStatementsHelper$StackEntry)object4).state) {
                        if (n5 == 32) {
                            ((LinkedList)object5).removeLast();
                            object8 = ((FlattenStatementsHelper$StackEntry)object4).destination;
                            n5 = ((FlattenStatementsHelper$StackEntry)object4).edgetype;
                            object9 = ((FlattenStatementsHelper$StackEntry)object4).finallyShortRangeSource;
                            object32 = ((FlattenStatementsHelper$StackEntry)object4).finallyLongRangeSource;
                            statement = ((FlattenStatementsHelper$StackEntry)object4).finallyShortRangeEntry;
                            statement2 = ((FlattenStatementsHelper$StackEntry)object4).finallyLongRangeEntry;
                            bl3 = true;
                            bl2 = catchAllStatement.getMonitor() != null & ((FlattenStatementsHelper$StackEntry)object4).isFinallyExceptionPath;
                            bl = false;
                            continue;
                        }
                        if (!catchAllStatement.containsStatementStrict((Statement)object8)) {
                            ((LinkedList)object5).removeLast();
                            bl = false;
                            continue;
                        }
                        super.saveEdge((DirectNode)object6, (Statement)object8, n5, (DirectNode)(bl3 ? object9 : null), (DirectNode)object32, statement, statement2, bl2);
                        continue;
                    }
                    if (!catchAllStatement.containsStatementStrict((Statement)object8)) {
                        super.saveEdge((DirectNode)object6, catchAllStatement.getHandler(), 1, (DirectNode)(bl3 ? object9 : null), (DirectNode)object32, statement, statement2, bl2);
                        ((LinkedList)object5).removeLast();
                        ((LinkedList)object5).add(new FlattenStatementsHelper$StackEntry(catchAllStatement, Boolean.TRUE, n5, (Statement)object8, catchAllStatement.getHandler(), statement2 == null ? catchAllStatement.getHandler() : statement2, (DirectNode)object6, (DirectNode)object32, false));
                        flattenStatementsHelper$1StatementStackEntry.edgeIndex = n + 1;
                        flattenStatementsHelper$1StatementStackEntry.succEdges = list;
                        ((LinkedList)object2).addFirst(flattenStatementsHelper$1StatementStackEntry);
                        ((LinkedList)object2).addFirst(new FlattenStatementsHelper$1StatementStackEntry(catchAllStatement.getHandler(), (LinkedList)object5, null));
                        continue block15;
                    }
                    super.saveEdge((DirectNode)object6, (Statement)object8, n5, (DirectNode)(bl3 ? object9 : null), (DirectNode)object32, statement, statement2, bl2);
                } while (!bl);
                ++n;
            }
        }
        object = rootStatement.getDummyExit();
        object2 = new DirectNode(1, (Statement)object, ((Statement)object).id.toString());
        new DirectNode(1, (Statement)object, ((Statement)object).id.toString()).exprents = new ArrayList();
        this.graph.nodes.addWithKey(object2, ((DirectNode)object2).id);
        String[] stringArray = new String[2];
        stringArray[0] = ((DirectNode)object2).id;
        this.mapDestinationNodes.put(((Statement)object).id, stringArray);
        this.setEdges();
        this.graph.first = (DirectNode)this.graph.nodes.getWithKey(((String[])this.mapDestinationNodes.get(rootStatement.id))[0]);
        this.graph.sortReversePostOrder();
        return this.graph;
    }

    private void saveEdge(DirectNode directNode, Statement statement, int n, DirectNode directNode2, DirectNode directNode3, Statement statement2, Statement statement3, boolean bl) {
        if (n != 32) {
            this.listEdges.add(new FlattenStatementsHelper$Edge(directNode.id, statement.id, n));
        }
        if (directNode2 != null) {
            List<String[]> list = (ArrayList<String[]>)this.mapShortRangeFinallyPathIds.get(directNode.id);
            if (list == null) {
                list = new ArrayList<String[]>();
                this.mapShortRangeFinallyPathIds.put(directNode.id, list);
            }
            list.add(new String[]{directNode2.id, statement.id.toString(), statement2.id.toString(), bl ? "1" : null});
            list = (List)this.mapLongRangeFinallyPathIds.get(directNode.id);
            if (list == null) {
                list = new ArrayList();
                this.mapLongRangeFinallyPathIds.put(directNode.id, list);
            }
            list.add(new String[]{directNode3.id, statement.id.toString(), statement3.id.toString()});
        }
    }

    private void setEdges() {
        Object object;
        Serializable serializable;
        for (FlattenStatementsHelper$Edge flattenStatementsHelper$Edge : this.listEdges) {
            String string = flattenStatementsHelper$Edge.sourceid;
            serializable = flattenStatementsHelper$Edge.statid;
            object = (DirectNode)this.graph.nodes.getWithKey(string);
            String[] stringArray = (String[])this.graph.nodes.getWithKey(((String[])this.mapDestinationNodes.get(serializable))[flattenStatementsHelper$Edge.edgetype == 8 ? 1 : 0]);
            if (!((DirectNode)object).succs.contains(stringArray)) {
                ((DirectNode)object).succs.add(stringArray);
            }
            if (!stringArray.preds.contains(object)) {
                stringArray.preds.add(object);
            }
            if (!this.mapPosIfBranch.containsKey(string) || ((Integer)serializable).equals(this.mapPosIfBranch.get(string))) continue;
            this.graph.mapNegIfBranch.put(string, stringArray.id);
        }
        int n = 0;
        while (n < 2) {
            for (Map.Entry entry : (n == 0 ? this.mapShortRangeFinallyPathIds : this.mapLongRangeFinallyPathIds).entrySet()) {
                serializable = new ArrayList();
                object = null;
                for (String[] stringArray : (List)entry.getValue()) {
                    DirectNode directNode = (DirectNode)this.graph.nodes.getWithKey(((String[])this.mapDestinationNodes.get(Integer.parseInt(stringArray[1])))[0]);
                    DirectNode directNode2 = (DirectNode)this.graph.nodes.getWithKey(((String[])this.mapDestinationNodes.get(Integer.parseInt(stringArray[2])))[0]);
                    serializable.add(new FlattenStatementsHelper$FinallyPathWrapper(stringArray[0], directNode.id, directNode2.id, null));
                    if (n != 0 || stringArray[3] == null) continue;
                    this.graph.mapFinallyMonitorExceptionPathExits.put((String)entry.getKey(), directNode.id);
                }
                if (serializable.isEmpty()) continue;
                (n == 0 ? this.graph.mapShortRangeFinallyPaths : this.graph.mapLongRangeFinallyPaths).put((String)entry.getKey(), new ArrayList(new HashSet(serializable)));
            }
            ++n;
        }
    }

    public final HashMap getMapDestinationNodes() {
        return this.mapDestinationNodes;
    }
}

