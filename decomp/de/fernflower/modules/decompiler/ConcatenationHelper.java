/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.List;

public final class ConcatenationHelper {
    private static final VarType builderType = new VarType(8, 0, "java/lang/StringBuilder");
    private static final VarType bufferType = new VarType(8, 0, "java/lang/StringBuffer");

    /*
     * Unable to fully structure code
     */
    public static Exprent contractStringConcat(Exprent var0) {
        var1_2 = null;
        var2_3 = null;
        if (var0.type == 8 && "toString".equals((var3_4 = (InvocationExprent)var0).getName())) {
            if ("java/lang/StringBuilder".equals(var3_4.getClassname())) {
                var2_3 = ConcatenationHelper.builderType;
            } else if ("java/lang/StringBuffer".equals(var3_4.getClassname())) {
                var2_3 = ConcatenationHelper.bufferType;
            }
            if (var2_3 != null) {
                var1_2 = var3_4.getInstance();
            }
        }
        if (var1_2 == null) {
            return var0;
        }
        var3_4 = new ArrayList<E>();
        do {
            var4_5 = 0;
            switch (var1_2.type) {
                case 8: {
                    var5_6 = (InvocationExprent)var1_2;
                    var7_11 = var2_3;
                    var6_9 = var5_6;
                    if (!"append".equals(var6_9.getName())) ** GOTO lbl-1000
                    var8_12 = var6_9.getDescriptor();
                    if (!var8_12.ret.equals(var7_11) || var8_12.params.length != 1) ** GOTO lbl-1000
                    var8_12 = var8_12.params[0];
                    switch (var8_12.type) {
                        case 8: {
                            if (!var8_12.equals(VarType.VARTYPE_STRING) && !var8_12.equals(VarType.VARTYPE_OBJECT)) ** GOTO lbl31
                        }
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: 
                        case 5: 
                        case 7: {
                            v0 = true;
                            break;
                        }
lbl31:
                        // 2 sources

                        default: lbl-1000:
                        // 3 sources

                        {
                            v0 = false;
                        }
                    }
                    if (!v0) break;
                    var3_4.add(0, (Exprent)var5_6.getLstParameters().get(0));
                    var1_2 = var5_6.getInstance();
                    var4_5 = 1;
                    break;
                }
                case 10: {
                    var5_6 = (NewExprent)var1_2;
                    var7_11 = var2_3;
                    var6_9 = var5_6;
                    if (!var5_6.getNewtype().equals(var7_11)) ** GOTO lbl-1000
                    var8_12 = var6_9.getConstructor().getDescriptor().params;
                    if (var6_9.getConstructor().getDescriptor().params.length == 0 || ((VarType[])var8_12).length == 1 && var8_12[0].equals(VarType.VARTYPE_STRING)) {
                        v1 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v1 = false;
                    }
                    if (!v1) break;
                    if (var5_6.getConstructor().getDescriptor().params.length == 1) {
                        var3_4.add(0, (Exprent)var5_6.getConstructor().getLstParameters().get(0));
                    }
                    var4_5 = 2;
                }
            }
            if (var4_5 != 0) continue;
            return var0;
        } while (var4_5 != 2);
        var4_5 = 0;
        var5_7 = 0;
        while (var5_7 < var3_4.size() && var5_7 < 2) {
            if (((Exprent)var3_4.get(var5_7)).getExprType().equals(VarType.VARTYPE_STRING)) {
                var4_5 |= var5_7 + 1;
            }
            ++var5_7;
        }
        if (var4_5 == 0) {
            var3_4.add(0, new ConstExprent(VarType.VARTYPE_STRING, ""));
        }
        var5_7 = 0;
        while (var5_7 < var3_4.size()) {
            var6_9 = (Exprent)var3_4.get(var5_7);
            if (var6_9.type != 8 || !"valueOf".equals((var7_11 = (InvocationExprent)var6_9).getName()) || !"java/lang/String".equals(var7_11.getClassname())) ** GOTO lbl-1000
            var8_12 = var7_11.getDescriptor();
            if (var8_12.params.length != 1) ** GOTO lbl-1000
            var8_12 = var8_12.params[0];
            switch (var8_12.type) {
                case 8: {
                    if (!var8_12.equals(VarType.VARTYPE_OBJECT)) ** GOTO lbl77
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 7: {
                    v2 = (Exprent)var7_11.getLstParameters().get(0);
                    break;
                }
lbl77:
                // 2 sources

                default: lbl-1000:
                // 3 sources

                {
                    v2 = var6_9 = var6_9;
                }
            }
            if (!(var0_1 = var5_7 > 1)) {
                v3 = var0_1 = var6_9.getExprType().equals(VarType.VARTYPE_STRING) != false || var4_5 != var5_7 + 1;
                if (var5_7 == 0) {
                    var4_5 &= 2;
                }
            }
            if (var0_1) {
                var3_4.set(var5_7, var6_9);
            }
            ++var5_7;
        }
        var5_8 = (Exprent)var3_4.get(0);
        var6_10 = 1;
        while (var6_10 < var3_4.size()) {
            var0 = new ArrayList<Exprent>();
            var0.add(var5_8);
            var0.add((Exprent)var3_4.get(var6_10));
            var5_8 = new FunctionExprent(50, (List)var0);
            ++var6_10;
        }
        return var5_8;
    }
}

