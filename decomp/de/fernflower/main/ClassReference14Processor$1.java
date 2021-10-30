/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassReference14Processor;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class ClassReference14Processor$1
implements DirectGraph$ExprentIterator {
    private /* synthetic */ ClassReference14Processor this$0;
    private final /* synthetic */ HashMap val$mapClassMeths;
    private final /* synthetic */ HashSet val$setFound;

    ClassReference14Processor$1(ClassReference14Processor classReference14Processor, HashMap hashMap, HashSet hashSet) {
        this.this$0 = classReference14Processor;
        this.val$mapClassMeths = hashMap;
        this.val$setFound = hashSet;
    }

    public final int processExprent(Exprent exprent) {
        for (Map.Entry entry : this.val$mapClassMeths.entrySet()) {
            if (!ClassReference14Processor.access$0(this.this$0, exprent, (ClassWrapper)entry.getKey(), (MethodWrapper)entry.getValue())) continue;
            this.val$setFound.add((ClassWrapper)entry.getKey());
        }
        return 0;
    }
}

