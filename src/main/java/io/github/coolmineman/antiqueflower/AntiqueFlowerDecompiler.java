package io.github.coolmineman.antiqueflower;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.Fernflower;
import de.fernflower.main.decompiler.helper.PrintStreamLogger;
import de.fernflower.main.extern.IBytecodeProvider;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.main.extern.IFernflowerLogger;

public class AntiqueFlowerDecompiler {
    private Fernflower fernflower;

    protected AntiqueFlowerDecompiler(IFernflowerLogger logger, HashMap<String, Object> propertiesCustom, IBytecodeProvider in, IDecompilatSaver out) {
        fernflower = new Fernflower(in, out, propertiesCustom);
        DecompilerContext.setLogger(logger);
    }

    public static void main(String[] args) {
        try {

            if (args != null && args.length > 1) {

                HashMap<String, Object> mapOptions = new HashMap<>();
                mapOptions.put("threads", Runtime.getRuntime().availableProcessors());

                List<String> lstSources = new ArrayList<>();
                List<String> lstLibraries = new ArrayList<String>();

                boolean isOption = true;
                for (int i = 0; i < args.length - 1; ++i) { // last parameter - destination
                    String arg = args[i];

                    if (isOption && arg.startsWith("-") && arg.length() > 5 && arg.charAt(4) == '=') {
                        String value = arg.substring(5).toUpperCase();
                        if ("TRUE".equals(value)) {
                            value = "1";
                        } else if ("FALSE".equals(value)) {
                            value = "0";
                        }

                        mapOptions.put(arg.substring(1, 4), value);
                    } else {
                        isOption = false;

                        if (arg.startsWith("-e=")) {
                            lstLibraries.add(arg.substring(3));
                        } else {
                            lstSources.add(arg);
                        }
                    }
                }

                if (lstSources.isEmpty()) {
                    printHelp();
                } else {
                    File root = new File(args[args.length - 1]);
                    try (
                        ThreadSafeBytecodeProvider in = new ThreadSafeBytecodeProvider();
                        ThreadSafeDecompilationSaver out = new ThreadSafeDecompilationSaver(root);
                    ) {
                        AntiqueFlowerDecompiler decompiler = new AntiqueFlowerDecompiler(new PrintStreamLogger(IFernflowerLogger.INFO, System.out), mapOptions, in, out);

                        for (String source : lstSources) {
                            decompiler.addSpace(new File(source), true);
                        }

                        for (String library : lstLibraries) {
                            decompiler.addSpace(new File(library), false);
                        }

                        decompiler.decompileContext();
                    }
                }
            } else {
                printHelp();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void printHelp() {
        System.out.println("Usage: java ConsoleDecompiler ( -<option>=<value>)* (<source>)+ <destination>");
        System.out.println("Example: java ConsoleDecompiler -dgs=true c:\\mysource\\ c:\\my.jar d:\\decompiled\\");
    }

    public void addSpace(File file, boolean isOwn) throws IOException {
        fernflower.getStructcontext().addSpace(file, isOwn);
    }

    public void decompileContext() {
        fernflower.decompileContext();
    }

}