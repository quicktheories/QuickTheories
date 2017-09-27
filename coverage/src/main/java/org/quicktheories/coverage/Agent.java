package org.quicktheories.coverage;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;



public class Agent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
      System.out.println("Coverage agent installed");

      ClassFileTransformer transformer = new CoverageTransformer( s -> !s.startsWith("org/quicktheories/coverage") 
                                                                    && !s.startsWith("org/quicktheories") 
                                                                    && !s.startsWith("jdk/") 
                                                                    && !s.startsWith("sun/")
                                                                    && !s.startsWith("com/sun/")
                                                                    && !s.startsWith("org/objectweb/asm")
                                                                    && !s.startsWith("javax/")
                                                                    && !s.startsWith("javafx/")                                                                    
                                                                    && !s.startsWith("java/") );
      
      inst.addTransformer(transformer, true);
              
      
        Arrays.stream(inst.getAllLoadedClasses())
        .filter(c -> inst.isModifiableClass(c) && ! c.getName().startsWith("java.") && !c.isSynthetic() 
            && !c.getName().startsWith("org.quicktheories.coverage")
            && !c.getName().startsWith("sun.") 
            && !c.getName().startsWith("com.sun."))
        .forEach(t -> {
          try {
           // System.err.println(" transforming " + t);
            inst.retransformClasses(t);
          } catch (Throwable e) {
            System.err.println("Error while transforming " + t);
            e.printStackTrace();
          }
        });

      
    }
  }

