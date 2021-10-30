/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.renamer;

import de.fernflower.main.extern.IIdentifierRenamer;
import java.util.HashSet;

public final class ConverterHelper
implements IIdentifierRenamer {
    private static HashSet setReserved = new HashSet<String>();
    private int class_counter = 0;
    private int field_counter = 0;
    private int method_counter = 0;
    private HashSet setNonStandardClassNames = new HashSet();

    static {
        setReserved.add("abstract");
        setReserved.add("do");
        setReserved.add("if");
        setReserved.add("package");
        setReserved.add("synchronized");
        setReserved.add("boolean");
        setReserved.add("double");
        setReserved.add("implements");
        setReserved.add("private");
        setReserved.add("this");
        setReserved.add("break");
        setReserved.add("else");
        setReserved.add("import");
        setReserved.add("protected");
        setReserved.add("throw");
        setReserved.add("byte");
        setReserved.add("extends");
        setReserved.add("instanceof");
        setReserved.add("public");
        setReserved.add("throws");
        setReserved.add("case");
        setReserved.add("false");
        setReserved.add("int");
        setReserved.add("return");
        setReserved.add("transient");
        setReserved.add("catch");
        setReserved.add("final");
        setReserved.add("interface");
        setReserved.add("short");
        setReserved.add("true");
        setReserved.add("char");
        setReserved.add("finally");
        setReserved.add("long");
        setReserved.add("static");
        setReserved.add("try");
        setReserved.add("class");
        setReserved.add("float");
        setReserved.add("native");
        setReserved.add("strictfp");
        setReserved.add("void");
        setReserved.add("const");
        setReserved.add("for");
        setReserved.add("new");
        setReserved.add("super");
        setReserved.add("volatile");
        setReserved.add("continue");
        setReserved.add("goto");
        setReserved.add("null");
        setReserved.add("switch");
        setReserved.add("while");
        setReserved.add("default");
        setReserved.add("assert");
        setReserved.add("enum");
    }

    public final boolean toBeRenamed(int n, String string, String string2, String string3) {
        String string4 = n == 1 ? string : string2;
        return string4 == null || string4.length() == 0 || string4.length() <= 2 || setReserved.contains(string4) || !Character.isJavaIdentifierStart(string4.charAt(0));
    }

    public final String getNextClassname(String string, String string2) {
        if (string2 == null) {
            return "class_" + this.class_counter++;
        }
        int n = 0;
        while (Character.isDigit(string2.charAt(n))) {
            ++n;
        }
        if (n == 0 || n == string2.length()) {
            return "class_" + this.class_counter++;
        }
        String string3 = string2.substring(n);
        if (this.setNonStandardClassNames.contains(string3)) {
            return "Inner" + string3 + "_" + this.class_counter++;
        }
        this.setNonStandardClassNames.add(string3);
        return "Inner" + string3;
    }

    public final String getNextFieldname(String string, String string2, String string3) {
        return "field_" + this.field_counter++;
    }

    public final String getNextMethodname(String string, String string2, String string3) {
        return "method_" + this.method_counter++;
    }

    public static String getSimpleClassName(String string) {
        return string.substring(string.lastIndexOf(47) + 1);
    }

    public static String replaceSimpleClassName(String string, String string2) {
        return String.valueOf(string.substring(0, string.lastIndexOf(47) + 1)) + string2;
    }
}

