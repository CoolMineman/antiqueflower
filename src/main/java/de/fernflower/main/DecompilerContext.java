/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.main.collectors.ImportCollector;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.main.extern.IFernflowerLogger;
import de.fernflower.modules.renamer.PoolInterceptor;
import de.fernflower.struct.StructContext;
import java.util.HashMap;

public final class DecompilerContext {
    private static ThreadLocal<DecompilerContext> currentContext = new ThreadLocal<>();
    private HashMap<String, Object> properties = new HashMap<>();
    private StructContext structcontext;
    private ImportCollector impcollector;
    private VarNamesCollector varncollector;
    private CounterContainer countercontainer;
    private ClassesProcessor classprocessor;
    private PoolInterceptor poolinterceptor;
    private IFernflowerLogger logger;

    private DecompilerContext(HashMap<String, Object> hashMap) {
        this.properties.putAll(hashMap);
    }

    public static void initContext(HashMap<String, Object> hashMap) {
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("din", "1");
        hashMap2.put("dc4", "1");
        hashMap2.put("das", "1");
        hashMap2.put("rbr", "1");
        hashMap2.put("rsy", "0");
        hashMap2.put("hes", "1");
        hashMap2.put("hdc", "1");
        hashMap2.put("dgs", "0");
        hashMap2.put("occ", "0");
        hashMap2.put("ner", "1");
        hashMap2.put("den", "1");
        hashMap2.put("fdi", "1");
        hashMap2.put("rgn", "1");
        hashMap2.put("bto", "1");
        hashMap2.put("nns", "1");
        hashMap2.put("uto", "1");
        hashMap2.put("udv", "1");
        hashMap2.put("mpm", "0");
        hashMap2.put("rer", "1");
        if (hashMap != null) {
            hashMap2.putAll(hashMap);
        }
        currentContext.set(new DecompilerContext(hashMap2));
    }

    public static DecompilerContext getCurrentContext() {
        return currentContext.get();
    }

    public static void setCurrentContext(DecompilerContext decompilerContext) {
        currentContext.set(decompilerContext);
    }

    public static Object getProperty(String string) {
        return DecompilerContext.getCurrentContext().properties.get(string);
    }

    public static void setProperty(String string, Object object) {
        DecompilerContext.getCurrentContext().properties.put(string, object);
    }

    public static boolean getOption(String string) {
        return "1".equals(DecompilerContext.getCurrentContext().properties.get(string));
    }

    public static ImportCollector getImpcollector() {
        return DecompilerContext.getCurrentContext().impcollector;
    }

    public static void setImpcollector(ImportCollector importCollector) {
        DecompilerContext.getCurrentContext().impcollector = importCollector;
    }

    public static VarNamesCollector getVarncollector() {
        return DecompilerContext.getCurrentContext().varncollector;
    }

    public static void setVarncollector(VarNamesCollector varNamesCollector) {
        DecompilerContext.getCurrentContext().varncollector = varNamesCollector;
    }

    public static StructContext getStructcontext() {
        return DecompilerContext.getCurrentContext().structcontext;
    }

    public static void setStructcontext(StructContext structContext) {
        DecompilerContext.getCurrentContext().structcontext = structContext;
    }

    public static CounterContainer getCountercontainer() {
        return DecompilerContext.getCurrentContext().countercontainer;
    }

    public static void setCountercontainer(CounterContainer counterContainer) {
        DecompilerContext.getCurrentContext().countercontainer = counterContainer;
    }

    public static ClassesProcessor getClassprocessor() {
        return DecompilerContext.getCurrentContext().classprocessor;
    }

    public static void setClassprocessor(ClassesProcessor classesProcessor) {
        DecompilerContext.getCurrentContext().classprocessor = classesProcessor;
    }

    public static PoolInterceptor getPoolInterceptor() {
        return DecompilerContext.getCurrentContext().poolinterceptor;
    }

    public static void setPoolInterceptor(PoolInterceptor poolInterceptor) {
        DecompilerContext.getCurrentContext().poolinterceptor = poolInterceptor;
    }

    public static IFernflowerLogger getLogger() {
        return DecompilerContext.getCurrentContext().logger;
    }

    public static void setLogger(IFernflowerLogger iFernflowerLogger) {
        Object object;
        DecompilerContext.getCurrentContext().logger = iFernflowerLogger;
        if ((iFernflowerLogger = DecompilerContext.getCurrentContext().logger) != null && (object = DecompilerContext.getProperty("log")) != null && (object = (Integer)IFernflowerLogger.mapLogLevel.get(((String)object).toUpperCase())) != null) {
            iFernflowerLogger.setSeverity((Integer)object);
        }
    }
}

