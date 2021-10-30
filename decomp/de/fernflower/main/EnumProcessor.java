/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.util.InterpreterUtil;

public final class EnumProcessor {
    /*
     * Unable to fully structure code
     */
    public static void clearEnum(ClassWrapper var0) {
        var1_1 = var0.getClassStruct();
        for (Object var2_3 : var1_1.getMethods()) {
            var4_4 = var2_3.getName();
            var5_5 = 0;
            if ("values".equals(var4_4)) {
                var5_5 = 1;
            } else if ("valueOf".equals(var4_4)) {
                var5_5 = 2;
            }
            if (var5_5 <= 0) continue;
            var6_9 = null;
            var6_9 = var2_3.getDescriptor().split("[()]")[1];
            if ((var5_5 != 1 || var6_9.length() != 0) && (var5_5 != 2 || !"Ljava/lang/String;".equals(var6_9))) continue;
            var0.getHideMembers().add(InterpreterUtil.makeUniqueKey((String)var4_4, var2_3.getDescriptor()));
        }
        for (Object var2_3 : var0.getMethods()) {
            if (!"<init>".equals(var2_3.methodStruct.getName())) continue;
            var4_4 = EnumProcessor.findFirstData(var2_3.root);
            if (var4_4 == null || var4_4.getExprents().isEmpty()) {
                return;
            }
            var5_7 = (Exprent)var4_4.getExprents().get(0);
            if (var5_7.type != 8) continue;
            v0 = (InvocationExprent)var5_7;
            var6_9 = null;
            var6_9 = var0;
            var5_7 = var2_3;
            var2_3 = v0;
            if (v0.getFunctype() != 2 || var2_3.getInstance().type != 12) ** GOTO lbl-1000
            var7_10 = (VarExprent)var2_3.getInstance();
            var8_11 = new VarVersionPaar(var7_10);
            if ((String)var5_7.varproc.getThisvars().get(var8_11) != null && !var6_9.getClassStruct().qualifiedName.equals(var2_3.getClassname())) {
                v1 = true;
            } else lbl-1000:
            // 2 sources

            {
                v1 = false;
            }
            if (!v1) continue;
            var4_4.getExprents().remove(0);
        }
        for (Object var2_3 : var1_1.getFields()) {
            if ((var2_3.access_flags & 16384) == 0 || (var4_4 = (Exprent)var0.getStaticFieldInitializers().getWithKey(InterpreterUtil.makeUniqueKey(var2_3.getName(), var2_3.getDescriptor()))) == null || var4_4.type != 10 || !(var5_8 = (NewExprent)var4_4).isAnonymous()) continue;
            var6_9 = null;
            var2_3 = ((ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get((Object)var5_8.getNewtype().value)).wrapper;
            var5_8 = var2_3.getClassStruct();
            for (Object var6_9 : var5_8.getFields()) {
                if ((var6_9.access_flags & 4096) == 0) continue;
                var8_11 = null;
                var8_11 = FieldDescriptor.parseDescriptor((String)var6_9.getDescriptor()).type;
                if (var8_11.type != 8 || var8_11.arraydim != 1 || !var5_8.qualifiedName.equals(var8_11.value)) continue;
                var2_3.getHideMembers().add(InterpreterUtil.makeUniqueKey(var6_9.getName(), var6_9.getDescriptor()));
            }
        }
    }

    private static Statement findFirstData(Statement statement) {
        if (statement.getExprents() != null) {
            return statement;
        }
        if (statement.isLabeled()) {
            return null;
        }
        switch (statement.type) {
            case 2: 
            case 6: 
            case 10: 
            case 13: 
            case 15: {
                return EnumProcessor.findFirstData(statement.getFirst());
            }
        }
        return null;
    }
}

