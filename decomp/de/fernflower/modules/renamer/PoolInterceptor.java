/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.renamer;

import de.fernflower.main.extern.IIdentifierRenamer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class PoolInterceptor {
    private IIdentifierRenamer helper;
    private HashMap mapOldToNewNames = new HashMap();
    private HashMap mapNewToOldNames = new HashMap();
    private HashMap d = new HashMap();

    public PoolInterceptor(IIdentifierRenamer iIdentifierRenamer) {
        this.helper = iIdentifierRenamer;
    }

    public final void getName(String string, String string2) {
        ArrayList<String> arrayList = (ArrayList<String>)this.d.get(string);
        if (arrayList == null) {
            arrayList = new ArrayList<String>();
            this.d.put(string, arrayList);
        }
        arrayList.add(string2);
    }

    public final void addName(String string, String string2) {
        this.mapOldToNewNames.put(string, string2);
        this.mapNewToOldNames.put(string2, string);
    }

    public final String a(int n, String string, String string2, String string3) {
        String string4 = (String)this.mapOldToNewNames.get(n == 1 ? string : String.valueOf(string) + " " + string2 + " " + string3);
        if (string4 == null && (n == 2 || n == 3) && this.helper.toBeRenamed(n, string, string2, string3)) {
            string4 = this.a(string, string2, string3);
        }
        return string4;
    }

    private String a(String object, String string, String string2) {
        String string3 = null;
        if ((object = (List)this.d.get(object)) != null) {
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (String)iterator.next();
                string3 = (String)this.mapOldToNewNames.get(String.valueOf(object) + " " + string + " " + string2);
                if (string3 == null) {
                    string3 = this.a((String)object, string, string2);
                }
                if (string3 != null) break;
            }
        }
        return string3;
    }

    public final String getOldName(String string) {
        return (String)this.mapNewToOldNames.get(string);
    }

    public final IIdentifierRenamer getHelper() {
        return this.helper;
    }
}

