package com.plugin

import com.plugin.bean.BeInjectClassBean
import javassist.*
import org.gradle.api.Project

/**
 * 插入class文件
 */
public class InjectClass {
    private static String fileSuffix = ".class"

    private static ClassPool classPool = ClassPool.getDefault()

    public static void injectClass(String path, Project project, List<BeInjectClassBean> list) {
//        System.out.println("进入injectOnCreate")
        classPool.appendClassPath(path)
        //安卓系统原生库
        ClassPath classPath = classPool.appendClassPath(project.android.bootClasspath[0].toString())

        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                for (BeInjectClassBean injectClassBean : list) {
                    String packageClassName = injectClassBean.getPackageClassName()
                    String className = packageClassName.substring(packageClassName.lastIndexOf(".")+1, packageClassName.length())
//                    System.out.println("className = " + className)
                    //为了筛选同名且为class的文件
                    if (file.getName().equals(className + fileSuffix)) {
                        if (classPool.find(packageClassName) != null) {
                            System.out.println("filePath = " + file.absolutePath)
                            System.out.println("file.getName = " + file.getName())

                            //导包
                            List<ClassPath> classPathList = importPackage2classPool(injectClassBean)

                            // 获取 class
                            CtClass ctClass = classPool.getCtClass(packageClassName)
                            System.out.println("ctClass = " + ctClass)

                            // 解冻
                            if (ctClass.isFrozen()) {
                                ctClass.defrost()
                            }
                            //注入代码
                            injectCode(injectClassBean, ctClass)
                            //写入文件
                            ctClass.writeFile(path)
                            ctClass.detach()

                            //classPool移除对应jar class文件的路径
                            for (int i = 0; i < classPathList.size(); i++) {
                                classPool.removeClassPath(classPathList.get(i))
                            }
                        }
                    }
                }
            }
        }
        classPool.removeClassPath(classPath)
    }

    /**
     * 注入代码
     * @param injectClassBean
     * @param ctClass
     */
    private static void injectCode(BeInjectClassBean injectClassBean, CtClass ctClass) {
        for (int i = 0; i < injectClassBean.getGarbageCode().size(); i++) {
            String methodName = injectClassBean.getGarbageCode().get(i).getMethodName()
            String insertBeforeStr = injectClassBean.getGarbageCode().get(i).getInsertBeforeCode()
            String insertAfterStr = injectClassBean.getGarbageCode().get(i).getInsertAfterCode()
            System.out.println("methodName = " + methodName)
            // 解冻
            if (ctClass.isFrozen()) {
                ctClass.defrost()
            }
            try {
                // 获取到 onCreate() 方法
                CtMethod ctMethod = ctClass.getDeclaredMethod(methodName)
                System.out.println("ctMethod = " + ctMethod)
                // 插入日志打印代码


                ctMethod.insertBefore(insertBeforeStr)
                System.out.println("insertBefore finish")
                ctMethod.insertAfter(insertAfterStr)
                System.out.println("insertAfter finish")
            } catch (NotFoundException e) {
                System.out.println(methodName + "NotFoundException " + e.getMessage())
            }
        }
    }

    /**
     * 包导入classpool
     * @param injectClassBean
     * @return
     */
    private static List<ClassPath> importPackage2classPool(BeInjectClassBean injectClassBean) {
//放置导包完毕后需关闭的classpath
        List<ClassPath> classPathList = new ArrayList<>()
        //导包对应jar class文件的路径读取到classPool
        if (injectClassBean.getImportClassPath() != null && injectClassBean.getImportClassPath() != "") {
            System.out.println("importClassPath = " + injectClassBean.getImportClassPath())
            String[] importClassPaths = injectClassBean.getImportClassPath().split(",")
            for (int i = 0; i < importClassPaths.length; i++) {
                classPathList.add(classPool.appendClassPath(importClassPaths[i]))
            }
        }
        //导包
        if (injectClassBean.getImportPackage() != null && injectClassBean.getImportPackage() != "") {

            System.out.println("importPackage = " + injectClassBean.getImportPackage())

            String[] importPackages = injectClassBean.getImportPackage().split(",")
            for (int i = 0; i < importPackages.length; i++) {
                classPool.importPackage(importPackages[i])
            }
        }
        return classPathList
    }
}
