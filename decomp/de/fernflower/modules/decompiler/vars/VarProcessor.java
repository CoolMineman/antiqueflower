/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarDefinitionHelper;
import de.fernflower.modules.decompiler.vars.VarProcessor$1;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.decompiler.vars.VarVersionsProcessor;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class VarProcessor {
    private HashMap mapVarNames = new HashMap();
    private VarVersionsProcessor varvers;
    private HashMap thisvars = new HashMap();
    private HashSet externvars = new HashSet();

    public final void setVarVersions(RootStatement rootStatement) {
        this.varvers = new VarVersionsProcessor();
        this.varvers.setVarVersions(rootStatement);
    }

    public final void setVarDefinitions(Statement statement) {
        this.mapVarNames = new HashMap();
        new VarDefinitionHelper(statement, (StructMethod)DecompilerContext.getProperty("CURRENT_METHOD"), this).setVarDefinitions();
    }

    public final void setDebugVarNames(HashMap hashMap) {
        if (this.varvers == null) {
            return;
        }
        HashMap hashMap2 = this.varvers.getMapOriginalVarIndices();
        Object object = new ArrayList(this.mapVarNames.keySet());
        Collections.sort(object, new VarProcessor$1());
        HashMap<String, Integer> hashMap3 = new HashMap<String, Integer>();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (VarVersionPaar)iterator.next();
            String string = (String)this.mapVarNames.get(object);
            Integer n = (Integer)hashMap2.get(((VarVersionPaar)object).var);
            if (n != null && hashMap.containsKey(n)) {
                string = (String)hashMap.get(n);
            }
            hashMap3.put(string, (n = (Integer)hashMap3.get(string)) == null ? (n = new Integer(0)) : (n = Integer.valueOf(n + 1)));
            if (n > 0) {
                string = String.valueOf(string) + String.valueOf(n);
            }
            this.mapVarNames.put(object, string);
        }
    }

    public final void refreshVarNames(VarNamesCollector varNamesCollector) {
        Map.Entry entry2 = null;
        for (Map.Entry entry2 : new HashMap(this.mapVarNames).entrySet()) {
            this.mapVarNames.put((VarVersionPaar)entry2.getKey(), varNamesCollector.getFreeName((String)entry2.getValue()));
        }
    }

    public final VarType getVarType(VarVersionPaar varVersionPaar) {
        if (this.varvers == null) {
            return null;
        }
        return this.varvers.getVarType(varVersionPaar);
    }

    public final void setVarType(VarVersionPaar varVersionPaar, VarType varType) {
        this.varvers.setVarType(varVersionPaar, varType);
    }

    public final String getVarName(VarVersionPaar varVersionPaar) {
        if (this.mapVarNames == null) {
            return null;
        }
        return (String)this.mapVarNames.get(varVersionPaar);
    }

    public final void setVarName(VarVersionPaar varVersionPaar, String string) {
        this.mapVarNames.put(varVersionPaar, string);
    }

    public final int getVarFinal(VarVersionPaar varVersionPaar) {
        if (this.varvers == null) {
            return 3;
        }
        return this.varvers.getVarFinal(varVersionPaar);
    }

    public final void setVarFinal(VarVersionPaar varVersionPaar) {
        this.varvers.setVarFinal(varVersionPaar);
    }

    public final HashMap getThisvars() {
        return this.thisvars;
    }

    public final HashSet getExternvars() {
        return this.externvars;
    }
}

