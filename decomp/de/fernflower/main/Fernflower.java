/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassesProcessor;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.main.extern.IBytecodeProvider;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.modules.renamer.IdentifierConverter;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructContext;
import de.fernflower.struct.lazy.LazyLoader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;

public final class Fernflower {
    private StructContext structcontext;
    private ClassesProcessor clprocessor;

    public Fernflower(IBytecodeProvider object, IDecompilatSaver iDecompilatSaver, HashMap hashMap) {
        this.structcontext = object = new StructContext(iDecompilatSaver, this, new LazyLoader((IBytecodeProvider)object));
        DecompilerContext.initContext(hashMap);
        DecompilerContext.setCountercontainer(new CounterContainer());
    }

    public final void decompileContext() {
        if (DecompilerContext.getOption("ren")) {
            new IdentifierConverter().rename(this.structcontext);
        }
        this.clprocessor = new ClassesProcessor(this.structcontext);
        DecompilerContext.setClassprocessor(this.clprocessor);
        DecompilerContext.setStructcontext(this.structcontext);
        this.structcontext.saveContext();
    }

    public final String getClassEntryName(StructClass structClass, String string) {
        if (((ClassesProcessor$ClassNode)string2.clprocessor.getMapRootClasses().get((Object)structClass.qualifiedName)).type != 0) {
            return null;
        }
        if (DecompilerContext.getOption("ren")) {
            String string2 = structClass.qualifiedName.substring(structClass.qualifiedName.lastIndexOf(47) + 1);
            return String.valueOf(string.substring(0, string.lastIndexOf(47) + 1)) + (String)string2 + ".java";
        }
        return String.valueOf(string.substring(0, string.lastIndexOf(".class"))) + ".java";
    }

    public final StructContext getStructcontext() {
        return this.structcontext;
    }

    public final String getClassContent(StructClass structClass) {
        String string = null;
        try {
            StringWriter stringWriter = new StringWriter();
            this.clprocessor.writeClass(structClass, new BufferedWriter(stringWriter));
            string = stringWriter.toString();
        }
        catch (ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            DecompilerContext.getLogger().writeMessage("Class " + structClass.qualifiedName + " couldn't be fully decompiled.", 4);
        }
        return string;
    }
}

