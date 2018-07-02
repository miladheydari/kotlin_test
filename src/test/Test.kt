package test

import com.squareup.kotlinpoet.*
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import java.io.File
import java.util.*


fun main(args: Array<String>) {


    val scanner = Scanner(System.`in`)
    print("package name: ")

    val packageName: String = scanner.nextLine()

    val className = packageName.split(".").last().capitalize()


    // Component interface

    val classNameActivity = ClassName("", "${className}Activity")

    val componentFile = FileSpec.builder(packageName, "${className}Component")

            .addType(TypeSpec.interfaceBuilder("${className}Component").addAnnotation(AnnotationSpec.builder(Subcomponent::class).addMember(
                    "modules = [${className}PresenterModule::class]"
            ).build()
            ).addFunction(FunSpec.builder("inject").addModifiers(KModifier.ABSTRACT)
                    .addParameter("activity", classNameActivity).build())
                    .build()).build()



    componentFile.writeTo(System.out)


    //Contract class


    val contractFile = FileSpec.builder(packageName, "${className}Contract")

            .addType(TypeSpec.classBuilder("${className}Contract").addType(TypeSpec.interfaceBuilder("View")
                    .addSuperinterface(ClassName("", "IView<Presenter>")).build())
                    .addType(TypeSpec.interfaceBuilder("Presenter").addSuperinterface(ClassName("", "IPresenter<View>")).build())

                    .build()).build()

    contractFile.writeTo(System.out)


// presenter class

    val presenterFile = FileSpec.builder(packageName, "${className}Presenter")

            .addType(TypeSpec.classBuilder("${className}Presenter").superclass(ClassName("","${className}Contract.Presenter"))



                    .build()).build()


    presenterFile.writeTo(System.out)


    //presenter module class


    val classNamePresenter = ClassName("", "${className}Presenter")

    val presenterModuleFile = FileSpec.builder(packageName, "${className}PresenterModule")

            .addType(TypeSpec.classBuilder("${className}PresenterModule").addAnnotation(AnnotationSpec.builder(Module::class).build()).addFunction(FunSpec.builder("provide${className}Presenter").addAnnotation(AnnotationSpec.builder(Provides::class).build())
                    .addParameter("presenter", classNamePresenter).returns(ClassName("", "${className}Contract.Presenter"))
                    .addStatement("return presenter")
                    .build())
                    .build()).build()

    presenterModuleFile.writeTo(System.out)


    val dir = File("./result")
    dir.deleteRecursively()
    dir.mkdirs()




    presenterFile.writeTo(dir)
    presenterModuleFile.writeTo(dir)
    componentFile.writeTo(dir)
    contractFile.writeTo(dir)


}