/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.List;

public final class InterpreterUtil {
    public static void copyFile(File object, File object2) {
        object = new FileInputStream((File)object).getChannel();
        object2 = new FileOutputStream((File)object2).getChannel();
        try {
            int n = 67076096;
            long l = ((FileChannel)object).size();
            long l2 = 0L;
            while (l2 < l) {
                l2 += ((FileChannel)object).transferTo(l2, n, (WritableByteChannel)object2);
            }
        }
        finally {
            if (object != null) {
                ((AbstractInterruptibleChannel)object).close();
            }
            if (object2 != null) {
                ((AbstractInterruptibleChannel)object2).close();
            }
        }
    }

    public static void copyInputStream(InputStream inputStream, OutputStream outputStream) {
        int n;
        byte[] byArray = new byte[1024];
        while ((n = inputStream.read(byArray)) >= 0) {
            outputStream.write(byArray, 0, n);
        }
    }

    public static String getIndentString(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        while (n-- > 0) {
            stringBuffer.append("   ");
        }
        return stringBuffer.toString();
    }

    public static boolean equalObjects(Object object, Object object2) {
        if (object == null) {
            return object2 == null;
        }
        return object.equals(object2);
    }

    public static boolean equalObjectArrays(Object[] objectArray, Object[] objectArray2) {
        if (objectArray == null) {
            return InterpreterUtil.equalObjects(objectArray, objectArray2);
        }
        if (objectArray.length != objectArray2.length) {
            return false;
        }
        int n = 0;
        while (n < objectArray.length) {
            if (!InterpreterUtil.equalObjects(objectArray[n], objectArray2[n])) {
                return false;
            }
            ++n;
        }
        return true;
    }

    public static boolean equalLists(List list, List list2) {
        if (list == null) {
            return list2 == null;
        }
        if (list2 == null) {
            return list == null;
        }
        if (list.size() == list2.size()) {
            int n = 0;
            while (n < list.size()) {
                if (!InterpreterUtil.equalObjects(list.get(n), list2.get(n))) {
                    return false;
                }
                ++n;
            }
            return true;
        }
        return false;
    }

    public static String charToUnicodeLiteral(int n) {
        String string = Integer.toHexString(n);
        string = ("0000" + string).substring(string.length());
        return "\\u" + string;
    }

    public static String makeUniqueKey(String string, String string2) {
        return String.valueOf(string) + " " + string2;
    }
}

