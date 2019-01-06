/*
 * Based on http://code.google.com/p/javacoveragent/ by
 * "alex.mq0" and "dmitry.kandalov"
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.quicktheories.coverage;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.quicktheories.coverage.analysis.CoverageAnalyser;

import sun.quicktheories.coverage.CodeCoverageStore;

/**
 * Instruments a class with probes on each line
 */
public class CoverageClassVisitor extends ClassVisitor {
  private final int classId;

  private int       probeCount = 0;

  public CoverageClassVisitor(final int classId, final ClassWriter writer) {
    super(Opcodes.ASM7, writer);
    this.classId = classId;
  }

  public void registerProbes(final int number) {
    this.probeCount = this.probeCount + number;
  }
 
  @Override
  public final MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions) {
    final MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc,
        signature, exceptions);
    return new CoverageAnalyser(this, this.classId, this.probeCount,
        methodVisitor, access, name, desc, signature, exceptions);
  }



  @Override
  public void visitEnd() {
    CodeCoverageStore.registerClassProbes(this.classId, this.probeCount);
  }

}
