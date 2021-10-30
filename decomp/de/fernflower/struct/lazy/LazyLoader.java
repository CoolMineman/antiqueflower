/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.lazy;

import de.fernflower.main.extern.IBytecodeProvider;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.lazy.LazyLoader$Link;
import de.fernflower.util.DataInputFullStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class LazyLoader {
    private HashMap mapClassLinks = new HashMap();
    private IBytecodeProvider provider;

    public LazyLoader(IBytecodeProvider iBytecodeProvider) {
        this.provider = iBytecodeProvider;
    }

    public final void addClassLink(String string, LazyLoader$Link lazyLoader$Link) {
        this.mapClassLinks.put(string, lazyLoader$Link);
    }

    public final void removeClassLink(String string) {
        this.mapClassLinks.remove(string);
    }

    public final LazyLoader$Link getClassLink(String string) {
        return (LazyLoader$Link)this.mapClassLinks.get(string);
    }

    public final ConstantPool loadPool(String string) {
        block3: {
            try {
                DataInputFullStream dataInputFullStream = ((LazyLoader)((Object)dataInputFullStream)).getClassStream(string);
                if (dataInputFullStream != null) break block3;
                return null;
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
        }
        dataInputFullStream.skip(8L);
        return new ConstantPool(dataInputFullStream);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public final byte[] loadBytecode(StructMethod structMethod, int n) {
        try {
            int n2;
            int n3;
            DataInputFullStream dataInputFullStream = ((LazyLoader)((Object)dataInputFullStream)).getClassStream(structMethod.getClassStruct().qualifiedName);
            if (dataInputFullStream == null) {
                return null;
            }
            Object object = null;
            dataInputFullStream.skip(8L);
            object = structMethod.getClassStruct().getPool();
            if (object == null) {
                object = new ConstantPool(dataInputFullStream);
            } else {
                DataInputFullStream dataInputFullStream2 = dataInputFullStream;
                n3 = dataInputFullStream2.readUnsignedShort();
                n2 = 1;
                while (n2 < n3) {
                    switch (dataInputFullStream2.readUnsignedByte()) {
                        case 1: {
                            dataInputFullStream2.readUTF();
                            break;
                        }
                        case 3: 
                        case 4: 
                        case 9: 
                        case 10: 
                        case 11: 
                        case 12: {
                            dataInputFullStream2.skip(4L);
                            break;
                        }
                        case 5: 
                        case 6: {
                            dataInputFullStream2.skip(8L);
                            ++n2;
                            break;
                        }
                        case 7: 
                        case 8: {
                            dataInputFullStream2.skip(2L);
                        }
                    }
                    ++n2;
                }
            }
            dataInputFullStream.skip(2L);
            int n4 = dataInputFullStream.readUnsignedShort();
            dataInputFullStream.skip(2L);
            dataInputFullStream.skip(dataInputFullStream.readUnsignedShort() << 1);
            n3 = dataInputFullStream.readUnsignedShort();
            n2 = 0;
            while (n2 < n3) {
                dataInputFullStream.skip(6L);
                LazyLoader.skipAttributes(dataInputFullStream);
                ++n2;
            }
            n3 = dataInputFullStream.readUnsignedShort();
            n2 = 0;
            while (n2 < n3) {
                dataInputFullStream.skip(2L);
                int n5 = dataInputFullStream.readUnsignedShort();
                int n6 = dataInputFullStream.readUnsignedShort();
                Object object2 = ((ConstantPool)object).getClassElement(3, n4, n5, n6);
                String string = object2[0];
                if (structMethod.getName().equals(string)) {
                    object2 = object2[1];
                    if (structMethod.getDescriptor().equals(object2)) {
                        int n7 = dataInputFullStream.readUnsignedShort();
                        n4 = 0;
                        while (n4 < n7) {
                            int n8 = dataInputFullStream.readUnsignedShort();
                            String string2 = null;
                            string2 = (String)object.getPrimitiveConstant((int)n8).value;
                            if ("Code".equals(string2)) {
                                dataInputFullStream.skip(12L);
                                object = new byte[n];
                                dataInputFullStream.readFull((byte[])object);
                                return object;
                            }
                            dataInputFullStream.skip(dataInputFullStream.readInt());
                            ++n4;
                        }
                        return null;
                    }
                }
                LazyLoader.skipAttributes(dataInputFullStream);
                ++n2;
            }
            return null;
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public final DataInputFullStream getClassStream(String string, String string2) {
        InputStream inputStream = ((LazyLoader)inputStream).provider.getBytecodeStream(string, string2);
        if (inputStream == null) {
            return null;
        }
        return new DataInputFullStream(inputStream);
    }

    public final DataInputFullStream getClassStream(String object) {
        if ((object = (LazyLoader$Link)this.mapClassLinks.get(object)) == null) {
            return null;
        }
        return this.getClassStream(((LazyLoader$Link)object).externPath, ((LazyLoader$Link)object).internPath);
    }

    private static void skipAttributes(DataInputFullStream dataInputFullStream) {
        int n = dataInputFullStream.readUnsignedShort();
        int n2 = 0;
        while (n2 < n) {
            dataInputFullStream.skip(2L);
            dataInputFullStream.skip(dataInputFullStream.readInt());
            ++n2;
        }
    }
}

