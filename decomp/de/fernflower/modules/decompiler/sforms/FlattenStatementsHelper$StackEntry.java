/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.Statement;

final class FlattenStatementsHelper$StackEntry {
    public CatchAllStatement catchstatement;
    public boolean state;
    public int edgetype;
    public boolean isFinallyExceptionPath;
    public Statement destination;
    public Statement finallyShortRangeEntry;
    public Statement finallyLongRangeEntry;
    public DirectNode finallyShortRangeSource;
    public DirectNode finallyLongRangeSource;

    public FlattenStatementsHelper$StackEntry(CatchAllStatement catchAllStatement, boolean bl, int n, Statement statement, Statement statement2, Statement statement3, DirectNode directNode, DirectNode directNode2, boolean bl2) {
        this.catchstatement = catchAllStatement;
        this.state = bl;
        this.edgetype = n;
        this.isFinallyExceptionPath = bl2;
        this.destination = statement;
        this.finallyShortRangeEntry = statement2;
        this.finallyLongRangeEntry = statement3;
        this.finallyShortRangeSource = directNode;
        this.finallyLongRangeSource = directNode2;
    }

    public FlattenStatementsHelper$StackEntry(CatchAllStatement catchAllStatement, boolean bl) {
        this(catchAllStatement, bl, -1, null, null, null, null, null, false);
    }
}

