/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.LinkedList;

public final class ClearStructHelper {
    public static void clearStatements(RootStatement statement) {
        LinkedList<RootStatement> linkedList = new LinkedList<RootStatement>();
        linkedList.add((RootStatement)statement);
        while (!linkedList.isEmpty()) {
            statement = (Statement)linkedList.removeFirst();
            statement.clearTempInformation();
            linkedList.addAll(statement.getStats());
        }
    }
}

