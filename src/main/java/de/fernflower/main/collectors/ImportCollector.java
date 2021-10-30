/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.collectors;

import de.fernflower.main.ClassesProcessor;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.struct.StructContext;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public final class ImportCollector {
    private HashMap<String, String> mapSimpleNames = new HashMap<>();
    private HashSet<String> setNotImportedNames = new HashSet<>();
    private String currentPackageSlash = "";
    private String currentPackagePoint = "";

    public ImportCollector(ClassesProcessor$ClassNode object) {
        String qn = object.classStruct.qualifiedName;
        int n = qn.lastIndexOf("/");
        if (n >= 0) {
            this.currentPackageSlash = qn.substring(0, n);
            this.currentPackagePoint = this.currentPackageSlash.replace('/', '.');
            this.currentPackageSlash = String.valueOf(this.currentPackageSlash) + "/";
        }
    }

    public final String getShortName(String fullname, boolean imported) {
        ClassesProcessor clproc = DecompilerContext.getClassprocessor();
		ClassesProcessor$ClassNode node = (ClassesProcessor$ClassNode) clproc.getMapRootClasses().get(fullname.replace('.', '/'));
		
		String retname = null;
		
		if(node != null && node.classStruct.isOwn()) {  
			
			retname = node.simpleName;
			
			while(node.parent != null && node.type == 1) { //ClassNode.CLASS_MEMBER
				retname = node.parent.simpleName+"."+retname;
				node = node.parent;
			}
			
			if(node.type == 0) { // ClassNode.CLASS_ROOT
				fullname = node.classStruct.qualifiedName;
				fullname = fullname.replace('/', '.');
			} else {
				return retname;
			}
			
		} else if(node == null || !node.classStruct.isOwn()) {
				fullname = fullname.replace('$', '.');
		}
		
		String nshort = fullname;
		String npackage = "";
		
		int lastpoint = fullname.lastIndexOf(".");
		
		if(lastpoint >= 0) {
			nshort = fullname.substring(lastpoint+1);
			npackage = fullname.substring(0, lastpoint);
		} 
		
		StructContext context = DecompilerContext.getStructcontext();
		
		boolean existsDefaultClass = (context.getClass(currentPackageSlash+nshort) != null 
													&& !npackage.equals(currentPackagePoint)) // current package
										|| (context.getClass(nshort) != null);  // default package
		
		if(existsDefaultClass || 
				(mapSimpleNames.containsKey(nshort) && !npackage.equals(mapSimpleNames.get(nshort)))) {
			return fullname;
		} else if(!mapSimpleNames.containsKey(nshort)) {
			mapSimpleNames.put(nshort, npackage);
			
			if(!imported) {
				setNotImportedNames.add(nshort);
			}
		}
		
		return retname==null?nshort:retname;
    }

    public final void writeImports(BufferedWriter bufferedWriter) throws IOException {
        ArrayList<Entry<String, String>> var2 = new ArrayList<>(this.mapSimpleNames.entrySet());
        Collections.sort(var2, (par0, par1) -> {
            int res = par0.getValue().compareTo(par1.getValue());
            if(res == 0) {
                res = par0.getKey().compareTo(par1.getKey());
            }
            return res;
        });
        ArrayList<String> var3 = new ArrayList<>();
        Iterator<Entry<String, String>> var4 = var2.iterator();

        while(var4.hasNext()) {
            Entry<String, String> var6 = var4.next();
            if (!this.setNotImportedNames.contains(var6.getKey()) && !"java.lang".equals(var6.getValue()) && (var6.getValue()).length() > 0) {
                String var7 = var6.getValue() + "." + var6.getKey();
                var3.add(var7);
            }
        }

        Iterator<String> var8 = var3.iterator();

        while(var8.hasNext()) {
            String var5 = var8.next();
            bufferedWriter.write("import " + var5 + ";");
            bufferedWriter.newLine();
        }
    }
}

