/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassWriter;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.lex.fffixer.Util;

public final class VarExprent
extends Exprent
implements Util.Indexed {
    private int index;
    private VarType vartype;
    private boolean definition = false;
    private VarProcessor processor;
    private int version = 0;
    private boolean classdef = false;
    private boolean stack = false;

    public VarExprent(int n, VarType varType, VarProcessor varProcessor) {
        this.type = 12;
        this.index = n;
        this.vartype = varType;
        this.processor = varProcessor;
    }

    public final VarType getExprType() {
        return this.getVartype();
    }

    public final int getExprentUse() {
        return 3;
    }

    public final List getAllExprents() {
        return new ArrayList();
    }

    public final Exprent copy() {
        VarExprent varExprent = new VarExprent(this.index, this.getVartype(), this.processor);
        new VarExprent(this.index, this.getVartype(), this.processor).definition = this.definition;
        varExprent.version = this.version;
        varExprent.classdef = this.classdef;
        varExprent.stack = this.stack;
        return varExprent;
    }

    public final String toJava(int n) {
        if (((VarExprent)bufferedWriter).classdef) {
            ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(bufferedWriter.vartype.value);
            StringWriter stringWriter = new StringWriter();
            BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
            ClassWriter classWriter = new ClassWriter();
            try {
                classWriter.classToJava(classesProcessor$ClassNode, bufferedWriter, n);
                bufferedWriter.flush();
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            return stringWriter.toString();
        }
        String string = null;
        if (((VarExprent)bufferedWriter).processor != null) {
            string = ((VarExprent)bufferedWriter).processor.getVarName(new VarVersionPaar(((VarExprent)bufferedWriter).index, ((VarExprent)bufferedWriter).version));
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (((VarExprent)bufferedWriter).definition) {
            if (((VarExprent)bufferedWriter).processor != null && ((VarExprent)bufferedWriter).processor.getVarFinal(new VarVersionPaar(((VarExprent)bufferedWriter).index, ((VarExprent)bufferedWriter).version)) == 2) {
                stringBuilder.append("final ");
            }
            stringBuilder.append(String.valueOf(ExprProcessor.getCastTypeName(((VarExprent)((Object)bufferedWriter)).getVartype())) + " ");
        }
        stringBuilder.append(string == null ? "var" + ((VarExprent)bufferedWriter).index + (((VarExprent)bufferedWriter).version == 0 ? "" : "_" + ((VarExprent)bufferedWriter).version) : string);
        return stringBuilder.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof VarExprent) {
            object = (VarExprent)object;
            return this.index == ((VarExprent)object).index && this.version == ((VarExprent)object).version && InterpreterUtil.equalObjects(this.getVartype(), ((VarExprent)object).getVartype());
        }
        return false;
    }

    public final int getIndex0() {
        return this.index;
    }

    public final void setIndex(int n) {
        this.index = n;
    }

    public final VarType getVartype() {
        VarType varType = null;
        if (this.processor != null) {
            varType = this.processor.getVarType(new VarVersionPaar(this.index, this.version));
        }
        if (varType == null || this.vartype != null && this.vartype.type != 17) {
            varType = this.vartype;
        }
        if (varType == null) {
            return VarType.VARTYPE_UNKNOWN;
        }
        return varType;
    }

    public final void setVartype(VarType varType) {
        this.vartype = varType;
    }

    public final boolean isDefinition() {
        return this.definition;
    }

    public final void setDefinition() {
        this.definition = true;
    }

    public final VarProcessor getProcessor() {
        return this.processor;
    }

    public final int getVersion() {
        return this.version;
    }

    public final void setVersion(int n) {
        this.version = n;
    }

    public final boolean isClassdef() {
        return this.classdef;
    }

    public final void setClassdef() {
        this.classdef = true;
    }

    public final boolean isStack() {
        return this.stack;
    }

    public final void setStack() {
        this.stack = true;
    }

    public final int getIndex() {
        if (!this.definition) {
            return -1;
        }
        return this.index;
    }
}

