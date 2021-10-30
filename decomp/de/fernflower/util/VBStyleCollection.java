/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public final class VBStyleCollection
extends ArrayList {
    private HashMap map = new HashMap();
    private ArrayList lstKeys = new ArrayList();

    public final boolean add(Object object) {
        this.lstKeys.add(null);
        super.add(object);
        return true;
    }

    public final boolean remove(Object object) {
        throw new RuntimeException("not implemented!");
    }

    public final boolean addAll(Collection collection) {
        int n = collection.size() - 1;
        while (n >= 0) {
            this.lstKeys.add(null);
            --n;
        }
        return super.addAll(collection);
    }

    public final void addAllWithKey(Collection collection, Collection collection2) {
        int n = super.size();
        for (Object e : collection2) {
            this.map.put(e, n++);
        }
        super.addAll(collection);
        this.lstKeys.addAll(collection2);
    }

    public final void addWithKey(Object object, Object object2) {
        this.map.put(object2, super.size());
        super.add(object);
        this.lstKeys.add(object2);
    }

    public final Object putWithKey(Object object, Object object2) {
        Integer n = (Integer)this.map.get(object2);
        if (n != null) {
            return super.set(n, object);
        }
        this.addWithKey(object, object2);
        return null;
    }

    public final void add(int n, Object object) {
        this.addToListIndex(n, 1);
        this.lstKeys.add(n, null);
        super.add(n, object);
    }

    public final void addWithKeyAndIndex(int n, Object object, Object object2) {
        this.addToListIndex(n, 1);
        this.map.put(object2, new Integer(n));
        super.add(n, object);
        this.lstKeys.add(n, object2);
    }

    public final void removeWithKey(Object object) {
        int n = (Integer)this.map.get(object);
        this.addToListIndex(n + 1, -1);
        super.remove(n);
        this.lstKeys.remove(n);
        this.map.remove(object);
    }

    public final Object remove(int n) {
        this.addToListIndex(n + 1, -1);
        Object e = this.lstKeys.get(n);
        if (e != null) {
            this.map.remove(e);
        }
        this.lstKeys.remove(n);
        return super.remove(n);
    }

    public final Object getWithKey(Object object) {
        if ((object = (Integer)this.map.get(object)) == null) {
            return null;
        }
        return super.get((Integer)object);
    }

    public final int getIndexByKey(Object object) {
        return (Integer)this.map.get(object);
    }

    public final Object getLast() {
        return super.get(super.size() - 1);
    }

    public final boolean containsKey(Object object) {
        return this.map.containsKey(object);
    }

    public final void clear() {
        this.map.clear();
        this.lstKeys.clear();
        super.clear();
    }

    public final VBStyleCollection clone() {
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        vBStyleCollection.addAll((Collection)new ArrayList(this));
        vBStyleCollection.map = new HashMap(this.map);
        vBStyleCollection.lstKeys = new ArrayList(this.lstKeys);
        return vBStyleCollection;
    }

    public final Object getKey(int n) {
        return this.lstKeys.get(n);
    }

    public final ArrayList getLstKeys() {
        return this.lstKeys;
    }

    private void addToListIndex(int n, int n2) {
        int n3 = this.lstKeys.size() - 1;
        while (n3 >= n) {
            Object e = this.lstKeys.get(n3);
            if (e != null) {
                this.map.put(e, new Integer(n3 + n2));
            }
            --n3;
        }
    }
}

