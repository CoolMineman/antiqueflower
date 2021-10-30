/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.util.InterpreterUtil;
import java.util.List;

public final class AnnotationExprent
extends Exprent {
    private String classname;
    private List parnames;
    private List parvalues;

    public AnnotationExprent(String string, List list, List list2) {
        this.type = 13;
        this.classname = string;
        this.parnames = list;
        this.parvalues = list2;
    }

    public final String toJava(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        String string = InterpreterUtil.getIndentString(n);
        stringBuilder.append(string);
        stringBuilder.append("@");
        String string2 = ExprProcessor.buildJavaClassName(this.classname);
        String string3 = null;
        stringBuilder.append(DecompilerContext.getImpcollector().getShortName(string2, true));
        if (!this.parnames.isEmpty()) {
            stringBuilder.append("(");
            if (this.parnames.size() == 1 && "value".equals(this.parnames.get(0))) {
                stringBuilder.append(((Exprent)this.parvalues.get(0)).toJava(n + 1));
            } else {
                string3 = InterpreterUtil.getIndentString(n + 1);
                int n2 = 0;
                while (n2 < this.parnames.size()) {
                    stringBuilder.append("\r\n" + string3);
                    stringBuilder.append((String)this.parnames.get(n2));
                    stringBuilder.append(" = ");
                    stringBuilder.append(((Exprent)this.parvalues.get(n2)).toJava(n + 2));
                    if (n2 < this.parnames.size() - 1) {
                        stringBuilder.append(",");
                    }
                    ++n2;
                }
                stringBuilder.append("\r\n" + string);
            }
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    public final int getAnnotationType() {
        if (this.parnames.isEmpty()) {
            return 2;
        }
        if (this.parnames.size() == 1 && "value".equals(this.parnames.get(0))) {
            return 3;
        }
        return 1;
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof AnnotationExprent) {
            object = (AnnotationExprent)object;
            return this.classname.equals(((AnnotationExprent)object).classname) && InterpreterUtil.equalLists(this.parnames, ((AnnotationExprent)object).parnames) && InterpreterUtil.equalLists(this.parvalues, ((AnnotationExprent)object).parvalues);
        }
        return false;
    }

    public final String getClassname() {
        return this.classname;
    }
}

