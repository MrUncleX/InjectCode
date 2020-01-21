package com.plugin.bean;

import java.util.List;

public class BeInjectClassBean {


    /**
     * packageClassName : com.android.garbage.Garbage
     * importPackage : android.util.Log
     * GarbageCode : [{"methodName":"runGarbage","insertBeforeCode":"Log.e(\"--->\", \"Hello1\");
     * ","insertAfterCode":"Log.e(\"--->\", \"Hello2\");"},{"methodName":"gohome",
     * "insertBeforeCode":"Log.e(\"--->\", \"Hello??\");","insertAfterCode":"Log.e(\"--->\",
     * \"Hello0.013\");"}]
     */

    private String packageClassName;
    private String importPackage;
    private String importClassPath;
    private List<GarbageCodeBean> GarbageCode;

    public String getPackageClassName() {
        return packageClassName;
    }

    public void setPackageClassName(String packageClassName) {
        this.packageClassName = packageClassName;
    }

    public String getImportPackage() {
        return importPackage;
    }

    public void setImportPackage(String importPackage) {
        this.importPackage = importPackage;
    }

    public List<GarbageCodeBean> getGarbageCode() {
        return GarbageCode;
    }

    public void setGarbageCode(List<GarbageCodeBean> GarbageCode) {
        this.GarbageCode = GarbageCode;
    }

    public String getImportClassPath() {
        return importClassPath;
    }

    public void setImportClassPath(String importClassPath) {
        this.importClassPath = importClassPath;
    }

    public static class GarbageCodeBean {
        /**
         * methodName : runGarbage
         * insertBeforeCode : Log.e("--->", "Hello1");
         * insertAfterCode : Log.e("--->", "Hello2");
         */

        private String methodName;
        private String insertBeforeCode;
        private String insertAfterCode;

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getInsertBeforeCode() {
            return insertBeforeCode;
        }

        public void setInsertBeforeCode(String insertBeforeCode) {
            this.insertBeforeCode = insertBeforeCode;
        }

        public String getInsertAfterCode() {
            return insertAfterCode;
        }

        public void setInsertAfterCode(String insertAfterCode) {
            this.insertAfterCode = insertAfterCode;
        }
    }
}
