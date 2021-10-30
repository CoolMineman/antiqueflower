/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class VarDefinitionHelper {
    private HashMap mapVarDefStatements = new HashMap();
    private HashMap mapStatementVars = new HashMap();
    private HashSet implDefVars = new HashSet();
    private VarProcessor varproc;

    public VarDefinitionHelper(Statement statement, StructMethod object, VarProcessor varProcessor) {
        this.varproc = varProcessor;
        VarNamesCollector varNamesCollector = DecompilerContext.getVarncollector();
        boolean bl = (((StructMethod)object).getAccessFlags() & 8) == 0;
        object = MethodDescriptor.parseDescriptor(((StructMethod)object).getDescriptor());
        int n = 0;
        if (bl) {
            n = 1;
        }
        int n2 = 0;
        int n3 = 0;
        while (n3 < (n += ((MethodDescriptor)object).params.length)) {
            this.implDefVars.add(n2);
            varProcessor.setVarName(new VarVersionPaar(n2, 0), varNamesCollector.getFreeName(n2));
            n2 = bl ? (n3 == 0 ? ++n2 : (n2 += object.params[n3 - 1].stack_size)) : (n2 += object.params[n3].stack_size);
            ++n3;
        }
        if (bl) {
            StructClass structClass = (StructClass)DecompilerContext.getProperty("CURRENT_CLASS");
            varProcessor.getThisvars().put(new VarVersionPaar(0, 0), structClass.qualifiedName);
            varProcessor.setVarName(new VarVersionPaar(0, 0), "this");
            varNamesCollector.addName("this");
        }
        LinkedList<Statement> linkedList = new LinkedList<Statement>();
        linkedList.add(statement);
        while (!linkedList.isEmpty()) {
            object = (Statement)linkedList.removeFirst();
            Object object2 = null;
            if (((Statement)object).type == 12) {
                object2 = ((CatchAllStatement)object).getVars();
            } else if (((Statement)object).type == 7) {
                object2 = ((CatchStatement)object).getVars();
            }
            if (object2 != null) {
                Iterator iterator = object2.iterator();
                while (iterator.hasNext()) {
                    object2 = (VarExprent)iterator.next();
                    this.implDefVars.add(((VarExprent)object2).getIndex0());
                    varProcessor.setVarName(new VarVersionPaar((VarExprent)object2), varNamesCollector.getFreeName(((VarExprent)object2).getIndex0()));
                    ((VarExprent)object2).setDefinition();
                }
            }
            linkedList.addAll(((Statement)object).getStats());
        }
        this.initStatement(statement);
    }

    public final void setVarDefinitions() {
        VarNamesCollector varNamesCollector = DecompilerContext.getVarncollector();
        for (Map.Entry entry : this.mapVarDefStatements.entrySet()) {
            Object object;
            Object object2;
            Object object3;
            Integer iterator;
            Object object42;
            block13: {
                Statement statement;
                Object object5;
                object42 = (Statement)entry.getValue();
                iterator = (Integer)entry.getKey();
                if (this.implDefVars.contains(iterator)) continue;
                this.varproc.setVarName(new VarVersionPaar((int)iterator, 0), varNamesCollector.getFreeName(iterator));
                if (((Statement)object42).type == 5 && ((DoStatement)(object3 = (DoStatement)object42)).getLooptype() == 3) {
                    if (((DoStatement)object3).getInitExprent() != null && VarDefinitionHelper.setDefinition(((DoStatement)object3).getInitExprent(), iterator)) continue;
                    object5 = VarDefinitionHelper.getAllVars(Arrays.asList(((DoStatement)object3).getConditionExprent(), ((DoStatement)object3).getIncExprent())).iterator();
                    while (object5.hasNext()) {
                        statement = null;
                        if (((VarExprent)object5.next()).getIndex0() != iterator.intValue()) continue;
                        object42 = ((Statement)object42).getParent();
                        break;
                    }
                }
                object5 = iterator;
                statement = object42;
                object3 = this;
                object2 = new LinkedList<Statement>();
                ((LinkedList)object2).add(statement);
                while (!((AbstractCollection)object2).isEmpty()) {
                    statement = (Statement)((LinkedList)object2).remove(0);
                    if (!((AbstractCollection)object2).isEmpty() && !((HashSet)((VarDefinitionHelper)object3).mapStatementVars.get(statement.id)).contains(object5)) continue;
                    if (statement.isLabeled() && !((AbstractCollection)object2).isEmpty()) {
                        object = statement;
                        break block13;
                    }
                    if (statement.getExprents() != null) {
                        object = statement;
                        break block13;
                    }
                    ((LinkedList)object2).clear();
                    switch (statement.type) {
                        case 15: {
                            ((LinkedList)object2).addAll(0, statement.getStats());
                            break;
                        }
                        case 2: 
                        case 6: 
                        case 10: 
                        case 13: {
                            ((LinkedList)object2).add(statement.getFirst());
                            break;
                        }
                        default: {
                            object = statement;
                            break block13;
                        }
                    }
                }
                object = object3 = null;
            }
            object3 = object == null ? ((Statement)object42).getVarDefinitions() : (((Statement)object3).getExprents() == null ? ((Statement)object3).getVarDefinitions() : ((Statement)object3).getExprents());
            boolean statement = false;
            int iterator2 = 0;
            object2 = object3.iterator();
            while (object2.hasNext()) {
                object42 = (Exprent)object2.next();
                if (VarDefinitionHelper.setDefinition((Exprent)object42, iterator)) {
                    statement = true;
                    break;
                }
                boolean bl = false;
                for (Object object42 : ((Exprent)object42).getAllExprents(true)) {
                    if (((Exprent)object42).type != 12 || ((VarExprent)object42).getIndex0() != iterator.intValue()) continue;
                    bl = true;
                    break;
                }
                if (bl) break;
                ++iterator2;
            }
            if (statement) continue;
            object42 = new VarExprent(iterator, this.varproc.getVarType(new VarVersionPaar((int)iterator, 0)), this.varproc);
            ((VarExprent)object42).setDefinition();
            object3.add(iterator2, object42);
        }
    }

    private Set initStatement(Statement statement) {
        Collection collection22;
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        if (statement.getExprents() == null) {
            Object object;
            collection22 = new ArrayList();
            Iterator iterator = new ArrayList();
            for (Object e : statement.getSequentialObjects()) {
                if (e instanceof Statement) {
                    CatchAllStatement catchAllStatement;
                    object = (Statement)e;
                    collection22.addAll(this.initStatement((Statement)object));
                    if (((Statement)object).type == 5) {
                        DoStatement doStatement = (DoStatement)object;
                        if (doStatement.getLooptype() == 3 || doStatement.getLooptype() == 0) continue;
                        iterator.add((Exprent)doStatement.getConditionExprent());
                        continue;
                    }
                    if (((Statement)object).type != 12 || !(catchAllStatement = (CatchAllStatement)object).isFinally() || catchAllStatement.getMonitor() == null) continue;
                    iterator.add((VarExprent)catchAllStatement.getMonitor());
                    continue;
                }
                if (!(e instanceof Exprent)) continue;
                iterator.add((Exprent)((Exprent)e));
            }
            for (Integer n : collection22) {
                object = (Integer)hashMap.get(n);
                if (object == null) {
                    object = new Integer(0);
                }
                hashMap.put(n, new Integer((Integer)object + 1));
            }
            collection22 = VarDefinitionHelper.getAllVars(iterator);
        } else {
            collection22 = VarDefinitionHelper.getAllVars(statement.getExprents());
        }
        for (Collection collection22 : collection22) {
            hashMap.put(new Integer(((VarExprent)((Object)collection22)).getIndex0()), new Integer(2));
        }
        collection22 = new HashSet(hashMap.keySet());
        for (Map.Entry entry : hashMap.entrySet()) {
            if ((Integer)entry.getValue() <= 1) continue;
            this.mapVarDefStatements.put((Integer)entry.getKey(), statement);
        }
        this.mapStatementVars.put(statement.id, collection22);
        return collection22;
    }

    private static List getAllVars(List object2) {
        ArrayList<VarExprent> arrayList = new ArrayList<VarExprent>();
        ArrayList<Object> arrayList2 = new ArrayList<Object>();
        Iterator iterator = object2.iterator();
        while (iterator.hasNext()) {
            object2 = (Exprent)iterator.next();
            arrayList2.addAll(((Exprent)object2).getAllExprents(true));
            arrayList2.add(object2);
        }
        for (Object object2 : arrayList2) {
            if (((Exprent)object2).type != 12) continue;
            arrayList.add((VarExprent)object2);
        }
        return arrayList;
    }

    private static boolean setDefinition(Exprent exprent, Integer n) {
        if (exprent.type == 2) {
            exprent = ((AssignmentExprent)exprent).getLeft();
            if (exprent.type == 12 && ((VarExprent)(exprent = (VarExprent)exprent)).getIndex0() == n.intValue()) {
                ((VarExprent)exprent).setDefinition();
                return true;
            }
        }
        return false;
    }
}

