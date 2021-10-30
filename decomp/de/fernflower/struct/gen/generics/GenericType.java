/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen.generics;

import java.util.ArrayList;
import java.util.List;

public final class GenericType {
    public int type;
    public int arraydim;
    public String value;
    private List enclosingClasses = new ArrayList();
    private List arguments = new ArrayList();
    private List wildcards = new ArrayList();

    public GenericType(int n, int n2, String string) {
        this.type = 8;
        this.arraydim = 0;
        this.value = string;
    }

    public GenericType(String string) {
        this.parseSignature(string);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void parseSignature(String string) {
        int n = 0;
        while (n < string.length()) {
            switch (string.charAt(n)) {
                case '[': {
                    ++this.arraydim;
                    break;
                }
                case 'T': {
                    this.type = 18;
                    this.value = string.substring(n + 1, string.length() - 1);
                    return;
                }
                case 'L': {
                    this.type = 8;
                    string = string.substring(n + 1, string.length() - 1);
                    while (true) {
                        String string2 = string;
                        int n2 = 0;
                        int n3 = 0;
                        block29: while (n3 < string2.length()) {
                            switch (string2.charAt(n3)) {
                                case '<': {
                                    ++n2;
                                    break;
                                }
                                case '>': {
                                    --n2;
                                    break;
                                }
                                case '.': {
                                    if (n2 == 0) break block29;
                                }
                            }
                            ++n3;
                        }
                        Object object = string2.substring(0, n3);
                        string2 = object;
                        String string3 = null;
                        int n4 = ((String)object).indexOf("<");
                        if (n4 >= 0) {
                            string2 = ((String)object).substring(0, n4);
                            string3 = ((String)object).substring(n4 + 1, ((String)object).length() - 1);
                        }
                        if (((String)object).length() >= string.length()) {
                            this.value = string2;
                            GenericType.parseArgumentsList(string3, this);
                            return;
                        }
                        string = string.substring(((String)object).length() + 1);
                        object = new GenericType(8, 0, string2);
                        GenericType.parseArgumentsList(string3, (GenericType)object);
                        this.enclosingClasses.add(object);
                    }
                }
                default: {
                    int n5;
                    this.value = string.substring(n, n + 1);
                    switch (this.value.charAt(0)) {
                        case 'B': {
                            n5 = 0;
                            break;
                        }
                        case 'C': {
                            n5 = 1;
                            break;
                        }
                        case 'D': {
                            n5 = 2;
                            break;
                        }
                        case 'F': {
                            n5 = 3;
                            break;
                        }
                        case 'I': {
                            n5 = 4;
                            break;
                        }
                        case 'J': {
                            n5 = 5;
                            break;
                        }
                        case 'S': {
                            n5 = 6;
                            break;
                        }
                        case 'Z': {
                            n5 = 7;
                            break;
                        }
                        case 'V': {
                            n5 = 10;
                            break;
                        }
                        case 'G': {
                            n5 = 12;
                            break;
                        }
                        case 'N': {
                            n5 = 14;
                            break;
                        }
                        case 'A': {
                            n5 = 9;
                            break;
                        }
                        case 'X': {
                            n5 = 15;
                            break;
                        }
                        case 'Y': {
                            n5 = 16;
                            break;
                        }
                        case 'U': {
                            n5 = 17;
                            break;
                        }
                        default: {
                            throw new RuntimeException("Invalid type");
                        }
                    }
                    this.type = n5;
                }
            }
            ++n;
        }
    }

    /*
     * Unable to fully structure code
     */
    private static void parseArgumentsList(String var0, GenericType var1_1) {
        if (var0 != null) ** GOTO lbl22
        return;
lbl-1000:
        // 1 sources

        {
            var2_2 = GenericType.getNextType(var0);
            var3_3 = var2_2.length();
            var4_4 = 4;
            switch (var2_2.charAt(0)) {
                case '*': {
                    var4_4 = 3;
                    break;
                }
                case '+': {
                    var4_4 = 1;
                    break;
                }
                case '-': {
                    var4_4 = 2;
                }
            }
            var1_1.wildcards.add(var4_4);
            if (var4_4 != 4) {
                var2_2 = var2_2.substring(1);
            }
            var1_1.arguments.add(var2_2.length() == 0 ? null : new GenericType(var2_2));
            var0 = var0.substring(var3_3);
lbl22:
            // 2 sources

            ** while (var0.length() > 0)
        }
lbl23:
        // 1 sources

    }

    public static String getNextType(String string) {
        int n = 0;
        int n2 = 0;
        boolean bl = false;
        block8: while (n2 < string.length()) {
            switch (string.charAt(n2)) {
                case '*': {
                    if (bl) break;
                    break block8;
                }
                case 'L': 
                case 'T': {
                    if (bl) break;
                    bl = true;
                }
                case '+': 
                case '-': 
                case '[': {
                    break;
                }
                default: {
                    if (bl) break;
                    break block8;
                }
                case '<': {
                    ++n;
                    break;
                }
                case '>': {
                    --n;
                    break;
                }
                case ';': {
                    if (n == 0) break block8;
                }
            }
            ++n2;
        }
        return string.substring(0, n2 + 1);
    }

    public final List getArguments() {
        return this.arguments;
    }

    public final List getEnclosingClasses() {
        return this.enclosingClasses;
    }

    public final List getWildcards() {
        return this.wildcards;
    }
}

