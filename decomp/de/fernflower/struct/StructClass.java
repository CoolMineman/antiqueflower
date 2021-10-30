/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct;

import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.lazy.LazyLoader;
import de.fernflower.util.DataInputFullStream;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;
import java.io.InputStream;

public final class StructClass {
    public int minor_version;
    public int major_version;
    public int access_flags;
    public int this_class;
    private int super_class;
    private PrimitiveConstant thisClass;
    public PrimitiveConstant superClass;
    public String qualifiedName;
    private ConstantPool pool;
    private int[] interfaces;
    private String[] interfaceNames;
    private VBStyleCollection fields = new VBStyleCollection();
    private VBStyleCollection methods = new VBStyleCollection();
    private VBStyleCollection attributes = new VBStyleCollection();
    private boolean own = true;
    private LazyLoader loader;

    public StructClass(InputStream inputStream, boolean bl, LazyLoader lazyLoader) {
        this(new DataInputFullStream(inputStream), bl, lazyLoader);
    }

    public StructClass(DataInputFullStream dataInputFullStream, boolean bl, LazyLoader lazyLoader) {
        this.own = bl;
        this.loader = lazyLoader;
        this.initStruct(dataInputFullStream);
    }

    public final StructField getField(String string, String string2) {
        return (StructField)this.fields.getWithKey(InterpreterUtil.makeUniqueKey(string, string2));
    }

    public final StructMethod getMethod(String string, String string2) {
        return (StructMethod)this.methods.getWithKey(InterpreterUtil.makeUniqueKey(string, string2));
    }

    public final String getInterface(int n) {
        return this.interfaceNames[n];
    }

    public final void releaseResources() {
        if (this.loader != null) {
            this.pool = null;
        }
    }

    private void initStruct(DataInputFullStream dataInputFullStream) {
        dataInputFullStream.skip(4L);
        this.minor_version = dataInputFullStream.readUnsignedShort();
        this.major_version = dataInputFullStream.readUnsignedShort();
        this.pool = new ConstantPool(dataInputFullStream);
        this.access_flags = dataInputFullStream.readUnsignedShort();
        this.this_class = dataInputFullStream.readUnsignedShort();
        this.thisClass = this.pool.getPrimitiveConstant(this.this_class);
        this.qualifiedName = (String)this.thisClass.value;
        this.super_class = dataInputFullStream.readUnsignedShort();
        this.superClass = this.pool.getPrimitiveConstant(this.super_class);
        int n = dataInputFullStream.readUnsignedShort();
        int[] nArray = new int[n];
        Object object = new String[n];
        int n2 = 0;
        while (n2 < n) {
            nArray[n2] = dataInputFullStream.readUnsignedShort();
            object[n2] = (String)this.pool.getPrimitiveConstant((int)nArray[n2]).value;
            ++n2;
        }
        this.interfaces = nArray;
        this.interfaceNames = object;
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        n = dataInputFullStream.readUnsignedShort();
        int n3 = 0;
        while (n3 < n) {
            object = new StructField();
            new StructField().access_flags = dataInputFullStream.readUnsignedShort();
            ((StructField)object).name_index = dataInputFullStream.readUnsignedShort();
            ((StructField)object).descriptor_index = dataInputFullStream.readUnsignedShort();
            ((StructField)object).initStrings(this.pool, this.this_class);
            ((StructField)object).setAttributes(this.readAttributes(dataInputFullStream));
            vBStyleCollection.addWithKey(object, InterpreterUtil.makeUniqueKey(((StructField)object).getName(), ((StructField)object).getDescriptor()));
            ++n3;
        }
        this.fields = vBStyleCollection;
        n = dataInputFullStream.readUnsignedShort();
        n3 = 0;
        while (n3 < n) {
            object = new StructMethod(dataInputFullStream, this.own, this);
            this.methods.addWithKey(object, InterpreterUtil.makeUniqueKey(((StructMethod)object).getName(), ((StructMethod)object).getDescriptor()));
            ++n3;
        }
        this.attributes = this.readAttributes(dataInputFullStream);
        if (this.loader != null) {
            this.pool = null;
        }
    }

    private VBStyleCollection readAttributes(DataInputFullStream dataInputFullStream) {
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        int n = dataInputFullStream.readUnsignedShort();
        int n2 = 0;
        while (n2 < n) {
            int n3 = dataInputFullStream.readUnsignedShort();
            Object object = null;
            object = (String)this.pool.getPrimitiveConstant((int)n3).value;
            StructGeneralAttribute structGeneralAttribute = StructGeneralAttribute.getMatchingAttributeInstance(n3, (String)object);
            if (structGeneralAttribute != null) {
                object = new byte[dataInputFullStream.readInt()];
                dataInputFullStream.readFull((byte[])object);
                structGeneralAttribute.setInfo((byte[])object);
                structGeneralAttribute.initContent(this.pool);
                vBStyleCollection.addWithKey(structGeneralAttribute, structGeneralAttribute.getName());
            } else {
                dataInputFullStream.skip(dataInputFullStream.readInt());
            }
            ++n2;
        }
        return vBStyleCollection;
    }

    public final ConstantPool getPool() {
        if (this.pool == null && this.loader != null) {
            this.pool = this.loader.loadPool(this.qualifiedName);
        }
        return this.pool;
    }

    public final int[] getInterfaces() {
        return this.interfaces;
    }

    public final String[] getInterfaceNames() {
        return this.interfaceNames;
    }

    public final VBStyleCollection getMethods() {
        return this.methods;
    }

    public final VBStyleCollection getFields() {
        return this.fields;
    }

    public final VBStyleCollection getAttributes() {
        return this.attributes;
    }

    public final boolean isOwn() {
        return this.own;
    }

    public final LazyLoader getLoader() {
        return this.loader;
    }
}

