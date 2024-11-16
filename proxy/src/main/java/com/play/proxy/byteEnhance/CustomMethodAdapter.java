package com.play.proxy.byteEnhance;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CustomMethodAdapter extends ClassVisitor implements Opcodes {

    public CustomMethodAdapter(int i, ClassVisitor classVisitor) {
        super(Opcodes.ASM4, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if(cv!=null){
            cv.visit(version,access,name,signature,superName,interfaces);
        }
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if("setUserName".equals(name)){
            MethodVisitor mv = cv.visitMethod(access,name,desc,signature,exceptions);
            return new AsmMethodVisit(api,mv);
        }
        if(cv!=null){
            return cv.visitMethod(access,name,desc,signature,exceptions);
        }
        return null;
    }
}
