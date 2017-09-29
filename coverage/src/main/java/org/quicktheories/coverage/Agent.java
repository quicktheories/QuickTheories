package org.quicktheories.coverage;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.function.Predicate;

public class Agent {
  
  private static Predicate<String> shouldTransform = startsWith("org/quicktheories/")
                                                .or(startsWith("jdk/"))
                                                .or(startsWith("java/"))
                                                .or(startsWith("javafx/"))
                                                .or(startsWith("com/sun/"))
                                                .or(startsWith("sun/"))
                                                .negate();

  public static void agentmain(String agentArgs, Instrumentation inst) {
    System.out.println("Coverage agent installed");

    ClassFileTransformer transformer = new CoverageTransformer(shouldTransform);

    inst.addTransformer(transformer, true);

    Arrays.stream(inst.getAllLoadedClasses())
        .filter(c -> inst.isModifiableClass(c)
            && !c.isSynthetic()
            && shouldTransform.test(c.getName()))
        .forEach(t -> {
          try {
            inst.retransformClasses(t);
          } catch (Throwable e) {
            System.err.println("Error while transforming " + t);
            e.printStackTrace();
          }
        });

  }
  
  private static Predicate<String> startsWith(String prefix) {
    return s -> s.replace('.',  '/').startsWith(prefix);
  }
}
