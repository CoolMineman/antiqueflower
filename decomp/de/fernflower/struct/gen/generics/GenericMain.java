/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen.generics;

import de.fernflower.main.DecompilerContext;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.gen.generics.GenericClassDescriptor;
import de.fernflower.struct.gen.generics.GenericFieldDescriptor;
import de.fernflower.struct.gen.generics.GenericMethodDescriptor;
import de.fernflower.struct.gen.generics.GenericType;
import java.util.ArrayList;
import java.util.List;

public final class GenericMain {
    private static final String[] typeNames = new String[]{"byte", "char", "double", "float", "int", "long", "short", "boolean"};

    public static GenericClassDescriptor parseClassSignature(String string) {
        GenericClassDescriptor genericClassDescriptor = new GenericClassDescriptor();
        string = GenericMain.parseFormalParameters(string, genericClassDescriptor.fparameters, genericClassDescriptor.fbounds);
        String string2 = GenericType.getNextType(string);
        genericClassDescriptor.superclass = new GenericType(string2);
        string = string.substring(string2.length());
        while (string.length() > 0) {
            string2 = GenericType.getNextType(string);
            genericClassDescriptor.superinterfaces.add(new GenericType(string2));
            string = string.substring(string2.length());
        }
        return genericClassDescriptor;
    }

    public static GenericFieldDescriptor parseFieldSignature(String string) {
        GenericFieldDescriptor genericFieldDescriptor = new GenericFieldDescriptor();
        new GenericFieldDescriptor().type = new GenericType(string);
        return genericFieldDescriptor;
    }

    public static GenericMethodDescriptor parseMethodSignature(String stringArray) {
        GenericMethodDescriptor genericMethodDescriptor = new GenericMethodDescriptor();
        stringArray = GenericMain.parseFormalParameters((String)stringArray, genericMethodDescriptor.fparameters, genericMethodDescriptor.fbounds);
        int n = stringArray.indexOf(")");
        String string = stringArray.substring(1, n);
        stringArray = stringArray.substring(n + 1);
        while (string.length() > 0) {
            String string2 = GenericType.getNextType(string);
            genericMethodDescriptor.params.add(new GenericType(string2));
            string = string.substring(string2.length());
        }
        String string3 = GenericType.getNextType((String)stringArray);
        genericMethodDescriptor.ret = new GenericType(string3);
        if ((stringArray = stringArray.substring(string3.length())).length() > 0) {
            stringArray = stringArray.split("\\^");
            int n2 = 1;
            while (n2 < stringArray.length) {
                genericMethodDescriptor.exceptions.add(new GenericType(stringArray[n2]));
                ++n2;
            }
        }
        return genericMethodDescriptor;
    }

    /*
     * Enabled aggressive block sorting
     */
    private static String parseFormalParameters(String string, List list, List list2) {
        if (string.charAt(0) != '<') {
            return string;
        }
        int n = 1;
        int n2 = 1;
        block4: while (n2 < string.length()) {
            switch (string.charAt(n2)) {
                case '<': {
                    ++n;
                    break;
                }
                case '>': {
                    if (--n == 0) break block4;
                }
            }
            ++n2;
        }
        String string2 = string.substring(1, n2);
        string = string.substring(n2 + 1);
        while (string2.length() > 0) {
            int n3 = string2.indexOf(":");
            String string3 = string2.substring(0, n3);
            string2 = string2.substring(n3 + 1);
            ArrayList<GenericType> arrayList = new ArrayList<GenericType>();
            while (true) {
                if (string2.charAt(0) == ':') {
                    arrayList.add(new GenericType(8, 0, "java/lang/Object"));
                    string2 = string2.substring(1);
                }
                String string4 = GenericType.getNextType(string2);
                arrayList.add(new GenericType(string4));
                string2 = string2.substring(string4.length());
                if (string2.length() == 0 || string2.charAt(0) != ':') break;
                string2 = string2.substring(1);
            }
            list.add(string3);
            list2.add(arrayList);
        }
        return string;
    }

    public static String getTypeName(GenericType genericType) {
        String string;
        Object object = genericType;
        int n = ((GenericType)object).type;
        if (n <= 7) {
            string = typeNames[n];
        } else if (n == 10) {
            string = "void";
        } else if (n == 18) {
            string = ((GenericType)object).value;
        } else if (n == 8) {
            StringBuilder stringBuilder = new StringBuilder();
            Object object2 = GenericMain.buildJavaClassName((GenericType)object);
            Object var3_7 = null;
            stringBuilder.append(DecompilerContext.getImpcollector().getShortName((String)object2, true));
            if (!((GenericType)object).getArguments().isEmpty()) {
                stringBuilder.append("<");
                int n2 = 0;
                while (n2 < ((GenericType)object).getArguments().size()) {
                    int n3;
                    if (n2 > 0) {
                        stringBuilder.append(", ");
                    }
                    if ((n3 = ((Integer)((GenericType)object).getWildcards().get(n2)).intValue()) != 4) {
                        stringBuilder.append("?");
                        switch (n3) {
                            case 1: {
                                stringBuilder.append(" extends ");
                                break;
                            }
                            case 2: {
                                stringBuilder.append(" super ");
                            }
                        }
                    }
                    if ((object2 = (GenericType)((GenericType)object).getArguments().get(n2)) != null) {
                        stringBuilder.append(GenericMain.getTypeName((GenericType)object2));
                    }
                    ++n2;
                }
                stringBuilder.append(">");
            }
            string = stringBuilder.toString();
        } else {
            throw new RuntimeException("invalid type");
        }
        object = string;
        int n4 = genericType.arraydim;
        while (n4-- > 0) {
            object = String.valueOf(object) + "[]";
        }
        return object;
    }

    private static String buildJavaClassName(GenericType genericType) {
        Object object;
        String string = "";
        Object object2 = genericType.getEnclosingClasses().iterator();
        while (object2.hasNext()) {
            object = (GenericType)object2.next();
            string = String.valueOf(string) + ((GenericType)object).value + "$";
        }
        object = (string = String.valueOf(string) + genericType.value).replace('/', '.');
        if (!(((String)object).indexOf("$") < 0 || (object2 = DecompilerContext.getStructcontext().getClass(string)) != null && ((StructClass)object2).isOwn())) {
            object = ((String)object).replace('$', '.');
        }
        return object;
    }
}

