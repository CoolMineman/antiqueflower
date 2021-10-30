/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.code.InstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.code.DeadCodeHelper;
import de.fernflower.modules.decompiler.ClearStructHelper;
import de.fernflower.modules.decompiler.DomHelper;
import de.fernflower.modules.decompiler.ExitHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.FinallyProcessor;
import de.fernflower.modules.decompiler.IfHelper;
import de.fernflower.modules.decompiler.InlineSingleBlockHelper;
import de.fernflower.modules.decompiler.LabelHelper;
import de.fernflower.modules.decompiler.LoopExtractHelper;
import de.fernflower.modules.decompiler.MergeHelper;
import de.fernflower.modules.decompiler.PPandMMHelper;
import de.fernflower.modules.decompiler.SecondaryFunctionsHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StackVarsProcessor;
import de.fernflower.modules.decompiler.deobfuscator.ExceptionDeobfuscator;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructMethod;
import java.util.HashSet;

public final class MethodProcessorThread
implements Runnable {
    private StructMethod method;
    private VarProcessor varproc;
    private DecompilerContext parentContext;
    private RootStatement root;
    private Throwable error;

    public MethodProcessorThread(StructMethod structMethod, VarProcessor varProcessor, DecompilerContext decompilerContext) {
        this.method = structMethod;
        this.varproc = varProcessor;
        this.parentContext = decompilerContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void run() {
        DecompilerContext.setCurrentContext(this.parentContext);
        this.error = null;
        this.root = null;
        try {
            this.root = MethodProcessorThread.codeToJava(this.method, this.varproc);
            MethodProcessorThread methodProcessorThread = this;
            synchronized (methodProcessorThread) {
                this.notify();
                return;
            }
        }
        catch (ThreadDeath threadDeath) {
            return;
        }
        catch (Throwable throwable) {
            this.error = throwable;
            return;
        }
    }

    public static RootStatement codeToJava(StructMethod structMethod, VarProcessor varProcessor) {
        StructClass structClass = structMethod.getClassStruct();
        boolean bl = "<clinit>".equals(structMethod.getName());
        structMethod.expandData();
        Object object = structMethod.getInstructionSequence();
        object = new ControlFlowGraph((InstructionSequence)object);
        DeadCodeHelper.removeDeadBlocks((ControlFlowGraph)object);
        ((ControlFlowGraph)object).inlineJsr(structMethod);
        Object object2 = null;
        object2 = ((ControlFlowGraph)object).getLast();
        for (Object object3 : new HashSet(((BasicBlock)object2).getPreds())) {
            ((BasicBlock)object2).removePredecessor((BasicBlock)object3);
            ((BasicBlock)object3).addSuccessor((BasicBlock)object2);
        }
        DeadCodeHelper.removeGotos((ControlFlowGraph)object);
        ExceptionDeobfuscator.removeCircularRanges((ControlFlowGraph)object);
        ExceptionDeobfuscator.restorePopRanges((ControlFlowGraph)object);
        if (DecompilerContext.getOption("rer")) {
            ExceptionDeobfuscator.removeEmptyRanges((ControlFlowGraph)object);
        }
        if (DecompilerContext.getOption("ner")) {
            DeadCodeHelper.incorporateValueReturns((ControlFlowGraph)object);
        }
        ExceptionDeobfuscator.insertEmptyExceptionHandlerBlocks((ControlFlowGraph)object);
        DeadCodeHelper.mergeBasicBlocks((ControlFlowGraph)object);
        DecompilerContext.getCountercontainer().setCounter(2, structMethod.getLocalVariables());
        if (ExceptionDeobfuscator.hasObfuscatedExceptions((ControlFlowGraph)object)) {
            DecompilerContext.getLogger().writeMessage("Heavily obfuscated exception ranges found!", 3);
        }
        object2 = DomHelper.graphToStatement((ControlFlowGraph)object);
        if (!DecompilerContext.getOption("FINALLY_CATCHALL")) {
            Object object3;
            object3 = new FinallyProcessor(varProcessor);
            while (((FinallyProcessor)object3).processStatementEx(structMethod, (RootStatement)object2, (ControlFlowGraph)object)) {
                object2 = DomHelper.graphToStatement((ControlFlowGraph)object);
            }
        }
        DomHelper.removeSynchronizedHandler((Statement)object2);
        SequenceHelper.condenseSequences((Statement)object2);
        ClearStructHelper.clearStatements((RootStatement)object2);
        new ExprProcessor().processStatement((RootStatement)object2, structClass.getPool());
        do {
            new StackVarsProcessor().simplifyStackVars((RootStatement)object2, structMethod);
            varProcessor.setVarVersions((RootStatement)object2);
        } while (new PPandMMHelper().findPPandMM((RootStatement)object2));
        do {
            LabelHelper.cleanUpEdges((RootStatement)object2);
            do {
                MergeHelper.enhanceLoops((Statement)object2);
            } while (LoopExtractHelper.extractLoops((Statement)object2) || IfHelper.mergeAllIfs((RootStatement)object2));
            LabelHelper.setRetEdgesUnlabeled((RootStatement)object2);
        } while (InlineSingleBlockHelper.inlineSingleBlocks((RootStatement)object2) || !bl && ExitHelper.condenseExits((RootStatement)object2));
        ExitHelper.removeRedundantReturns((RootStatement)object2);
        SecondaryFunctionsHelper.identifySecondaryFunctions((Statement)object2);
        varProcessor.setVarDefinitions((Statement)object2);
        LabelHelper.replaceContinueWithBreak((Statement)object2);
        structMethod.releaseResources();
        return object2;
    }

    public final RootStatement getRoot() {
        return this.root;
    }

    public final Throwable getError() {
        return this.error;
    }
}

