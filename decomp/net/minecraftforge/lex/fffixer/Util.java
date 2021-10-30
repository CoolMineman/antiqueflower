/*
 * Decompiled with CFR 0.151.
 */
package net.minecraftforge.lex.fffixer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Util {
    public static Iterator sortIndexed(Iterator itr) {
        ArrayList list = new ArrayList();
        ArrayList<Indexed> def_dec = new ArrayList<Indexed>();
        int first = -1;
        while (itr.hasNext()) {
            Object i = itr.next();
            if (i instanceof Indexed && ((Indexed)i).getIndex() >= 0) {
                if (first == -1) {
                    first = list.size();
                }
                def_dec.add((Indexed)i);
                continue;
            }
            list.add(i);
        }
        if (def_dec.size() > 0) {
            Collections.sort(def_dec, new Comparator<Indexed>(){

                @Override
                public int compare(Indexed o1, Indexed o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });
            list.addAll(first, def_dec);
        }
        return list.iterator();
    }

    public static <T extends Comparable> Iterator<T> sortComparable(Iterator<T> itr) {
        ArrayList<Comparable> list = new ArrayList<Comparable>();
        while (itr.hasNext()) {
            list.add((Comparable)itr.next());
        }
        Collections.sort(list);
        return list.iterator();
    }

    public static interface Indexed {
        public int getIndex();
    }
}

