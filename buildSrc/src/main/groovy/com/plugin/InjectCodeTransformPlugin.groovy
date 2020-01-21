package com.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class InjectCodeTransformPlugin implements Plugin<Project> {

    private static final String PLUGIN_NAME = 'injectCode'

    @Override
    public void apply(Project project) {
        project.extensions.create(PLUGIN_NAME,InjectCodeExtension)

        def android = project.extensions.getByType(AppExtension)
        // 注册Transform
        def classTransform = new InjectCodeTransform(project)
        android.registerTransform(classTransform)
    }


}