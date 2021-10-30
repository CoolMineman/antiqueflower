/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen.generics;

import de.fernflower.struct.gen.generics.GenericType;
import java.util.ArrayList;
import java.util.List;

public final class GenericClassDescriptor {
    public GenericType superclass;
    public List superinterfaces = new ArrayList();
    public List fparameters = new ArrayList();
    public List fbounds = new ArrayList();
}

