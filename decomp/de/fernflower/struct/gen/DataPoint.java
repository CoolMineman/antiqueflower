/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen;

import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.ListStack;
import java.util.ArrayList;
import java.util.List;

public final class DataPoint {
    private List localVariables = new ArrayList();
    private ListStack stack = new ListStack();

    public final void setVariable(int n, VarType varType) {
        if (n >= this.localVariables.size()) {
            int n2 = this.localVariables.size();
            while (n2 <= n) {
                this.localVariables.add(new VarType(14));
                ++n2;
            }
        }
        this.localVariables.set(n, varType);
    }

    public final VarType getVariable(int n) {
        if (n < this.localVariables.size()) {
            return (VarType)this.localVariables.get(n);
        }
        if (n < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new VarType(14);
    }

    public final DataPoint copy() {
        DataPoint dataPoint = new DataPoint();
        new DataPoint().localVariables = new ArrayList(this.localVariables);
        dataPoint.stack = this.stack.clone();
        return dataPoint;
    }

    public static DataPoint getInitialDataPoint(StructMethod structMethod) {
        DataPoint dataPoint = new DataPoint();
        MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor(structMethod.getDescriptor());
        int n = 0;
        if ((structMethod.getAccessFlags() & 8) == 0) {
            ++n;
            dataPoint.setVariable(0, new VarType(8, 0, null));
        }
        int n2 = 0;
        while (n2 < methodDescriptor.params.length) {
            VarType varType = methodDescriptor.params[n2];
            dataPoint.setVariable(n++, varType);
            if (varType.stack_size == 2) {
                dataPoint.setVariable(n++, new VarType(12));
            }
            ++n2;
        }
        return dataPoint;
    }

    public final List getLocalVariables() {
        return this.localVariables;
    }

    public final void setLocalVariables(List list) {
        this.localVariables = list;
    }

    public final ListStack getStack() {
        return this.stack;
    }
}

