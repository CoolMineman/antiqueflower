/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import java.util.ArrayList;

public class ListStack
extends ArrayList {
    protected int pointer = 0;

    public ListStack() {
    }

    public ListStack(ArrayList arrayList) {
        super(arrayList);
    }

    public ListStack clone() {
        ListStack listStack = new ListStack(this);
        new ListStack(this).pointer = this.pointer;
        return listStack;
    }

    public Object push(Object object) {
        this.add(object);
        ++this.pointer;
        return object;
    }

    public Object pop() {
        --this.pointer;
        Object object = this.get(this.pointer);
        this.remove(this.pointer);
        return object;
    }

    public final Object pop(int n) {
        Object object = null;
        while (n > 0) {
            object = this.pop();
            --n;
        }
        return object;
    }

    public final void removeMultiple(int n) {
        while (n > 0) {
            --this.pointer;
            this.remove(this.pointer);
            --n;
        }
    }

    public final int getPointer() {
        return this.pointer;
    }

    public Object get(int n) {
        return super.get(n);
    }

    public Object set(int n, Object object) {
        return super.set(n, object);
    }

    public final Object getByOffset(int n) {
        return this.get(this.pointer + n);
    }

    public final void insertByOffset(int n, Object object) {
        this.add(this.pointer + n, object);
        ++this.pointer;
    }

    public void clear() {
        super.clear();
        this.pointer = 0;
    }
}

