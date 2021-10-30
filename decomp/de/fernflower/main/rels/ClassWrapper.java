/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.main.rels.MethodProcessorThread;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.attr.StructLocalVariableTableAttribute;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;
import java.util.HashSet;

public final class ClassWrapper {
    private StructClass classStruct;
    private HashSet hideMembers = new HashSet();
    private VBStyleCollection staticFieldInitializers = new VBStyleCollection();
    private VBStyleCollection dynamicFieldInitializers = new VBStyleCollection();
    private VBStyleCollection methods = new VBStyleCollection();

    public ClassWrapper(StructClass structClass) {
        this.classStruct = structClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void init() {
        DecompilerContext.setProperty("CURRENT_CLASS", this.classStruct);
        DecompilerContext.getLogger().startClass(this.classStruct.qualifiedName);
        HashSet<String> hashSet = new HashSet<String>();
        for (Object object : this.classStruct.getFields()) {
            hashSet.add(((StructField)object).getName());
        }
        for (Object object : this.classStruct.getMethods()) {
            Object object2;
            boolean bl;
            RootStatement rootStatement;
            VarProcessor varProcessor;
            CounterContainer counterContainer;
            block20: {
                DecompilerContext.getLogger().startMethod(String.valueOf(((StructMethod)object).getName()) + " " + ((StructMethod)object).getDescriptor());
                VarNamesCollector varNamesCollector = new VarNamesCollector();
                DecompilerContext.setVarncollector(varNamesCollector);
                counterContainer = new CounterContainer();
                DecompilerContext.setCountercontainer(counterContainer);
                DecompilerContext.setProperty("CURRENT_METHOD", object);
                DecompilerContext.setProperty("CURRENT_METHOD_DESCRIPTOR", MethodDescriptor.parseDescriptor(((StructMethod)object).getDescriptor()));
                varProcessor = new VarProcessor();
                DecompilerContext.setProperty("CURRENT_VAR_PROCESSOR", varProcessor);
                Thread thread = null;
                rootStatement = null;
                bl = false;
                try {
                    int n;
                    if ((((StructMethod)object).getAccessFlags() & 0x500) == 0) {
                        int n2 = 10 * Integer.parseInt(DecompilerContext.getProperty("mpm").toString());
                        if (n2 == 0) {
                            rootStatement = MethodProcessorThread.codeToJava((StructMethod)object, varProcessor);
                            break block20;
                        }
                        object2 = new MethodProcessorThread((StructMethod)object, varProcessor, DecompilerContext.getCurrentContext());
                        thread = new Thread((Runnable)object2);
                        thread.start();
                        n = 0;
                        while (thread.isAlive()) {
                            Object object3 = object2;
                            synchronized (object3) {
                                object2.wait(100L);
                            }
                            if (n2 <= 0 || ++n <= n2) continue;
                            DecompilerContext.getLogger().writeMessage("Processing time limit (" + n2 + " sec.) for method " + ((StructMethod)object).getName() + " " + ((StructMethod)object).getDescriptor() + " exceeded, execution interrupted.", 4, 0);
                            thread.stop();
                            bl = true;
                            break;
                        }
                        if (!bl) {
                            if (((MethodProcessorThread)object2).getError() != null) {
                                throw ((MethodProcessorThread)object2).getError();
                            }
                            rootStatement = ((MethodProcessorThread)object2).getRoot();
                        }
                        break block20;
                    }
                    boolean bl2 = (((StructMethod)object).getAccessFlags() & 8) == 0;
                    object2 = MethodDescriptor.parseDescriptor(((StructMethod)object).getDescriptor());
                    n = 0;
                    if (bl2) {
                        varProcessor.getThisvars().put(new VarVersionPaar(0, 0), this.classStruct.qualifiedName);
                        n = 1;
                    }
                    int n3 = 0;
                    int n4 = 0;
                    while (n4 < (n += ((MethodDescriptor)object2).params.length)) {
                        varProcessor.setVarName(new VarVersionPaar(n3, 0), varNamesCollector.getFreeName(n3));
                        n3 = bl2 ? (n4 == 0 ? ++n3 : (n3 += object2.params[n4 - 1].stack_size)) : (n3 += object2.params[n4].stack_size);
                        ++n4;
                    }
                }
                catch (ThreadDeath threadDeath) {
                    try {
                        if (thread != null) {
                            thread.stop();
                        }
                    }
                    catch (Throwable throwable) {}
                    throw threadDeath;
                }
                catch (Throwable throwable) {
                    if (DecompilerContext.getLogger().getShowStacktrace()) {
                        throwable.printStackTrace();
                    }
                    DecompilerContext.getLogger().writeMessage("Method " + ((StructMethod)object).getName() + " " + ((StructMethod)object).getDescriptor() + " couldn't be decompiled.", 4);
                    bl = true;
                }
            }
            MethodWrapper methodWrapper = new MethodWrapper(rootStatement, varProcessor, (StructMethod)object, counterContainer);
            new MethodWrapper(rootStatement, varProcessor, (StructMethod)object, counterContainer).decompiledWithErrors = bl;
            this.methods.addWithKey(methodWrapper, InterpreterUtil.makeUniqueKey(((StructMethod)object).getName(), ((StructMethod)object).getDescriptor()));
            varProcessor.refreshVarNames(new VarNamesCollector(hashSet));
            if (DecompilerContext.getOption("udv") && (object2 = (StructLocalVariableTableAttribute)((StructMethod)object).getAttributes().getWithKey("LocalVariableTable")) != null) {
                varProcessor.setDebugVarNames(((StructLocalVariableTableAttribute)object2).getMapVarNames());
            }
            DecompilerContext.getLogger().endMethod();
        }
        DecompilerContext.getLogger().endClass();
    }

    public final MethodWrapper getMethodWrapper(String string, String string2) {
        return (MethodWrapper)this.methods.getWithKey(InterpreterUtil.makeUniqueKey(string, string2));
    }

    public final StructClass getClassStruct() {
        return this.classStruct;
    }

    public final VBStyleCollection getMethods() {
        return this.methods;
    }

    public final HashSet getHideMembers() {
        return this.hideMembers;
    }

    public final VBStyleCollection getStaticFieldInitializers() {
        return this.staticFieldInitializers;
    }

    public final VBStyleCollection getDynamicFieldInitializers() {
        return this.dynamicFieldInitializers;
    }
}

