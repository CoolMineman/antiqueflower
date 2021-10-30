/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.ConstantsUtil;
import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.SimpleInstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.code.cfg.ExceptionRangeCFG;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.code.DeadCodeHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.FinallyProcessor$1BlockStackEntry;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.SSAConstructorSparseEx;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.VarType;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class FinallyProcessor {
    private HashMap finallyBlockIDs = new HashMap();
    private HashMap catchallBlockIDs = new HashMap();
    private VarProcessor varprocessor;

    public FinallyProcessor(VarProcessor varProcessor) {
        this.varprocessor = varProcessor;
    }

    public final boolean processStatementEx(StructMethod structMethod, RootStatement rootStatement, ControlFlowGraph controlFlowGraph) {
        LinkedList<RootStatement> linkedList = new LinkedList<RootStatement>();
        linkedList.add(rootStatement);
        while (!linkedList.isEmpty()) {
            Statement statement = (Statement)linkedList.removeLast();
            Statement statement2 = statement.getParent();
            if (statement2 != null && statement2.type == 12 && statement == statement2.getFirst() && !statement2.isCopied()) {
                statement2 = (CatchAllStatement)statement2;
                BasicBlock basicBlock = statement2.getBasichead().getBlock();
                BasicBlock basicBlock2 = ((CatchAllStatement)statement2).getHandler().getBasichead().getBlock();
                if (!this.catchallBlockIDs.containsKey(basicBlock2.id)) {
                    Object[] objectArray;
                    if (this.finallyBlockIDs.containsKey(basicBlock2.id)) {
                        ((CatchAllStatement)statement2).setFinally();
                        objectArray = (Integer)this.finallyBlockIDs.get(basicBlock2.id);
                        ((CatchAllStatement)statement2).setMonitor(objectArray == null ? null : new VarExprent(objectArray.intValue(), VarType.VARTYPE_INT, this.varprocessor));
                    } else {
                        objectArray = FinallyProcessor.getFinallyInformation(structMethod, rootStatement, (CatchAllStatement)statement2);
                        if (objectArray == null) {
                            this.catchallBlockIDs.put(basicBlock2.id, null);
                        } else {
                            if (DecompilerContext.getOption("fdi") && this.verifyFinallyEx(controlFlowGraph, (CatchAllStatement)statement2, objectArray)) {
                                this.finallyBlockIDs.put(basicBlock2.id, null);
                            } else {
                                int n = DecompilerContext.getCountercontainer().getCounterAndIncrement(2);
                                this.insertSemaphore(controlFlowGraph, FinallyProcessor.getAllBasicBlocks(statement2.getFirst()), basicBlock, basicBlock2, n, objectArray);
                                this.finallyBlockIDs.put(basicBlock2.id, n);
                            }
                            DeadCodeHelper.removeDeadBlocks(controlFlowGraph);
                            DeadCodeHelper.removeEmptyBlocks(controlFlowGraph);
                            DeadCodeHelper.mergeBasicBlocks(controlFlowGraph);
                        }
                        return true;
                    }
                }
            }
            linkedList.addAll(statement.getStats());
        }
        return false;
    }

    private static Object[] getFinallyInformation(StructMethod object, RootStatement hashSet, CatchAllStatement catchAllStatement) {
        HashMap<BasicBlock, Boolean> hashMap = new HashMap<BasicBlock, Boolean>();
        Object object2 = catchAllStatement.getHandler().getBasichead();
        BasicBlock basicBlock = ((BasicBlockStatement)object2).getBlock();
        Object object3 = basicBlock.getInstruction();
        int n = 0;
        switch (((Instruction)object3).opcode) {
            case 87: {
                n = 1;
                break;
            }
            case 58: {
                n = 2;
            }
        }
        new ExprProcessor().processStatement((RootStatement)((Object)hashSet), ((StructMethod)object).getClassStruct().getPool());
        new SSAConstructorSparseEx().splitVariables((RootStatement)((Object)hashSet), (StructMethod)object);
        object = ((Statement)object2).getExprents();
        object = new VarVersionPaar((VarExprent)((AssignmentExprent)object.get(n == 2 ? 1 : 0)).getLeft());
        hashSet = new FlattenStatementsHelper().buildDirectGraph((RootStatement)((Object)hashSet));
        object2 = new LinkedList<DirectNode>();
        ((LinkedList)object2).add(((DirectGraph)hashSet).first);
        hashSet = new HashSet<Object>();
        while (!((AbstractCollection)object2).isEmpty()) {
            object3 = (DirectNode)((LinkedList)object2).removeFirst();
            if (hashSet.contains(object3)) continue;
            hashSet.add(object3);
            BasicBlockStatement basicBlockStatement = null;
            if (((DirectNode)object3).block != null) {
                basicBlockStatement = ((DirectNode)object3).block;
            } else if (((DirectNode)object3).preds.size() == 1) {
                basicBlockStatement = ((DirectNode)object3.preds.get((int)0)).block;
            }
            boolean bl = true;
            if (n != 1) {
                bl = false;
                int n2 = 0;
                while (n2 < ((DirectNode)object3).exprents.size()) {
                    Object object4;
                    Object object5;
                    Object object6 = (Exprent)((DirectNode)object3).exprents.get(n2);
                    if (n == 0) {
                        Exprent exprent;
                        object5 = ((Exprent)object6).getAllExprents();
                        object5.add(object6);
                        boolean bl2 = false;
                        object4 = object5.iterator();
                        while (object4.hasNext()) {
                            exprent = (Exprent)object4.next();
                            if (exprent.type != 12 || !new VarVersionPaar((VarExprent)exprent).equals(object)) continue;
                            bl2 = true;
                            break;
                        }
                        if (bl2) {
                            bl2 = false;
                            if (((Exprent)object6).type == 4 && ((ExitExprent)(exprent = (ExitExprent)object6)).getExittype() == 1 && exprent.getValue().type == 12) {
                                bl2 = true;
                            }
                            if (!bl2) {
                                return null;
                            }
                            bl = true;
                        }
                    } else if (n == 2 && ((Exprent)object6).type == 2) {
                        object5 = (AssignmentExprent)object6;
                        if (object5.getRight().type == 12 && new VarVersionPaar((VarExprent)((AssignmentExprent)object5).getRight()).equals(object)) {
                            Exprent exprent = null;
                            if (n2 == ((DirectNode)object3).exprents.size() - 1) {
                                if (((DirectNode)object3).succs.size() == 1) {
                                    DirectNode directNode = (DirectNode)((DirectNode)object3).succs.get(0);
                                    if (!directNode.exprents.isEmpty()) {
                                        exprent = (Exprent)directNode.exprents.get(0);
                                    }
                                }
                            } else {
                                exprent = (Exprent)((DirectNode)object3).exprents.get(n2 + 1);
                            }
                            boolean bl3 = false;
                            if (exprent != null && exprent.type == 4 && ((ExitExprent)(object4 = (ExitExprent)exprent)).getExittype() == 1 && object4.getValue().type == 12 && ((AssignmentExprent)object5).getLeft().equals(((ExitExprent)object4).getValue())) {
                                bl3 = true;
                            }
                            if (!bl3) {
                                return null;
                            }
                            bl = true;
                        }
                    }
                    ++n2;
                }
            }
            if (basicBlockStatement != null && basicBlockStatement.getBlock() != null) {
                Statement statement = catchAllStatement.getHandler();
                for (Object object6 : basicBlockStatement.getSuccessorEdges(0x40000000)) {
                    Boolean bl4;
                    if (((StatEdge)object6).getType() == 1 || !statement.containsStatement(basicBlockStatement) || statement.containsStatement(((StatEdge)object6).getDestination()) || (bl4 = (Boolean)hashMap.get(basicBlockStatement.getBlock())) != null && bl4.booleanValue()) continue;
                    hashMap.put(basicBlockStatement.getBlock(), bl);
                    break;
                }
            }
            ((LinkedList)object2).addAll(((DirectNode)object3).succs);
        }
        if (catchAllStatement.getHandler().type == 8) {
            boolean bl = false;
            boolean bl5 = hashMap.containsKey(basicBlock);
            InstructionSequence instructionSequence = basicBlock.getSeq();
            switch (n) {
                case 0: {
                    bl = bl5 && instructionSequence.length() == 1;
                    break;
                }
                case 1: {
                    bl = instructionSequence.length() == 1;
                    break;
                }
                case 2: {
                    boolean bl6;
                    if (bl5) {
                        if (instructionSequence.length() == 3) {
                            bl6 = true;
                            break;
                        }
                        bl6 = false;
                        break;
                    }
                    bl6 = bl = instructionSequence.length() == 1;
                }
            }
            if (bl) {
                n = 3;
            }
        }
        return new Object[]{n, hashMap};
    }

    /*
     * WARNING - void declaration
     */
    private void insertSemaphore(ControlFlowGraph controlFlowGraph, HashSet object52, BasicBlock basicBlock, BasicBlock object22, int n, Object[] object3) {
        void var5_13;
        Object object;
        BasicBlock basicBlock2;
        Object object2;
        HashSet hashSet = new HashSet(object52);
        int n2 = (Integer)object2[0];
        object2 = (HashMap)object2[1];
        FinallyProcessor.removeExceptionInstructionsEx((BasicBlock)object22, 1, n2);
        for (Map.Entry entry : ((HashMap)object2).entrySet()) {
            basicBlock2 = (BasicBlock)entry.getKey();
            if (!((Boolean)entry.getValue()).booleanValue()) continue;
            FinallyProcessor.removeExceptionInstructionsEx(basicBlock2, 2, n2);
            controlFlowGraph.getFinallyExits().add(basicBlock2);
        }
        object2 = ((HashSet)object52).iterator();
        while (object2.hasNext()) {
            BasicBlock basicBlock3 = (BasicBlock)object2.next();
            basicBlock2 = null;
            for (BasicBlock basicBlock32 : basicBlock3.getSuccs()) {
                if (hashSet.contains(basicBlock32) || basicBlock32 == controlFlowGraph.getLast()) continue;
                object = new SimpleInstructionSequence();
                ((InstructionSequence)object).addInstruction(ConstantsUtil.getInstructionInstance(16, false, 1, new int[1]), -1);
                ((InstructionSequence)object).addInstruction(ConstantsUtil.getInstructionInstance(54, false, 1, new int[]{var5_13}), -1);
                basicBlock2 = new BasicBlock(++controlFlowGraph.last_id);
                basicBlock2.setSeq((InstructionSequence)object);
                basicBlock3.replaceSuccessor(basicBlock32, basicBlock2);
                basicBlock2.addSuccessor(basicBlock32);
                hashSet.add(basicBlock2);
                controlFlowGraph.getBlocks().addWithKey(basicBlock2, basicBlock2.id);
                int n3 = 0;
                while (n3 < basicBlock3.getSuccExceptions().size()) {
                    object = (BasicBlock)basicBlock3.getSuccExceptions().get(n3);
                    ExceptionRangeCFG exceptionRangeCFG = controlFlowGraph.getExceptionRange((BasicBlock)object, basicBlock3);
                    basicBlock2.addSuccessorException((BasicBlock)object);
                    exceptionRangeCFG.getProtectedRange().add(basicBlock2);
                    ++n3;
                }
            }
        }
        SimpleInstructionSequence simpleInstructionSequence = new SimpleInstructionSequence();
        simpleInstructionSequence.addInstruction(ConstantsUtil.getInstructionInstance(16, false, 1, new int[]{1}), -1);
        simpleInstructionSequence.addInstruction(ConstantsUtil.getInstructionInstance(54, false, 1, new int[]{var5_13}), -1);
        object2 = new BasicBlock(++controlFlowGraph.last_id);
        ((BasicBlock)object2).setSeq(simpleInstructionSequence);
        FinallyProcessor.insertBlockBefore(controlFlowGraph, basicBlock, (BasicBlock)object2);
        SimpleInstructionSequence simpleInstructionSequence2 = new SimpleInstructionSequence();
        simpleInstructionSequence2.addInstruction(ConstantsUtil.getInstructionInstance(16, false, 1, new int[1]), -1);
        simpleInstructionSequence2.addInstruction(ConstantsUtil.getInstructionInstance(54, false, 1, new int[]{var5_13}), -1);
        basicBlock2 = new BasicBlock(++controlFlowGraph.last_id);
        basicBlock2.setSeq(simpleInstructionSequence2);
        FinallyProcessor.insertBlockBefore(controlFlowGraph, (BasicBlock)object2, basicBlock2);
        hashSet.add(object2);
        hashSet.add(basicBlock2);
        for (Object object52 : new HashSet(basicBlock2.getSuccExceptions())) {
            object = controlFlowGraph.getExceptionRange((BasicBlock)object52, basicBlock2);
            if (!hashSet.containsAll(((ExceptionRangeCFG)object).getProtectedRange())) continue;
            basicBlock2.removeSuccessorException((BasicBlock)object52);
            ((ExceptionRangeCFG)object).getProtectedRange().remove(basicBlock2);
        }
    }

    private static void insertBlockBefore(ControlFlowGraph controlFlowGraph, BasicBlock basicBlock, BasicBlock basicBlock2) {
        Object object2 = new ArrayList();
        object2.addAll(basicBlock.getPreds());
        object2.addAll(basicBlock.getPredExceptions());
        Iterator iterator = object2.iterator();
        while (iterator.hasNext()) {
            ((BasicBlock)iterator.next()).replaceSuccessor(basicBlock, basicBlock2);
        }
        for (Object object2 : basicBlock.getSuccExceptions()) {
            ExceptionRangeCFG exceptionRangeCFG = controlFlowGraph.getExceptionRange((BasicBlock)object2, basicBlock);
            basicBlock2.addSuccessorException((BasicBlock)object2);
            exceptionRangeCFG.getProtectedRange().add(basicBlock2);
        }
        for (Object object2 : controlFlowGraph.getExceptions()) {
            if (((ExceptionRangeCFG)object2).getHandler() != basicBlock) continue;
            ((ExceptionRangeCFG)object2).setHandler(basicBlock2);
        }
        basicBlock2.addSuccessor(basicBlock);
        controlFlowGraph.getBlocks().addWithKey(basicBlock2, basicBlock2.id);
        if (controlFlowGraph.getFirst() == basicBlock) {
            controlFlowGraph.setFirst(basicBlock2);
        }
    }

    private static HashSet getAllBasicBlocks(Statement statement) {
        Object object;
        Object object2 = new LinkedList<Statement>();
        object2.add(statement);
        int n = 0;
        do {
            object = (Statement)object2.get(n);
            if (((Statement)object).type == 8) {
                ++n;
                continue;
            }
            object2.addAll(((Statement)object).getStats());
            object2.remove(n);
        } while (n < object2.size());
        object = new HashSet();
        object2 = object2.iterator();
        while (object2.hasNext()) {
            Statement statement2 = (Statement)object2.next();
            ((HashSet)object).add(((BasicBlockStatement)statement2).getBlock());
        }
        return object;
    }

    private boolean verifyFinallyEx(ControlFlowGraph controlFlowGraph, CatchAllStatement catchAllStatement, Object[] object) {
        Object object2;
        Object object3;
        Iterator<Object> iterator = FinallyProcessor.getAllBasicBlocks(catchAllStatement.getFirst());
        HashSet hashSet = FinallyProcessor.getAllBasicBlocks(catchAllStatement.getHandler());
        int n = (Integer)object[0];
        object = (HashMap)object[1];
        Object object4 = catchAllStatement.getHandler().getBasichead().getBlock();
        boolean bl = false;
        if (n == 3) {
            FinallyProcessor.removeExceptionInstructionsEx((BasicBlock)object4, 3, n);
            if (((HashMap)object).containsKey(object4)) {
                controlFlowGraph.getFinallyExits().add(object4);
            }
            return true;
        }
        if (((BasicBlock)object4).getSeq().length() == 1 && n > 0 && hashSet.contains(object3 = (BasicBlock)((BasicBlock)object4).getSuccs().get(0))) {
            object4 = object3;
            bl = true;
        }
        object3 = new HashSet();
        Iterator object52 = ((HashSet)((Object)iterator)).iterator();
        while (object52.hasNext()) {
            object2 = (BasicBlock)object52.next();
            object3.addAll(((BasicBlock)object2).getSuccs());
        }
        object3.remove(controlFlowGraph.getLast());
        object3.removeAll((Collection<?>)((Object)iterator));
        object2 = new ArrayList();
        for (BasicBlock basicBlock : object3) {
            object3 = this.compareSubgraphsEx(controlFlowGraph, basicBlock, hashSet, (BasicBlock)object4, n, (HashMap)object, bl);
            if (object3 == null) {
                return false;
            }
            object2.add(new Object[]{basicBlock, object3[0], object3[1]});
        }
        iterator = object2.iterator();
        while (iterator.hasNext()) {
            Object[] objectArray = (Object[])iterator.next();
            FinallyProcessor.deleteArea(controlFlowGraph, objectArray);
        }
        for (Map.Entry entry : ((HashMap)object).entrySet()) {
            object3 = (BasicBlock)entry.getKey();
            if (!((Boolean)entry.getValue()).booleanValue()) continue;
            FinallyProcessor.removeExceptionInstructionsEx((BasicBlock)object3, 2, n);
            controlFlowGraph.getFinallyExits().add(object3);
        }
        FinallyProcessor.removeExceptionInstructionsEx(catchAllStatement.getHandler().getBasichead().getBlock(), 1, n);
        return true;
    }

    private Object[] compareSubgraphsEx(ControlFlowGraph controlFlowGraph, BasicBlock object, HashSet hashSet, BasicBlock basicBlock, int n, HashMap hashMap, boolean bl) {
        LinkedList<FinallyProcessor$1BlockStackEntry> linkedList = new LinkedList<FinallyProcessor$1BlockStackEntry>();
        HashSet<BasicBlock> hashSet2 = new HashSet<BasicBlock>();
        HashMap<String, BasicBlock[]> hashMap2 = new HashMap<String, BasicBlock[]>();
        linkedList.add(new FinallyProcessor$1BlockStackEntry(basicBlock, (BasicBlock)object, new ArrayList()));
        while (!linkedList.isEmpty()) {
            Object object2;
            boolean bl2;
            boolean bl3;
            object = (FinallyProcessor$1BlockStackEntry)linkedList.remove(0);
            BasicBlock basicBlock2 = ((FinallyProcessor$1BlockStackEntry)object).blockCatch;
            BasicBlock basicBlock3 = ((FinallyProcessor$1BlockStackEntry)object).blockSample;
            int n2 = !bl && basicBlock2 == basicBlock ? 1 : 0;
            if (!this.compareBasicBlocksEx(controlFlowGraph, basicBlock2, basicBlock3, (n2 != 0 ? 1 : 0) | ((bl3 = (bl2 = hashMap.containsKey(basicBlock2)) && (Boolean)hashMap.get(basicBlock2) != false) ? 2 : 0), n, ((FinallyProcessor$1BlockStackEntry)object).lstStoreVars)) {
                return null;
            }
            if (basicBlock3.getSuccs().size() != basicBlock2.getSuccs().size()) {
                return null;
            }
            hashSet2.add(basicBlock3);
            n2 = 0;
            while (n2 < basicBlock2.getSuccs().size()) {
                BasicBlock basicBlock4 = (BasicBlock)basicBlock2.getSuccs().get(n2);
                object2 = (BasicBlock)basicBlock3.getSuccs().get(n2);
                if (hashSet.contains(basicBlock4) && !hashSet2.contains(object2)) {
                    linkedList.add(new FinallyProcessor$1BlockStackEntry(basicBlock4, (BasicBlock)object2, ((FinallyProcessor$1BlockStackEntry)object).lstStoreVars));
                }
                ++n2;
            }
            if (!bl2 || !basicBlock3.getSeq().isEmpty()) {
                if (basicBlock2.getSuccExceptions().size() == basicBlock3.getSuccExceptions().size()) {
                    n2 = 0;
                    while (n2 < basicBlock2.getSuccExceptions().size()) {
                        BasicBlock basicBlock5 = (BasicBlock)basicBlock2.getSuccExceptions().get(n2);
                        object2 = (BasicBlock)basicBlock3.getSuccExceptions().get(n2);
                        String string = controlFlowGraph.getExceptionRange(basicBlock5, basicBlock2).getExceptionType();
                        Object object3 = controlFlowGraph.getExceptionRange((BasicBlock)object2, basicBlock3).getExceptionType();
                        boolean bl4 = string == null ? object3 == null : string.equals(object3);
                        boolean bl5 = false;
                        if (bl4) {
                            if (hashSet.contains(basicBlock5) && !hashSet2.contains(object2)) {
                                ArrayList<int[]> arrayList = ((FinallyProcessor$1BlockStackEntry)object).lstStoreVars;
                                if (basicBlock5.getSeq().length() > 0 && ((BasicBlock)object2).getSeq().length() > 0) {
                                    object3 = basicBlock5.getSeq().getInstr(0);
                                    Instruction instruction = ((BasicBlock)object2).getSeq().getInstr(0);
                                    if (((Instruction)object3).opcode == 58 && instruction.opcode == 58) {
                                        arrayList = new ArrayList<int[]>(arrayList);
                                        arrayList.add(new int[]{((Instruction)object3).getOperand(0), instruction.getOperand(0)});
                                    }
                                }
                                linkedList.add(new FinallyProcessor$1BlockStackEntry(basicBlock5, (BasicBlock)object2, arrayList));
                            }
                        } else {
                            return null;
                        }
                        ++n2;
                    }
                } else {
                    return null;
                }
            }
            if (!bl2) continue;
            HashSet hashSet3 = new HashSet(basicBlock3.getSuccs());
            hashSet3.removeAll(hashSet2);
            for (FinallyProcessor$1BlockStackEntry finallyProcessor$1BlockStackEntry : linkedList) {
                hashSet3.remove(finallyProcessor$1BlockStackEntry.blockSample);
            }
            for (BasicBlock basicBlock6 : hashSet3) {
                if (controlFlowGraph.getLast() == basicBlock6) continue;
                hashMap2.put(String.valueOf(basicBlock3.id) + "#" + basicBlock6.id, new BasicBlock[]{basicBlock3, basicBlock6, bl3 ? basicBlock6 : null});
            }
        }
        return new Object[]{hashSet2, FinallyProcessor.getUniqueNext(controlFlowGraph, new HashSet(hashMap2.values()))};
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static BasicBlock getUniqueNext(ControlFlowGraph controlFlowGraph, HashSet hashSet) {
        BasicBlock basicBlock = null;
        boolean bl = false;
        for (Object object : hashSet) {
            if (object[2] != null) {
                return object[1];
            }
            if (basicBlock == null) {
                basicBlock = object[1];
            } else if (basicBlock != object[1]) {
                bl = true;
            }
            if (object[1].getPreds().size() != 1) continue;
            basicBlock = object[1];
        }
        if (!bl) return basicBlock;
        Iterator iterator = hashSet.iterator();
        while (iterator.hasNext()) {
            boolean bl2;
            Object object;
            object = null;
            Object object2 = ((BasicBlock[])iterator.next())[1];
            if (object2 == basicBlock) continue;
            List list = ((BasicBlock)object2).getSuccs();
            object = basicBlock.getSuccs();
            if (object == null) {
                if (list != null) return null;
                bl2 = true;
            } else {
                if (list == null) return null;
                if (object.size() != list.size()) return null;
                object = new HashSet(object);
                ((AbstractSet)object).removeAll(list);
                if (((HashSet)object).size() != 0) return null;
                bl2 = true;
            }
            if (!bl2) return null;
            object = basicBlock.getSeq();
            object2 = ((BasicBlock)object2).getSeq();
            if (((InstructionSequence)object).length() != ((InstructionSequence)object2).length()) return null;
            int n = 0;
            while (n < ((InstructionSequence)object).length()) {
                Instruction instruction = ((InstructionSequence)object).getInstr(n);
                Instruction instruction2 = ((InstructionSequence)object2).getInstr(n);
                if (instruction.opcode != instruction2.opcode) return null;
                if (instruction.wide != instruction2.wide) return null;
                if (instruction.operandsCount() != instruction2.operandsCount()) {
                    return null;
                }
                int n2 = 0;
                while (n < instruction.getOperands().length) {
                    if (instruction.getOperand(n2) != instruction2.getOperand(n2)) {
                        return null;
                    }
                    ++n2;
                }
                ++n;
            }
        }
        for (Object object : hashSet) {
            if (object[1] == basicBlock) continue;
            object[0].removeSuccessor(object[1]);
            ((BasicBlock)object[0]).addSuccessor(basicBlock);
        }
        DeadCodeHelper.removeDeadBlocks(controlFlowGraph);
        return basicBlock;
    }

    private boolean compareBasicBlocksEx(ControlFlowGraph controlFlowGraph, BasicBlock iterator, BasicBlock basicBlock, int n, int n2, List list) {
        Iterator iterator2;
        Object object = ((BasicBlock)((Object)iterator)).getSeq();
        iterator = basicBlock.getSeq();
        if (n != 0) {
            object = ((InstructionSequence)object).clone();
            if ((n & 1) > 0 && n2 > 0) {
                ((InstructionSequence)object).removeInstruction(0);
            }
            if ((n & 2) > 0) {
                if (n2 == 0 || n2 == 2) {
                    ((InstructionSequence)object).removeInstruction(((InstructionSequence)object).length() - 1);
                }
                if (n2 == 2) {
                    ((InstructionSequence)object).removeInstruction(((InstructionSequence)object).length() - 1);
                }
            }
        }
        if (((InstructionSequence)object).length() > ((InstructionSequence)((Object)iterator)).length()) {
            return false;
        }
        n = 0;
        while (n < ((InstructionSequence)object).length()) {
            boolean bl;
            block20: {
                Object object2 = ((InstructionSequence)object).getInstr(n);
                iterator2 = ((InstructionSequence)((Object)iterator)).getInstr(n);
                List list2 = list;
                if (((Instruction)object2).opcode != ((Instruction)iterator2).opcode || ((Instruction)object2).wide != ((Instruction)iterator2).wide || ((Instruction)object2).operandsCount() != ((Instruction)((Object)iterator2)).operandsCount()) {
                    bl = false;
                } else {
                    if (((Instruction)object2).group != 2 && ((Instruction)object2).getOperands() != null) {
                        int n3 = 0;
                        block1: while (n3 < ((Instruction)object2).getOperands().length) {
                            int n4;
                            int n5 = ((Instruction)object2).getOperand(n3);
                            if (n5 != (n4 = ((Instruction)((Object)iterator2)).getOperand(n3))) {
                                if (((Instruction)object2).opcode == 25 || ((Instruction)object2).opcode == 58) {
                                    for (int[] nArray : list2) {
                                        object2 = nArray;
                                        if (nArray[0] == n5 && object2[1] == n4) break block1;
                                    }
                                }
                                bl = false;
                                break block20;
                            }
                            ++n3;
                        }
                    }
                    bl = true;
                }
            }
            if (!bl) {
                return false;
            }
            ++n;
        }
        if (((InstructionSequence)object).length() < ((InstructionSequence)((Object)iterator)).length()) {
            Object object3 = new SimpleInstructionSequence();
            n2 = ((InstructionSequence)((Object)iterator)).length() - 1;
            while (n2 >= ((InstructionSequence)object).length()) {
                ((InstructionSequence)object3).a(((InstructionSequence)((Object)iterator)).getInstr(n2));
                ((InstructionSequence)((Object)iterator)).removeInstruction(n2);
                --n2;
            }
            BasicBlock basicBlock2 = new BasicBlock(++controlFlowGraph.last_id);
            basicBlock2.setSeq((InstructionSequence)object3);
            iterator2 = new ArrayList();
            iterator2.addAll(basicBlock.getSuccs());
            iterator = iterator2.iterator();
            while (iterator.hasNext()) {
                object = (BasicBlock)iterator.next();
                basicBlock.removeSuccessor((BasicBlock)object);
                basicBlock2.addSuccessor((BasicBlock)object);
            }
            basicBlock.addSuccessor(basicBlock2);
            controlFlowGraph.getBlocks().addWithKey(basicBlock2, basicBlock2.id);
            object = controlFlowGraph.getFinallyExits();
            if (((HashSet)object).contains(basicBlock)) {
                ((HashSet)object).remove(basicBlock);
                ((HashSet)object).add(basicBlock2);
            }
            int n6 = 0;
            while (n6 < basicBlock.getSuccExceptions().size()) {
                object = (BasicBlock)basicBlock.getSuccExceptions().get(n6);
                object3 = controlFlowGraph.getExceptionRange((BasicBlock)object, basicBlock);
                basicBlock2.addSuccessorException((BasicBlock)object);
                ((ExceptionRangeCFG)object3).getProtectedRange().add(basicBlock2);
                ++n6;
            }
        }
        return true;
    }

    private static void deleteArea(ControlFlowGraph controlFlowGraph, Object[] object) {
        HashSet hashSet;
        BasicBlock basicBlock = (BasicBlock)object[0];
        BasicBlock basicBlock22 = (BasicBlock)object[2];
        if (basicBlock == basicBlock22) {
            return;
        }
        if (basicBlock22 == null) {
            basicBlock22 = controlFlowGraph.getLast();
        }
        Object object22 = new ArrayList(basicBlock.getPreds()).iterator();
        while (object22.hasNext()) {
            hashSet = null;
            ((BasicBlock)object22.next()).replaceSuccessor(basicBlock, basicBlock22);
        }
        hashSet = (HashSet)object[1];
        for (Object object22 : hashSet) {
            if (!controlFlowGraph.getBlocks().containsKey(((BasicBlock)object22).id)) continue;
            if (((BasicBlock)object22).getSeq().isEmpty() && ((BasicBlock)object22).getSuccs().size() == 1) {
                basicBlock = (BasicBlock)((BasicBlock)object22).getSuccs().get(0);
                for (BasicBlock basicBlock22 : new ArrayList(((BasicBlock)object22).getPreds())) {
                    if (hashSet.contains(basicBlock22)) continue;
                    basicBlock22.replaceSuccessor((BasicBlock)object22, basicBlock);
                }
                if (controlFlowGraph.getFirst() == object22) {
                    controlFlowGraph.setFirst(basicBlock);
                }
            }
            controlFlowGraph.removeBlock((BasicBlock)object22);
        }
    }

    private static void removeExceptionInstructionsEx(BasicBlock object, int n, int n2) {
        object = ((BasicBlock)object).getSeq();
        if (n2 == 3) {
            n = ((InstructionSequence)object).length() - 1;
            while (n >= 0) {
                ((InstructionSequence)object).removeInstruction(n);
                --n;
            }
            return;
        }
        if ((n & 1) > 0 && (n2 == 2 || n2 == 1)) {
            ((InstructionSequence)object).removeInstruction(0);
        }
        if ((n & 2) > 0) {
            if (n2 == 2 || n2 == 0) {
                ((InstructionSequence)object).removeInstruction(((InstructionSequence)object).length() - 1);
            }
            if (n2 == 2) {
                ((InstructionSequence)object).removeInstruction(((InstructionSequence)object).length() - 1);
            }
        }
    }
}

