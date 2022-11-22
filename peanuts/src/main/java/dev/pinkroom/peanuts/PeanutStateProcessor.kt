package dev.pinkroom.peanuts

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.OutputStream
import kotlin.reflect.KClass

internal class PeanutStateProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotations = resolver.findAnnotations(PeanutState::class)
        if (!annotations.iterator().hasNext()) return emptyList()

        val peanutStateHelperBuilder = FileSpec.builder("dev.pinkroom.peanuts", "PeanutStateHelper")
            .addImport("kotlinx.coroutines.flow", "update")

        annotations.forEach { annotation ->
            annotation.closestClassDeclaration()?.toClassName()?.let { className ->
                val updateFunSpec = FunSpec.builder("update")
                    .receiver(MutableStateFlow::class.asClassName().parameterizedBy(className))
                annotation.primaryConstructor?.parameters?.forEach { parameter ->
                    val name = parameter.name!!.getShortName()
                    val parameterSpec = ParameterSpec.builder(
                        parameter.name!!.getShortName(),
                        parameter.type.toTypeName(),
                    ).defaultValue("%L", "value.$name").build()
                    updateFunSpec.addParameter(parameterSpec)
                }
                val copyParams = updateFunSpec.parameters.joinToString(",\n") { parameter ->
                    "${parameter.name} = ${parameter.name}"
                }
                updateFunSpec.addCode(
                    """
                    |update {
                    |   it.copy($copyParams)
                    |}
                    |""".trimMargin()
                )
                peanutStateHelperBuilder.addFunction(updateFunSpec.build())
            }
        }

        peanutStateHelperBuilder.build().writeTo(
            codeGenerator,
            Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        )

        return annotations.filterNot { it.validate() }.toList()
    }

    private fun Resolver.findAnnotations(kClass: KClass<*>) =
        getSymbolsWithAnnotation(kClass.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()

    private operator fun OutputStream.plusAssign(str: String) = write(str.toByteArray())
}