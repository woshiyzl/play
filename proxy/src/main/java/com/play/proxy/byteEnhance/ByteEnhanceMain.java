package com.play.proxy.byteEnhance;

import com.play.proxy.UserImpl;
import org.objectweb.asm.*;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class ByteEnhanceMain extends ClassLoader implements Opcodes {
    public static void main(String[] args) throws Exception {
        ClassReader cr = new ClassReader(UserImpl.class.getName());
        ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new CustomMethodAdapter(0,cw);
       // cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "com/play/proxy/UserImpl", null, "java/lang/Object", null);
        //addNewMethod(cv);
        byte []codes = cw.toByteArray();
      /*  ByteEnhanceMain loader = new ByteEnhanceMain();
        Class<?> aClass = loader.defineClass(UserImpl.class.getName(), codes, 0, codes.length);
        for(Method method:aClass.getMethods()){
            System.out.println(method.getName());
        }
        Object test = aClass.getMethods()[1].invoke(aClass.newInstance(), "test");
        System.out.println(test);*/
        String path = ByteEnhanceMain.class.getResource("/").getPath() + "UserImpl.class";
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(codes);
        fos.close();
    }

   /* public static void addNewMethod(ClassVisitor cv){
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC,"setAge","(I)V",null,null);
        mv.visitLdcInsn("我添加了一个设置年纪的方法 setAge");
        mv.visitMethodInsn(INVOKESTATIC,"java/io/System","println","(Ljava/lang/Integer;)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();
    }*/
}
