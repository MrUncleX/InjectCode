package com.plugin

import com.plugin.bean.BeInjectClassBean
import com.plugin.utils.ZipUtils
import javassist.*
import org.gradle.api.Project

/**
 * 插入jar文件
 */
public class InjectJar {

    //是否已解压
    private static boolean isUnZip = false


    private static String tempFileDir = "/tempFile/unzip"
    private static String tempJarDir = "/tempFile/jar"
    private static String tempJarName = "tmpJar.jar"


    private static ClassPool classPool = ClassPool.getDefault()

    /**
     * 一个jar处理
     * @param path
     * @param project
     * @param list
     * @return
     */
    public static File injectJar(String path, Project project, List<BeInjectClassBean> list) {
//        System.out.println("进入injectOnCreate")
        ClassPath classPath = classPool.appendClassPath(path)
        //安卓系统原生库
        ClassPath classPath2 = classPool.appendClassPath(project.android.bootClasspath[0].toString())
        // 解压 修改 重新压缩
        File file = new File(path)
        String outputTempFileDir = project.buildDir.absolutePath + tempFileDir
        String outputTempJarDir = project.buildDir.absolutePath + tempJarDir
        //临时输出目录
        File tmpFileDir = new File(outputTempFileDir)
        if (!tmpFileDir.exists()) {
            tmpFileDir.mkdirs()
        }
        File tmpJarDir = new File(outputTempJarDir)
        if (!tmpJarDir.exists()) {
            tmpJarDir.mkdirs()
        }
        //临时jar文件
        File tmpJarFile = new File(outputTempJarDir, tempJarName)
        if (!tmpJarFile.exists()) {
            tmpJarFile.createNewFile()
        }

        for (BeInjectClassBean injectClassBean : list) {
            if (classPool.find(injectClassBean.getPackageClassName()) != null) {
                //存在需要修改的字节码类
                if (!isUnZip) {
                    //未解压则进行解压
                    //解压
                    ZipUtils.upZipFile(file, outputTempFileDir)
                    isUnZip = true
                }
                //修改
                classCodeModify(outputTempFileDir, injectClassBean, project)
            }
        }
        //移除ClassPath
        classPool.removeClassPath(classPath)
        classPool.removeClassPath(classPath2)

        if (isUnZip) {
            //有解压表示有修改

            //压缩
            ZipUtils.zipFiles(Arrays.asList(tmpFileDir.listFiles()), tmpJarFile)
            //清除
            deleteDir(tmpFileDir)
            isUnZip = false
            return tmpJarFile
        }
        return null
    }

    /**
     * 修改class字节码
     * @param path
     */
    private static void classCodeModify(String path, BeInjectClassBean injectClassBean, Project project) {
        //放置导包完毕后需关闭的classpath
        List<ClassPath> classPathList = importPackage2classPool(injectClassBean)

        CtClass ctClass = classPool.getCtClass(injectClassBean.getPackageClassName())
        System.out.println("packageClassName = " + injectClassBean.getPackageClassName())
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

    /**
     * 注入代码
     * @param injectClassBean
     * @param ctClass
     */
    private static injectCode(BeInjectClassBean injectClassBean, CtClass ctClass) {
        for (int i = 0; i < injectClassBean.getGarbageCode().size(); i++) {
            String methodName = injectClassBean.getGarbageCode().get(i).getMethodName()
            String insertBeforeStr = injectClassBean.getGarbageCode().get(i).getInsertBeforeCode()
            String insertAfterStr = injectClassBean.getGarbageCode().get(i).getInsertAfterCode()
            System.out.println("methodName = " + methodName)

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

    private static List<ClassPath> importPackage2classPool(BeInjectClassBean injectClassBean) {
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

    /**
     * 删除目录
     * @param file
     */
    public static void deleteDir(File file) {
        if (file.isFile()) {
            file.delete()
        } else {
            File[] files = file.listFiles()
            if (files == null) {
                file.delete()
            } else {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i])
                }
                file.delete()
            }
        }
    }
}
