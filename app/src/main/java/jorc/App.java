/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jorc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;


public class App {
    public static void main(String[] args) throws Exception {
        FileInputStream fi = new FileInputStream("/tmp/HelloWorld.class");
        ClassReader cr = new ClassReader(fi);

        ClassNode cn = new ClassNode();
        
        cr.accept(cn, 0);

        for (MethodNode methodNode : cn.methods) {
            if (methodNode.localVariables != null && methodNode.localVariables.size() > 0) {
                System.err.println(String.format("Method %s already contains local variables, skipping...", methodNode.name));
                //continue;
            }
            Type methodType = Type.getType(methodNode.desc);
            boolean isMethodStatic = (methodNode.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            if (!isMethodStatic || methodType.getArgumentTypes().length != 0) {
                LabelNode first = new LabelNode();
                LabelNode last = new LabelNode();

                methodNode.instructions.insert(first);
                methodNode.instructions.add(last);

                int index = 0;
                if (!isMethodStatic) {
                    LocalVariableNode thisVar = new LocalVariableNode("this", Type.getObjectType(cn.name).toString(), null, first, last, index);
                    methodNode.localVariables.add(thisVar);
                    index++;
                }
                Type[] argTypes = methodType.getArgumentTypes();
                for (int i = 0; i < argTypes.length; i++) {
                    Type argType = argTypes[i];
                    LocalVariableNode thisVar = new LocalVariableNode(String.format("arg%d", i+1), argType.getDescriptor(), null, first, last, index);
                    methodNode.localVariables.add(thisVar);
                    index += argType.getSize();
                }
            }
            /*
            for (AbstractInsnNode insn : methodNode.instructions) {
                // check if this instruction stores a local var on the stack
                int opcode = insn.getOpcode();
                if (insn.getType() == AbstractInsnNode.VAR_INSN) {
                    Type varType;
                    if (opcode == Opcodes.ASTORE) {
                        AbstractInsnNode prev = insn.getPrevious();
                        if (prev.getType() == AbstractInsnNode.LDC_INSN) {
                            LdcInsnNode ldcInsnNode = ((LdcInsnNode)prev);
                            varType = Type.getType(ldcInsnNode.cst.getClass());
                        }
                        else if (prev.getType() == AbstractInsnNode.METHOD_INSN) {
                            MethodInsnNode methodInsnNode = ((MethodInsnNode)prev);
                            varType = Type.getType(methodInsnNode.desc).getReturnType();
                        }
                        else if (prev.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
                            InvokeDynamicInsnNode invokeDynamicInsnNode = ((InvokeDynamicInsnNode)prev);
                            varType = Type.getType(invokeDynamicInsnNode.desc).getReturnType();
                        }
                        else {
                            throw new Exception(String.format("Unexpected instruction type %d", prev.getType()));
                        }
                    }
                    else if (opcode == Opcodes.ISTORE) {
                        varType = Type.INT_TYPE;
                    }
                    else if(opcode == Opcodes.DSTORE) {
                        varType = Type.DOUBLE_TYPE;
                    }
                    else if(opcode == Opcodes.FSTORE) {
                        varType = Type.FLOAT_TYPE;
                    }
                    else if(opcode == Opcodes.LSTORE) {
                        varType = Type.LONG_TYPE;
                    }
                    else {
                        throw new Exception(String.format("Unexpected opcode %d", opcode));
                    }
                    VarInsnNode varIsnNode = ((VarInsnNode)insn);
                    //new LocalVariableNode("local"+varIsnNode.var, varType.toString(), null, null, null, varIsnNode.var);
                }
            }
            */
        }
        // No need to recompute max or frames
        // because we aren't actually changing
        // the methods
        ClassWriter classWriter = new ClassWriter(0);
        cn.accept(classWriter);
 
        OutputStream dout = new FileOutputStream(new File("/tmp","GoodbyeWorld.class"));
        dout.write(classWriter.toByteArray());
        dout.flush();
        dout.close();
    }
}
