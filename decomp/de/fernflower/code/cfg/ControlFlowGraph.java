/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code.cfg;

import de.fernflower.bx;
import de.fernflower.code.ExceptionHandler;
import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.JumpInstruction;
import de.fernflower.code.SimpleInstructionSequence;
import de.fernflower.code.SwitchInstruction;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ExceptionRangeCFG;
import de.fernflower.code.interpreter.InstructionImpact;
import de.fernflower.modules.code.DeadCodeHelper;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.gen.DataPoint;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.ListStack;
import de.fernflower.util.VBStyleCollection;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ControlFlowGraph
implements bx {
    public int last_id = 0;
    private VBStyleCollection blocks;
    private BasicBlock first;
    private BasicBlock last;
    private List exceptions;
    private HashMap subroutines;
    private HashSet finallyExits = new HashSet();

    public ControlFlowGraph(InstructionSequence instructionSequence) {
        Object object = ControlFlowGraph.findStartInstructions(instructionSequence);
        HashMap hashMap = new HashMap();
        object = this.createBasicBlocks((short[])object, instructionSequence, hashMap);
        this.blocks = object;
        ControlFlowGraph.connectBlocks((List)object, hashMap);
        this.setExceptionEdges(instructionSequence, hashMap);
        this.setSubroutineEdges();
        this.setFirstAndLastBlocks();
    }

    private void removeMarkers() {
        Iterator iterator = ((ControlFlowGraph)iterator).blocks.iterator();
        while (iterator.hasNext()) {
            ((BasicBlock)iterator.next()).mark = 0;
        }
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (BasicBlock basicBlock : this.blocks) {
            stringBuffer.append("----- Block " + basicBlock.id + " -----\r\n");
            stringBuffer.append(basicBlock.toString());
            stringBuffer.append("----- Edges -----\r\n");
            List list = basicBlock.getSuccs();
            int n = 0;
            while (n < list.size()) {
                stringBuffer.append(">>>>>>>>(regular) Block " + ((BasicBlock)list.get((int)n)).id + "\r\n");
                ++n;
            }
            list = basicBlock.getSuccExceptions();
            n = 0;
            while (n < list.size()) {
                BasicBlock basicBlock2 = (BasicBlock)list.get(n);
                ExceptionRangeCFG exceptionRangeCFG = this.getExceptionRange(basicBlock2, basicBlock);
                stringBuffer.append(">>>>>>>>(exception) Block " + basicBlock2.id + "\t" + (exceptionRangeCFG == null ? "ERROR: range not found!" : exceptionRangeCFG.getExceptionType()) + "\r\n");
                ++n;
            }
            stringBuffer.append("----- ----- -----\r\n");
        }
        return stringBuffer.toString();
    }

    public final void inlineJsr(StructMethod structMethod) {
        bx bx2;
        boolean bl;
        ControlFlowGraph controlFlowGraph = this;
        block0: do {
            Object object;
            Object[] objectArray;
            bx2 = controlFlowGraph;
            ArrayList<Object[]> arrayList = new ArrayList<Object[]>();
            for (Map.Entry entry : ((ControlFlowGraph)bx2).subroutines.entrySet()) {
                objectArray = (BasicBlock)entry.getKey();
                object = (BasicBlock)entry.getValue();
                arrayList.add(new Object[]{objectArray, ((ControlFlowGraph)bx2).getJsrRange((BasicBlock)objectArray, (BasicBlock)object), object});
            }
            ArrayList<Object[]> object22 = new ArrayList<Object[]>();
            for (Object[] objectArray2 : arrayList) {
                int n = 0;
                while (n < object22.size()) {
                    if (((HashSet)((Object[])object22.get(n))[1]).contains(objectArray2[0])) break;
                    ++n;
                }
                object22.add(n, objectArray2);
            }
            int n = 0;
            while (n < object22.size()) {
                objectArray = (Object[])object22.get(n);
                object = (HashSet)objectArray[1];
                int n2 = n + 1;
                while (n2 < object22.size()) {
                    Object object2 = (Object[])object22.get(n2);
                    HashSet hashSet = (HashSet)object2[1];
                    if (!((HashSet)object).contains(object2[0]) && !hashSet.contains(objectArray[0])) {
                        object2 = new HashSet(object);
                        ((AbstractCollection)object2).retainAll(hashSet);
                        if (!((HashSet)object2).isEmpty()) {
                            super.splitJsrRange((BasicBlock)objectArray[0], (BasicBlock)objectArray[2], (HashSet)object2);
                            bl = true;
                            continue block0;
                        }
                    }
                    ++n2;
                }
                ++n;
            }
            bl = false;
        } while (bl);
        bx2 = structMethod;
        controlFlowGraph = this;
        controlFlowGraph.removeJsrInstructions(((StructMethod)bx2).getClassStruct().getPool(), controlFlowGraph.first, DataPoint.getInitialDataPoint((StructMethod)bx2));
        this.removeMarkers();
        DeadCodeHelper.removeEmptyBlocks(this);
    }

    public final void removeBlock(BasicBlock basicBlock) {
        Map.Entry entry;
        while (basicBlock.getSuccs().size() > 0) {
            basicBlock.removeSuccessor((BasicBlock)basicBlock.getSuccs().get(0));
        }
        while (basicBlock.getSuccExceptions().size() > 0) {
            basicBlock.removeSuccessorException((BasicBlock)basicBlock.getSuccExceptions().get(0));
        }
        while (basicBlock.getPreds().size() > 0) {
            ((BasicBlock)basicBlock.getPreds().get(0)).removeSuccessor(basicBlock);
        }
        while (basicBlock.getPredExceptions().size() > 0) {
            ((BasicBlock)basicBlock.getPredExceptions().get(0)).removeSuccessorException(basicBlock);
        }
        this.last.removePredecessor(basicBlock);
        this.blocks.removeWithKey(basicBlock.id);
        int n = this.exceptions.size() - 1;
        while (n >= 0) {
            entry = (ExceptionRangeCFG)this.exceptions.get(n);
            if (((ExceptionRangeCFG)((Object)entry)).getHandler() == basicBlock) {
                this.exceptions.remove(n);
            } else {
                entry = ((ExceptionRangeCFG)((Object)entry)).getProtectedRange();
                entry.remove(basicBlock);
                if (entry.isEmpty()) {
                    this.exceptions.remove(n);
                }
            }
            --n;
        }
        Iterator iterator = this.subroutines.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.getKey() != basicBlock && entry.getValue() != basicBlock) continue;
            iterator.remove();
        }
    }

    public final ExceptionRangeCFG getExceptionRange(BasicBlock basicBlock, BasicBlock basicBlock2) {
        int n = this.exceptions.size() - 1;
        while (n >= 0) {
            ExceptionRangeCFG exceptionRangeCFG = (ExceptionRangeCFG)this.exceptions.get(n);
            if (exceptionRangeCFG.getHandler() == basicBlock && exceptionRangeCFG.getProtectedRange().contains(basicBlock2)) {
                return exceptionRangeCFG;
            }
            --n;
        }
        return null;
    }

    private static short[] findStartInstructions(InstructionSequence instructionSequence) {
        int n = instructionSequence.length();
        short[] sArray = new short[n];
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (ExceptionHandler exceptionHandler : instructionSequence.getExceptionTable().getHandlers()) {
            hashSet.add(exceptionHandler.from_instr);
            hashSet.add(exceptionHandler.to_instr);
            hashSet.add(exceptionHandler.handler_instr);
        }
        int n2 = 0;
        while (n2 < n) {
            if (hashSet.contains(new Integer(n2))) {
                sArray[n2] = 1;
            }
            Object object = instructionSequence.getInstr(n2);
            switch (((Instruction)object).group) {
                case 2: {
                    sArray[((JumpInstruction)object).destination] = 1;
                }
                case 6: {
                    if (n2 + 1 >= n) break;
                    sArray[n2 + 1] = 1;
                    break;
                }
                case 3: {
                    object = (SwitchInstruction)object;
                    int[] nArray = ((SwitchInstruction)object).getDestinations();
                    int n3 = nArray.length - 1;
                    while (n3 >= 0) {
                        sArray[nArray[n3]] = 1;
                        --n3;
                    }
                    sArray[object.getDefaultdest()] = 1;
                    if (n2 + 1 >= n) break;
                    sArray[n2 + 1] = 1;
                }
            }
            ++n2;
        }
        sArray[0] = 1;
        return sArray;
    }

    private VBStyleCollection createBasicBlocks(short[] sArray, InstructionSequence instructionSequence, HashMap hashMap) {
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        SimpleInstructionSequence simpleInstructionSequence = null;
        ArrayList<Integer> arrayList = null;
        int n = sArray.length;
        int n2 = 0;
        int n3 = 0;
        BasicBlock basicBlock = null;
        int n4 = 0;
        while (n4 < n) {
            if (sArray[n4] == 1) {
                basicBlock = new BasicBlock();
                short s = (short)(n2 + 1);
                n2 = s;
                new BasicBlock().id = s;
                simpleInstructionSequence = new SimpleInstructionSequence();
                arrayList = new ArrayList<Integer>();
                basicBlock.setSeq(simpleInstructionSequence);
                basicBlock.setInstrOldOffsets(arrayList);
                vBStyleCollection.addWithKey(basicBlock, basicBlock.id);
                n3 = instructionSequence.getOffset(n4);
            }
            sArray[n4] = n2;
            hashMap.put(n4, basicBlock);
            simpleInstructionSequence.addInstruction(instructionSequence.getInstr(n4), instructionSequence.getOffset(n4) - n3);
            arrayList.add(instructionSequence.getOffset(n4));
            ++n4;
        }
        this.last_id = n2;
        return vBStyleCollection;
    }

    private static void connectBlocks(List list, HashMap hashMap) {
        int n = 0;
        while (n < list.size()) {
            BasicBlock basicBlock = (BasicBlock)list.get(n);
            Object object = basicBlock.getLastInstruction();
            Instruction instruction = object;
            boolean bl = ((Instruction)object).opcode != 167 && instruction.opcode != 200 && instruction.opcode != 169 && (instruction.opcode < 172 || instruction.opcode > 177) && instruction.opcode != 191 && instruction.opcode != 168 && instruction.opcode != 170 && instruction.opcode != 171;
            switch (((Instruction)object).group) {
                case 2: {
                    int n2 = ((JumpInstruction)object).destination;
                    object = (BasicBlock)hashMap.get(n2);
                    basicBlock.addSuccessor((BasicBlock)object);
                    break;
                }
                case 3: {
                    int[] nArray = null;
                    nArray = ((SwitchInstruction)object).getDestinations();
                    object = (BasicBlock)hashMap.get(((SwitchInstruction)object).getDefaultdest());
                    basicBlock.addSuccessor((BasicBlock)object);
                    int n3 = 0;
                    while (n3 < nArray.length) {
                        object = (BasicBlock)hashMap.get(nArray[n3]);
                        basicBlock.addSuccessor((BasicBlock)object);
                        ++n3;
                    }
                    break;
                }
            }
            if (bl && n < list.size() - 1) {
                object = (BasicBlock)list.get(n + 1);
                basicBlock.addSuccessor((BasicBlock)object);
            }
            ++n;
        }
    }

    private void setExceptionEdges(InstructionSequence object2, HashMap hashMap) {
        this.exceptions = new ArrayList();
        for (Object object2 : ((InstructionSequence)object2).getExceptionTable().getHandlers()) {
            BasicBlock basicBlock = (BasicBlock)hashMap.get(((ExceptionHandler)object2).from_instr);
            BasicBlock basicBlock2 = (BasicBlock)hashMap.get(((ExceptionHandler)object2).to_instr);
            BasicBlock basicBlock3 = (BasicBlock)hashMap.get(((ExceptionHandler)object2).handler_instr);
            ArrayList<BasicBlock> arrayList = new ArrayList<BasicBlock>();
            int n = basicBlock.id;
            while (n < basicBlock2.id) {
                BasicBlock basicBlock4 = (BasicBlock)this.blocks.getWithKey(n);
                arrayList.add(basicBlock4);
                basicBlock4.addSuccessorException(basicBlock3);
                ++n;
            }
            this.exceptions.add(new ExceptionRangeCFG(arrayList, basicBlock3, ((ExceptionHandler)object2).exceptionClass));
        }
    }

    private void setSubroutineEdges() {
        HashMap<BasicBlock, Object> hashMap = new HashMap<BasicBlock, Object>();
        for (BasicBlock basicBlock : this.blocks) {
            if (basicBlock.getSeq().getLastInstr().opcode != 168) continue;
            LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
            LinkedList linkedList2 = new LinkedList();
            HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
            linkedList.add(basicBlock);
            linkedList2.add(new LinkedList());
            while (!linkedList.isEmpty()) {
                basicBlock = (BasicBlock)linkedList.removeFirst();
                LinkedList linkedList3 = (LinkedList)linkedList2.removeFirst();
                hashSet.add(basicBlock);
                switch (basicBlock.getSeq().getLastInstr().opcode) {
                    case 168: {
                        linkedList3.add(basicBlock);
                        break;
                    }
                    case 169: {
                        BasicBlock basicBlock2 = (BasicBlock)linkedList3.getLast();
                        BasicBlock basicBlock3 = (BasicBlock)this.blocks.getWithKey(basicBlock2.id + 1);
                        if (basicBlock3 != null) {
                            if (!basicBlock.isSuccessor(basicBlock3)) {
                                basicBlock.addSuccessor(basicBlock3);
                            }
                            linkedList3.removeLast();
                            hashMap.put(basicBlock2, basicBlock3);
                            break;
                        }
                        throw new RuntimeException("ERROR: last instruction jsr");
                    }
                }
                if (linkedList3.isEmpty()) continue;
                for (BasicBlock basicBlock2 : basicBlock.getSuccs()) {
                    if (hashSet.contains(basicBlock2)) continue;
                    linkedList.add(basicBlock2);
                    linkedList2.add(new LinkedList(linkedList3));
                }
            }
        }
        this.subroutines = hashMap;
    }

    private HashSet getJsrRange(BasicBlock basicBlock, BasicBlock basicBlock2) {
        HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
        LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
        linkedList.add(basicBlock);
        BasicBlock basicBlock3 = (BasicBlock)basicBlock.getSuccs().get(0);
        while (!linkedList.isEmpty()) {
            BasicBlock basicBlock4 = (BasicBlock)linkedList.remove(0);
            int n = 0;
            while (n < 2) {
                block14: {
                    List list;
                    block15: {
                        block13: {
                            if (n != 0) break block13;
                            if (basicBlock4.getLastInstruction().opcode == 169 && basicBlock4.getSuccs().contains(basicBlock2)) break block14;
                            list = basicBlock4.getSuccs();
                            break block15;
                        }
                        if (basicBlock4 == basicBlock) break block14;
                        list = basicBlock4.getSuccExceptions();
                    }
                    int n2 = list.size() - 1;
                    while (n2 >= 0) {
                        block12: {
                            BasicBlock basicBlock5 = (BasicBlock)list.get(n2);
                            if (!hashSet.contains(basicBlock5)) {
                                if (basicBlock4 != basicBlock) {
                                    int n3 = 0;
                                    while (n3 < basicBlock5.getPreds().size()) {
                                        if (DeadCodeHelper.isDominator(this, (BasicBlock)basicBlock5.getPreds().get(n3), basicBlock3)) {
                                            ++n3;
                                            continue;
                                        }
                                        break block12;
                                    }
                                    n3 = 0;
                                    while (n3 < basicBlock5.getPredExceptions().size()) {
                                        if (DeadCodeHelper.isDominator(this, (BasicBlock)basicBlock5.getPredExceptions().get(n3), basicBlock3)) {
                                            ++n3;
                                            continue;
                                        }
                                        break block12;
                                    }
                                }
                                if (basicBlock5 != this.last) {
                                    hashSet.add(basicBlock5);
                                }
                                linkedList.add(basicBlock5);
                            }
                        }
                        --n2;
                    }
                }
                ++n;
            }
        }
        return hashSet;
    }

    private void splitJsrRange(BasicBlock basicBlock, BasicBlock basicBlock2, HashSet hashSet) {
        LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
        HashMap<Integer, BasicBlock> hashMap = new HashMap<Integer, BasicBlock>();
        linkedList.add(basicBlock);
        hashMap.put(basicBlock.id, basicBlock);
        while (!linkedList.isEmpty()) {
            BasicBlock basicBlock3 = (BasicBlock)linkedList.remove(0);
            int n = 0;
            while (n < 2) {
                block16: {
                    List list;
                    block17: {
                        block15: {
                            if (n != 0) break block15;
                            if (basicBlock3.getLastInstruction().opcode == 169 && basicBlock3.getSuccs().contains(basicBlock2)) break block16;
                            list = basicBlock3.getSuccs();
                            break block17;
                        }
                        if (basicBlock3 == basicBlock) break block16;
                        list = basicBlock3.getSuccExceptions();
                    }
                    int n2 = list.size() - 1;
                    while (n2 >= 0) {
                        BasicBlock basicBlock4 = (BasicBlock)list.get(n2);
                        Integer n3 = basicBlock4.id;
                        if (hashMap.containsKey(n3)) {
                            basicBlock3.replaceSuccessor(basicBlock4, (BasicBlock)hashMap.get(n3));
                        } else if (hashSet.contains(basicBlock4)) {
                            int n4;
                            BasicBlock basicBlock5 = (BasicBlock)basicBlock4.clone();
                            ((BasicBlock)basicBlock4.clone()).id = ++this.last_id;
                            if (basicBlock5.getLastInstruction().opcode == 169 && basicBlock4.getSuccs().contains(basicBlock2)) {
                                basicBlock5.addSuccessor(basicBlock2);
                                basicBlock4.removeSuccessor(basicBlock2);
                            } else {
                                n4 = 0;
                                while (n4 < basicBlock4.getSuccs().size()) {
                                    basicBlock5.addSuccessor((BasicBlock)basicBlock4.getSuccs().get(n4));
                                    ++n4;
                                }
                            }
                            n4 = 0;
                            while (n4 < basicBlock4.getSuccExceptions().size()) {
                                basicBlock5.addSuccessorException((BasicBlock)basicBlock4.getSuccExceptions().get(n4));
                                ++n4;
                            }
                            linkedList.add(basicBlock5);
                            hashMap.put(n3, basicBlock5);
                            if (this.last.getPreds().contains(basicBlock4)) {
                                this.last.addPredecessor(basicBlock5);
                            }
                            basicBlock3.replaceSuccessor(basicBlock4, basicBlock5);
                            this.blocks.addWithKey(basicBlock5, basicBlock5.id);
                        } else {
                            hashMap.put(n3, basicBlock4);
                        }
                        --n2;
                    }
                }
                ++n;
            }
        }
        this.splitJsrExceptionRanges(hashSet, hashMap);
    }

    private void splitJsrExceptionRanges(HashSet hashSet, HashMap hashMap) {
        int n = this.exceptions.size() - 1;
        while (n >= 0) {
            Object object = (ExceptionRangeCFG)this.exceptions.get(n);
            ArrayList<BasicBlock> arrayList = ((ExceptionRangeCFG)object).getProtectedRange();
            Object object2 = new HashSet(hashSet);
            ((AbstractCollection)object2).retainAll(arrayList);
            if (((HashSet)object2).size() > 0) {
                if (((HashSet)object2).size() == arrayList.size()) {
                    arrayList = new ArrayList<BasicBlock>();
                    object = new ExceptionRangeCFG(arrayList, (BasicBlock)hashMap.get(object.getHandler().id), ((ExceptionRangeCFG)object).getExceptionType());
                    this.exceptions.add(object);
                }
                object2 = ((HashSet)object2).iterator();
                while (object2.hasNext()) {
                    object = (BasicBlock)object2.next();
                    arrayList.add((BasicBlock)hashMap.get(((BasicBlock)object).id));
                }
            }
            --n;
        }
    }

    private void removeJsrInstructions(ConstantPool constantPool, BasicBlock basicBlock, DataPoint dataPoint) {
        Object object;
        Object object2;
        ListStack listStack = dataPoint.getStack();
        InstructionSequence instructionSequence = basicBlock.getSeq();
        int n = 0;
        while (n < instructionSequence.length()) {
            object2 = instructionSequence.getInstr(n);
            object = null;
            if (((Instruction)object2).opcode == 58 || ((Instruction)object2).opcode == 87) {
                object = (VarType)listStack.getByOffset(-1);
            }
            InstructionImpact.processSpecialInstructions(dataPoint, (Instruction)object2, constantPool);
            switch (((Instruction)object2).opcode) {
                case 168: 
                case 169: {
                    instructionSequence.removeInstruction(n);
                    --n;
                    break;
                }
                case 58: 
                case 87: {
                    if (((VarType)object).type != 9) break;
                    instructionSequence.removeInstruction(n);
                    --n;
                }
            }
            ++n;
        }
        basicBlock.mark = 1;
        n = 0;
        while (n < basicBlock.getSuccs().size()) {
            object2 = (BasicBlock)basicBlock.getSuccs().get(n);
            if (((BasicBlock)object2).mark != 1) {
                this.removeJsrInstructions(constantPool, (BasicBlock)object2, dataPoint.copy());
            }
            ++n;
        }
        n = 0;
        while (n < basicBlock.getSuccExceptions().size()) {
            object2 = (BasicBlock)basicBlock.getSuccExceptions().get(n);
            if (((BasicBlock)object2).mark != 1) {
                object = new DataPoint();
                ((DataPoint)object).setLocalVariables(new ArrayList(dataPoint.getLocalVariables()));
                ((DataPoint)object).getStack().push(new VarType(8, 0, null));
                this.removeJsrInstructions(constantPool, (BasicBlock)object2, (DataPoint)object);
            }
            ++n;
        }
    }

    private void setFirstAndLastBlocks() {
        this.first = (BasicBlock)this.blocks.get(0);
        this.last = new BasicBlock();
        this.last.id = ++this.last_id;
        this.last.setSeq(new SimpleInstructionSequence());
        for (BasicBlock basicBlock : this.blocks) {
            if (!basicBlock.getSuccs().isEmpty()) continue;
            this.last.addPredecessor(basicBlock);
        }
    }

    public final List getReversePostOrder() {
        LinkedList linkedList = new LinkedList();
        ControlFlowGraph.addToReversePostOrderListIterative(this.first, linkedList);
        return linkedList;
    }

    private static void addToReversePostOrderListIterative(BasicBlock basicBlock, List list) {
        LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
        linkedList.add(basicBlock);
        linkedList2.add(0);
        while (!linkedList.isEmpty()) {
            basicBlock = (BasicBlock)linkedList.getLast();
            int n = (Integer)linkedList2.removeLast();
            hashSet.add(basicBlock);
            ArrayList arrayList = new ArrayList(basicBlock.getSuccs());
            arrayList.addAll(basicBlock.getSuccExceptions());
            while (n < arrayList.size()) {
                BasicBlock basicBlock2 = (BasicBlock)arrayList.get(n);
                if (!hashSet.contains(basicBlock2)) {
                    linkedList2.add(n + 1);
                    linkedList.add(basicBlock2);
                    linkedList2.add(0);
                    break;
                }
                ++n;
            }
            if (n != arrayList.size()) continue;
            list.add(0, basicBlock);
            linkedList.removeLast();
        }
    }

    public final VBStyleCollection getBlocks() {
        return this.blocks;
    }

    public final BasicBlock getFirst() {
        return this.first;
    }

    public final void setFirst(BasicBlock basicBlock) {
        this.first = basicBlock;
    }

    public final List getExceptions() {
        return this.exceptions;
    }

    public final BasicBlock getLast() {
        return this.last;
    }

    public final HashSet getFinallyExits() {
        return this.finallyExits;
    }
}

