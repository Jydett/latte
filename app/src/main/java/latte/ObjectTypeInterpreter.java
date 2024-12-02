package latte;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

class ObjectTypeInterpreter extends BasicInterpreter {
    public ObjectTypeInterpreter() {
      super(/* latest api = */ ASM9);
    }
  
    @Override
    public BasicValue newValue(final Type type) {
        BasicValue value;
        if (type != null && type.getSort() == Type.BOOLEAN) {
            value = new BasicValue(Type.BOOLEAN_TYPE);
        }
        else {
            value = super.newValue(type);
            if (value != null && value.equals(BasicValue.REFERENCE_VALUE)) {
                value = new BasicValue(type);
            }
        }
        return value;
    }

    @Override
    public BasicValue binaryOperation(
        final AbstractInsnNode insn, final BasicValue value1, final BasicValue value2)
        throws AnalyzerException {
        if (insn.getOpcode() == Opcodes.AALOAD) {
            Type type = value1.getType();
            String internalName = type.getInternalName();
            if (internalName.startsWith("[[")) {
                try {
                    return new BasicValue(Type.getType(internalName.substring(1)));
                } catch (IllegalArgumentException e) {
                    System.err.println("susex");
                }
            }
            return new BasicValue(type.getElementType());
        }
        return super.binaryOperation(insn, value1, value2);
    }

    @Override
    public BasicValue merge(final BasicValue value1, final BasicValue value2) {
      return value1;
    }


}