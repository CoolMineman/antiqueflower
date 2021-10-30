/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.bx;
import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprentStack;
import de.fernflower.modules.decompiler.PrimitiveExprsList;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.ArrayExprent;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.IfExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.MonitorExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.SwitchExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$FinallyPathWrapper;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.AbstractCollection;
import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraftforge.lex.fffixer.Util;

public final class ExprProcessor
implements bx {
    private static final HashMap mapConsts = new HashMap<Integer, Integer>();
    private static final VarType[] consts;
    private static final VarType[] vartypes;
    private static final VarType[] arrtypes;
    private static final int[] func1;
    private static final int[] func2;
    private static final int[] func3;
    private static final int[] func4;
    private static final int[] func5;
    private static final int[] func6;
    private static final int[] func7;
    private static final int[] func8;
    private static final int[] arr_type;
    private static final int[] negifs;
    private static final String[] typeNames;
    private VarProcessor varProcessor = (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR");

    static {
        mapConsts.put(new Integer(190), new Integer(31));
        mapConsts.put(new Integer(192), new Integer(29));
        mapConsts.put(new Integer(193), new Integer(30));
        consts = new VarType[]{VarType.VARTYPE_INT, VarType.VARTYPE_FLOAT, VarType.VARTYPE_LONG, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_CLASS, VarType.VARTYPE_STRING};
        vartypes = new VarType[]{VarType.VARTYPE_INT, VarType.VARTYPE_LONG, VarType.VARTYPE_FLOAT, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_OBJECT};
        arrtypes = new VarType[]{VarType.VARTYPE_INT, VarType.VARTYPE_LONG, VarType.VARTYPE_FLOAT, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_OBJECT, VarType.VARTYPE_BOOLEAN, VarType.VARTYPE_CHAR, VarType.VARTYPE_SHORT};
        int[] nArray = new int[5];
        nArray[1] = 1;
        nArray[2] = 2;
        nArray[3] = 3;
        nArray[4] = 7;
        func1 = nArray;
        func2 = new int[]{8, 9, 10, 4, 5, 6};
        func3 = new int[]{14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
        func4 = new int[]{37, 38, 39, 40, 41};
        int[] nArray2 = new int[6];
        nArray2[1] = 1;
        nArray2[2] = 2;
        nArray2[3] = 3;
        nArray2[4] = 4;
        nArray2[5] = 5;
        func5 = nArray2;
        func6 = new int[]{8, 9, 10, 11, 12, 13, 14, 15};
        func7 = new int[]{6, 7};
        int[] nArray3 = new int[2];
        nArray3[1] = 1;
        func8 = nArray3;
        int[] nArray4 = new int[8];
        nArray4[0] = 7;
        nArray4[1] = 1;
        nArray4[2] = 3;
        nArray4[3] = 2;
        nArray4[5] = 6;
        nArray4[6] = 4;
        nArray4[7] = 5;
        arr_type = nArray4;
        int[] nArray5 = new int[16];
        nArray5[0] = 1;
        nArray5[2] = 3;
        nArray5[3] = 2;
        nArray5[4] = 5;
        nArray5[5] = 4;
        nArray5[6] = 7;
        nArray5[7] = 6;
        nArray5[8] = 9;
        nArray5[9] = 8;
        nArray5[10] = 11;
        nArray5[11] = 10;
        nArray5[12] = 13;
        nArray5[13] = 12;
        nArray5[14] = 15;
        nArray5[15] = 14;
        negifs = nArray5;
        typeNames = new String[]{"byte", "char", "double", "float", "int", "long", "short", "boolean"};
    }

    public final void processStatement(RootStatement rootStatement, ConstantPool constantPool) {
        LinkedList<Object> linkedList2;
        HashMap<Object, Object> hashMap2;
        HashSet<String> hashSet;
        Object object = new FlattenStatementsHelper();
        DirectGraph directGraph = ((FlattenStatementsHelper)object).buildDirectGraph(rootStatement);
        HashSet<String> hashSet2 = new HashSet<String>();
        Object object2 = directGraph.mapShortRangeFinallyPaths.values().iterator();
        while (object2.hasNext()) {
            hashSet = null;
            for (HashMap<Object, Object> hashMap2 : (List)object2.next()) {
                hashSet2.add(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).entry);
            }
        }
        hashSet = new HashSet<String>();
        hashMap2 = directGraph.mapLongRangeFinallyPaths.values().iterator();
        while (hashMap2.hasNext()) {
            for (LinkedList<Object> linkedList2 : (List)hashMap2.next()) {
                hashSet.add(String.valueOf(((FlattenStatementsHelper$FinallyPathWrapper)linkedList2).source) + "##" + ((FlattenStatementsHelper$FinallyPathWrapper)linkedList2).entry);
            }
        }
        object2 = new HashMap();
        this.collectCatchVars(rootStatement, (FlattenStatementsHelper)object, (Map)object2);
        hashMap2 = new HashMap<Object, Object>();
        linkedList2 = new LinkedList<Object>();
        LinkedList linkedList3 = new LinkedList();
        linkedList2.add(directGraph.first);
        linkedList3.add(new LinkedList());
        object = new HashMap<Object, PrimitiveExprsList>();
        object.put(null, new PrimitiveExprsList());
        hashMap2.put(directGraph.first, object);
        while (!linkedList2.isEmpty()) {
            Object object3;
            Object object4;
            object = (DirectNode)linkedList2.removeFirst();
            LinkedList linkedList4 = (LinkedList)linkedList3.removeFirst();
            if (object2.containsKey(((DirectNode)object).id)) {
                object4 = (VarExprent)object2.get(((DirectNode)object).id);
                object3 = new PrimitiveExprsList();
                Object object5 = new VarExprent(10000, ((VarExprent)object4).getExprType(), ((VarExprent)object4).getProcessor());
                ((VarExprent)object5).setStack();
                ((PrimitiveExprsList)object3).getLstExprents().add(new AssignmentExprent((Exprent)object5, ((VarExprent)object4).copy()));
                ((PrimitiveExprsList)object3).getStack().push(((VarExprent)object5).copy());
                object4 = object3;
            } else {
                object4 = (PrimitiveExprsList)((Map)hashMap2.get(object)).get(ExprProcessor.buildEntryPointKey(linkedList4));
            }
            object3 = ((DirectNode)object).block;
            if (object3 != null) {
                this.processBlock((BasicBlockStatement)object3, (PrimitiveExprsList)object4, constantPool);
                ((Statement)object3).setExprents(((PrimitiveExprsList)object4).getLstExprents());
            }
            object3 = linkedList4.isEmpty() ? null : (String)linkedList4.getLast();
            for (Object object5 : ((DirectNode)object).succs) {
                HashMap hashMap32;
                boolean bl = true;
                if (object3 != null && directGraph.mapLongRangeFinallyPaths.containsKey(((DirectNode)object).id)) {
                    bl = false;
                    for (HashMap hashMap32 : (List)directGraph.mapLongRangeFinallyPaths.get(((DirectNode)object).id)) {
                        if (!((FlattenStatementsHelper$FinallyPathWrapper)hashMap32).source.equals(object3) || !((FlattenStatementsHelper$FinallyPathWrapper)hashMap32).destination.equals(((DirectNode)object5).id)) continue;
                        bl = true;
                        break;
                    }
                }
                if (!bl) continue;
                hashMap32 = (HashMap)hashMap2.get(object5);
                if (hashMap32 == null) {
                    hashMap32 = new HashMap();
                    hashMap2.put(object5, hashMap32);
                }
                LinkedList linkedList5 = new LinkedList(linkedList4);
                if (hashSet.contains(String.valueOf(((DirectNode)object).id) + "##" + ((DirectNode)object5).id)) {
                    linkedList5.addLast(((DirectNode)object).id);
                } else if (!hashSet2.contains(((DirectNode)object5).id) && directGraph.mapLongRangeFinallyPaths.containsKey(((DirectNode)object).id)) {
                    linkedList5.removeLast();
                }
                String string = ExprProcessor.buildEntryPointKey(linkedList5);
                if (hashMap32.containsKey(string)) continue;
                hashMap32.put(string, ExprProcessor.copyVarExprents(((PrimitiveExprsList)object4).copyStack()));
                linkedList2.add(object5);
                linkedList3.add(linkedList5);
            }
        }
        this.initStatementExprents(rootStatement);
    }

    private static String buildEntryPointKey(LinkedList object) {
        if (((AbstractCollection)object).isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = ((AbstractSequentialList)object).iterator();
        while (iterator.hasNext()) {
            object = (String)iterator.next();
            stringBuilder.append((String)object);
            stringBuilder.append(":");
        }
        return stringBuilder.toString();
    }

    private static PrimitiveExprsList copyVarExprents(PrimitiveExprsList primitiveExprsList) {
        ExprentStack exprentStack = primitiveExprsList.getStack();
        int n = 0;
        while (n < exprentStack.size()) {
            exprentStack.set(n, ((Exprent)exprentStack.get(n)).copy());
            ++n;
        }
        return primitiveExprsList;
    }

    private void collectCatchVars(Statement object, FlattenStatementsHelper flattenStatementsHelper, Map map) {
        List list = null;
        if (((Statement)object).type == 12) {
            Statement statement = (CatchAllStatement)object;
            if (!statement.isFinally()) {
                list = statement.getVars();
            }
        } else if (((Statement)object).type == 7) {
            list = ((CatchStatement)object).getVars();
        }
        if (list != null) {
            int n = 1;
            while (n < ((Statement)object).getStats().size()) {
                map.put(((String[])flattenStatementsHelper.getMapDestinationNodes().get(((Statement)object.getStats().get((int)n)).id))[0], (VarExprent)list.get(n - 1));
                ++n;
            }
        }
        for (Statement statement : ((Statement)object).getStats()) {
            this.collectCatchVars(statement, flattenStatementsHelper, map);
        }
    }

    private void initStatementExprents(Statement statement2) {
        statement2.initExprents();
        for (Statement statement2 : statement2.getStats()) {
            this.initStatementExprents(statement2);
        }
    }

    private void processBlock(BasicBlockStatement object, PrimitiveExprsList object2, ConstantPool constantPool) {
        object = ((BasicBlockStatement)object).getBlock();
        ExprentStack exprentStack = ((PrimitiveExprsList)object2).getStack();
        object2 = ((PrimitiveExprsList)object2).getLstExprents();
        object = ((BasicBlock)object).getSeq();
        int n = 0;
        while (n < ((InstructionSequence)object).length()) {
            Object object3 = ((InstructionSequence)object).getInstr(n);
            switch (((Instruction)object3).opcode) {
                case 1: {
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(VarType.VARTYPE_NULL, null));
                    break;
                }
                case 16: 
                case 17: {
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(((Instruction)object3).getOperand(0), true));
                    break;
                }
                case 9: 
                case 10: {
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(VarType.VARTYPE_LONG, new Long(((Instruction)object3).opcode - 9)));
                    break;
                }
                case 11: 
                case 12: 
                case 13: {
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(VarType.VARTYPE_FLOAT, new Float(((Instruction)object3).opcode - 11)));
                    break;
                }
                case 14: 
                case 15: {
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(VarType.VARTYPE_DOUBLE, new Double(((Instruction)object3).opcode - 14)));
                    break;
                }
                case 18: 
                case 19: 
                case 20: {
                    object3 = constantPool.getPrimitiveConstant(((Instruction)object3).getOperand(0));
                    this.pushEx(exprentStack, (List)object2, new ConstExprent(consts[((PrimitiveConstant)object3).type - 3], ((PrimitiveConstant)object3).value));
                    break;
                }
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: {
                    this.pushEx(exprentStack, (List)object2, new VarExprent(((Instruction)object3).getOperand(0), vartypes[((Instruction)object3).opcode - 21], this.varProcessor));
                    break;
                }
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: {
                    Exprent exprent = exprentStack.pop();
                    Object object4 = exprentStack.pop();
                    Object object5 = null;
                    switch (((Instruction)object3).opcode) {
                        case 47: {
                            object5 = VarType.VARTYPE_LONG;
                            break;
                        }
                        case 49: {
                            object5 = VarType.VARTYPE_DOUBLE;
                        }
                    }
                    this.pushEx(exprentStack, (List)object2, new ArrayExprent((Exprent)object4, exprent, arrtypes[((Instruction)object3).opcode - 46]), (VarType)object5);
                    break;
                }
                case 54: 
                case 55: 
                case 56: 
                case 57: 
                case 58: {
                    Exprent exprent = exprentStack.pop();
                    int n2 = ((Instruction)object3).getOperand(0);
                    object3 = new AssignmentExprent(new VarExprent(n2, vartypes[((Instruction)object3).opcode - 54], this.varProcessor), exprent);
                    object2.add(object3);
                    break;
                }
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: {
                    Exprent exprent = exprentStack.pop();
                    Object object4 = exprentStack.pop();
                    Object object5 = exprentStack.pop();
                    object3 = new AssignmentExprent(new ArrayExprent((Exprent)object5, (Exprent)object4, arrtypes[((Instruction)object3).opcode - 79]), exprent);
                    object2.add(object3);
                    break;
                }
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent(func1[(((Instruction)object3).opcode - 96) / 4], exprentStack));
                    break;
                }
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent(func2[(((Instruction)object3).opcode - 120) / 2], exprentStack));
                    break;
                }
                case 116: 
                case 117: 
                case 118: 
                case 119: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent(13, exprentStack));
                    break;
                }
                case 132: {
                    VarExprent varExprent = new VarExprent(((Instruction)object3).getOperand(0), VarType.VARTYPE_INT, this.varProcessor);
                    object2.add(new AssignmentExprent(varExprent, new FunctionExprent(((Instruction)object3).getOperand(1) < 0 ? 1 : 0, Arrays.asList(varExprent.copy(), new ConstExprent(VarType.VARTYPE_INT, new Integer(Math.abs(((Instruction)object3).getOperand(1))))))));
                    break;
                }
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent(func3[((Instruction)object3).opcode - 133], exprentStack));
                    break;
                }
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent(func4[((Instruction)object3).opcode - 148], exprentStack));
                    break;
                }
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: {
                    object2.add(new IfExprent(negifs[func5[((Instruction)object3).opcode - 153]], exprentStack));
                    break;
                }
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: {
                    object2.add(new IfExprent(negifs[func6[((Instruction)object3).opcode - 159]], exprentStack));
                    break;
                }
                case 198: 
                case 199: {
                    object2.add(new IfExprent(negifs[func7[((Instruction)object3).opcode - 198]], exprentStack));
                    break;
                }
                case 170: 
                case 171: {
                    object2.add(new SwitchExprent(exprentStack.pop()));
                    break;
                }
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    object2.add(new ExitExprent(((Instruction)object3).opcode == 191 ? 1 : 0, ((Instruction)object3).opcode == 177 ? null : exprentStack.pop(), ((Instruction)object3).opcode == 191 ? null : ((MethodDescriptor)DecompilerContext.getProperty((String)"CURRENT_METHOD_DESCRIPTOR")).ret));
                    break;
                }
                case 194: 
                case 195: {
                    object2.add(new MonitorExprent(func8[((Instruction)object3).opcode - 194], exprentStack.pop()));
                    break;
                }
                case 192: 
                case 193: {
                    Object object4 = null;
                    exprentStack.push(new ConstExprent(new VarType((String)constantPool.getPrimitiveConstant((int)object3.getOperand((int)0)).value, true), null));
                }
                case 190: {
                    this.pushEx(exprentStack, (List)object2, new FunctionExprent((int)((Integer)mapConsts.get(((Instruction)object3).opcode)), exprentStack));
                    break;
                }
                case 178: 
                case 180: {
                    this.pushEx(exprentStack, (List)object2, new FieldExprent(constantPool.getLinkConstant(((Instruction)object3).getOperand(0)), ((Instruction)object3).opcode == 178 ? null : exprentStack.pop()));
                    break;
                }
                case 179: 
                case 181: {
                    Exprent exprent = exprentStack.pop();
                    object3 = new FieldExprent(constantPool.getLinkConstant(((Instruction)object3).getOperand(0)), ((Instruction)object3).opcode == 179 ? null : exprentStack.pop());
                    object2.add(new AssignmentExprent((Exprent)object3, exprent));
                    break;
                }
                case 182: 
                case 183: 
                case 184: 
                case 185: {
                    object3 = new InvocationExprent(((Instruction)object3).opcode, constantPool.getLinkConstant(((Instruction)object3).getOperand(0)), exprentStack);
                    if (object3.getDescriptor().ret.type == 10) {
                        object2.add(object3);
                        break;
                    }
                    this.pushEx(exprentStack, (List)object2, (Exprent)object3);
                    break;
                }
                case 187: 
                case 189: 
                case 197: {
                    int n3 = ((Instruction)object3).opcode == 187 ? 0 : (((Instruction)object3).opcode == 189 ? 1 : ((Instruction)object3).getOperand(1));
                    Object object4 = null;
                    object4 = new VarType((String)constantPool.getPrimitiveConstant((int)object3.getOperand((int)0)).value, true);
                    if (((Instruction)object3).opcode != 197) {
                        ((VarType)object4).arraydim += n3;
                    }
                    this.pushEx(exprentStack, (List)object2, new NewExprent((VarType)object4, exprentStack, n3));
                    break;
                }
                case 188: {
                    this.pushEx(exprentStack, (List)object2, new NewExprent(new VarType(arr_type[((Instruction)object3).getOperand(0) - 4], 1), exprentStack, 1));
                    break;
                }
                case 89: {
                    this.pushEx(exprentStack, (List)object2, ((Exprent)exprentStack.getByOffset(-1)).copy());
                    break;
                }
                case 90: {
                    this.insertByOffsetEx(-2, exprentStack, (List)object2, -1);
                    break;
                }
                case 91: {
                    if (((Exprent)exprentStack.getByOffset((int)-2)).getExprType().stack_size == 2) {
                        this.insertByOffsetEx(-2, exprentStack, (List)object2, -1);
                        break;
                    }
                    this.insertByOffsetEx(-3, exprentStack, (List)object2, -1);
                    break;
                }
                case 92: {
                    if (((Exprent)exprentStack.getByOffset((int)-1)).getExprType().stack_size == 2) {
                        this.pushEx(exprentStack, (List)object2, ((Exprent)exprentStack.getByOffset(-1)).copy());
                        break;
                    }
                    this.pushEx(exprentStack, (List)object2, ((Exprent)exprentStack.getByOffset(-2)).copy());
                    this.pushEx(exprentStack, (List)object2, ((Exprent)exprentStack.getByOffset(-2)).copy());
                    break;
                }
                case 93: {
                    if (((Exprent)exprentStack.getByOffset((int)-1)).getExprType().stack_size == 2) {
                        this.insertByOffsetEx(-2, exprentStack, (List)object2, -1);
                        break;
                    }
                    this.insertByOffsetEx(-3, exprentStack, (List)object2, -2);
                    this.insertByOffsetEx(-3, exprentStack, (List)object2, -1);
                    break;
                }
                case 94: {
                    if (((Exprent)exprentStack.getByOffset((int)-1)).getExprType().stack_size == 2) {
                        if (((Exprent)exprentStack.getByOffset((int)-2)).getExprType().stack_size == 2) {
                            this.insertByOffsetEx(-2, exprentStack, (List)object2, -1);
                            break;
                        }
                        this.insertByOffsetEx(-3, exprentStack, (List)object2, -1);
                        break;
                    }
                    if (((Exprent)exprentStack.getByOffset((int)-3)).getExprType().stack_size == 2) {
                        this.insertByOffsetEx(-3, exprentStack, (List)object2, -2);
                        this.insertByOffsetEx(-3, exprentStack, (List)object2, -1);
                        break;
                    }
                    this.insertByOffsetEx(-4, exprentStack, (List)object2, -2);
                    this.insertByOffsetEx(-4, exprentStack, (List)object2, -1);
                    break;
                }
                case 95: {
                    this.insertByOffsetEx(-2, exprentStack, (List)object2, -1);
                    exprentStack.pop();
                    break;
                }
                case 87: 
                case 88: {
                    exprentStack.pop();
                }
            }
            ++n;
        }
    }

    private void pushEx(ExprentStack exprentStack, List list, Exprent exprent) {
        this.pushEx(exprentStack, list, exprent, null);
    }

    private void pushEx(ExprentStack exprentStack, List list, Exprent exprent, VarType varType) {
        int n = 10000 + exprentStack.size();
        VarExprent varExprent = new VarExprent(n, varType == null ? exprent.getExprType() : varType, ((ExprProcessor)varExprent).varProcessor);
        varExprent.setStack();
        list.add(new AssignmentExprent(varExprent, exprent));
        exprentStack.push(varExprent.copy());
    }

    private void insertByOffsetEx(int n, ExprentStack exprentStack, List list, int n2) {
        Exprent exprent;
        int n3 = 10000 + exprentStack.size();
        LinkedList<VarExprent> linkedList = new LinkedList<VarExprent>();
        int n4 = -1;
        while (n4 >= n) {
            exprent = exprentStack.pop();
            VarExprent varExprent = new VarExprent(n3 + n4 + 1, exprent.getExprType(), ((ExprProcessor)this).varProcessor);
            varExprent.setStack();
            list.add(new AssignmentExprent(varExprent, exprent));
            linkedList.add(0, (VarExprent)varExprent.copy());
            --n4;
        }
        Exprent exprent2 = ((VarExprent)linkedList.get(linkedList.size() + n2)).copy();
        exprent = new VarExprent(n3 + n, exprent2.getExprType(), ((ExprProcessor)this).varProcessor);
        ((VarExprent)exprent).setStack();
        list.add(new AssignmentExprent(exprent, exprent2));
        linkedList.add(0, (VarExprent)((VarExprent)exprent).copy());
        for (VarExprent varExprent : linkedList) {
            exprentStack.push(varExprent);
        }
    }

    public static String getTypeName(VarType varType) {
        return ExprProcessor.getTypeName(varType, true);
    }

    private static String getTypeName(VarType object, boolean bl) {
        int n = ((VarType)object).type;
        if (n <= 7) {
            return typeNames[n];
        }
        if (n == 17) {
            return "<unknown>";
        }
        if (n == 13) {
            return "<null>";
        }
        if (n == 10) {
            return "void";
        }
        if (n == 8) {
            object = ExprProcessor.buildJavaClassName(((VarType)object).value);
            if (bl) {
                object = DecompilerContext.getImpcollector().getShortName((String)object, true);
            }
            if (object == null) {
                object = "<undefinedtype>";
            }
            return object;
        }
        throw new RuntimeException("invalid type");
    }

    public static String getCastTypeName(VarType varType) {
        return ExprProcessor.getCastTypeName(varType, true);
    }

    public static String getCastTypeName(VarType varType, boolean bl) {
        String string = ExprProcessor.getTypeName(varType, bl);
        int n = varType.arraydim;
        while (n-- > 0) {
            string = String.valueOf(string) + "[]";
        }
        return string;
    }

    public static String jmpWrapper(Statement object, int n, boolean bl) {
        StringBuffer stringBuffer = new StringBuffer(((Statement)object).toJava(n));
        if ((object = ((Statement)object).getSuccessorEdges(0x40000000)).size() == 1 && ((StatEdge)(object = (StatEdge)object.get(0))).getType() != 1 && ((StatEdge)object).explicit && object.getDestination().type != 14) {
            stringBuffer.append(InterpreterUtil.getIndentString(n));
            switch (((StatEdge)object).getType()) {
                case 4: {
                    stringBuffer.append("break");
                    break;
                }
                case 8: {
                    stringBuffer.append("continue");
                }
            }
            if (((StatEdge)object).labeled) {
                stringBuffer.append(" label" + object.closure.id);
            }
            stringBuffer.append(";\r\n");
        }
        if (stringBuffer.length() == 0 && bl) {
            stringBuffer.append(String.valueOf(InterpreterUtil.getIndentString(n)) + ";\r\n");
        }
        return stringBuffer.toString();
    }

    public static String buildJavaClassName(String object) {
        String string = ((String)object).replace('/', '.');
        if (!(string.indexOf("$") < 0 || (object = DecompilerContext.getStructcontext().getClass((String)object)) != null && ((StructClass)object).isOwn())) {
            string = string.replace('$', '.');
        }
        return string;
    }

    public static String listToJava(List object, int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = Util.sortIndexed(object.iterator());
        while (iterator.hasNext()) {
            int n2;
            object = (Exprent)iterator.next();
            String string2 = ((Exprent)object).toJava(n);
            if (string2.length() <= 0) continue;
            if (((Exprent)object).type != 12 || !((VarExprent)object).isClassdef()) {
                stringBuffer.append(string);
            }
            stringBuffer.append(string2);
            if (((Exprent)object).type == 9 && ((MonitorExprent)object).getMontype() == 0) {
                stringBuffer.append("{}");
            }
            if ((n2 = ((Exprent)object).type) != 11 && n2 != 9 && n2 != 7 && (n2 != 12 || !((VarExprent)object).isClassdef())) {
                stringBuffer.append(";");
            }
            stringBuffer.append("\r\n");
        }
        return stringBuffer.toString();
    }

    public static ConstExprent getDefaultArrayValue(VarType object) {
        object = ((VarType)object).type == 8 || ((VarType)object).arraydim > 0 ? new ConstExprent(VarType.VARTYPE_NULL, null) : (((VarType)object).type == 3 ? new ConstExprent(VarType.VARTYPE_FLOAT, new Float(0.0f)) : (((VarType)object).type == 5 ? new ConstExprent(VarType.VARTYPE_LONG, new Long(0L)) : (((VarType)object).type == 2 ? new ConstExprent(VarType.VARTYPE_DOUBLE, new Double(0.0)) : new ConstExprent(0, true))));
        return object;
    }

    public static boolean getCastedExprent(Exprent exprent, VarType varType, StringBuilder stringBuilder, int n, boolean bl) {
        return ExprProcessor.getCastedExprent(exprent, varType, stringBuilder, n, bl, false);
    }

    /*
     * Unable to fully structure code
     */
    public static boolean getCastedExprent(Exprent var0, VarType var1_1, StringBuilder var2_2, int var3_3, boolean var4_5, boolean var5_8) {
        block6: {
            var6_9 = false;
            var7_10 = var0.getExprType();
            var3_4 = var0.toJava(var3_3);
            var8_11 = false;
            var8_11 = (var1_1.isSuperset(var7_10) == false && (var7_10.equals(VarType.VARTYPE_OBJECT) != false || var1_1.type != 8)) | (var5_8 != false && var1_1.equals(var7_10) == false);
            if (!var8_11 && var4_5 && var7_10.type == 13) {
                var4_6 = null;
                v0 = var8_11 = "<undefinedtype>".equals(ExprProcessor.getTypeName(var1_1, true)) == false;
            }
            if (var8_11) break block6;
            var4_7 = var0;
            if (var4_7.type != 3) ** GOTO lbl-1000
            switch (((ConstExprent)var4_7).getConsttype().type) {
                case 0: 
                case 4: 
                case 6: 
                case 15: 
                case 16: {
                    v1 = true;
                    break;
                }
                default: lbl-1000:
                // 2 sources

                {
                    v1 = false;
                }
            }
            v2 = var8_11 = v1 != false && VarType.VARTYPE_INT.isStrictSuperset(var1_1) != false;
        }
        if (var8_11) {
            if (var0.getPrecedence() >= FunctionExprent.g()) {
                var3_4 = "(" + var3_4 + ")";
            }
            var3_4 = "(" + ExprProcessor.getCastTypeName(var1_1, true) + ")" + var3_4;
            var6_9 = true;
        }
        var2_2.append(var3_4);
        return var6_9;
    }
}

