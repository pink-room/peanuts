package dev.pinkroom.peanuts

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

internal class PeanutStateProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotations = resolver.findAnnotations(PeanutState::class)
        if (!annotations.iterator().hasNext()) return emptyList()

        val peanutStateHelperBuilder = FileSpec.builder("dev.pinkroom.peanuts", "PeanutStateHelper")
            .addImport("kotlinx.coroutines.flow", "update")

        annotations.forEach { annotation ->
            val dataClass = annotation.closestClassDeclaration()
            dataClass?.toClassName()?.let { className ->
                val updateFunSpec = FunSpec.builder("update")
                    .receiver(MutableStateFlow::class.asClassName().parameterizedBy(className))
                    .addAnnotation(
                        AnnotationSpec.builder(JvmName::class)
                            .addMember("%S", "update${className.simpleName}")
                            .build()
                    )
                buildParameters(dataClass)?.addTo(updateFunSpec)
                addFunctionBody(updateFunSpec)
                peanutStateHelperBuilder.addFunction(updateFunSpec.build())
            }
        }

        peanutStateHelperBuilder.build().writeTo(
            environment.codeGenerator,
            Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        )

        return annotations.filterNot { it.validate() }.toList()
    }

    private fun Resolver.findAnnotations(kClass: KClass<*>) =
        getSymbolsWithAnnotation(kClass.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()

    private fun buildParameters(dataClass: KSClassDeclaration): List<KSValueParameter>? {
        val properties = dataClass.getAllProperties()
        return dataClass.primaryConstructor?.parameters?.filter { parameter ->
            properties.forEach { property ->
                if (property.hasGetter(parameter.name?.getShortName())) return@filter true
            }
            return@filter false
        }
    }

    private fun KSPropertyDeclaration.hasGetter(propertyName: String?) =
        simpleName.getShortName() == propertyName && getter != null

    private fun List<KSValueParameter>.addTo(funSpecBuilder: FunSpec.Builder) {
        forEach { parameter ->
            val name = parameter.name!!.getShortName()
            val parameterSpec = ParameterSpec.builder(
                parameter.name!!.getShortName(),
                parameter.type.toTypeName(),
            ).defaultValue("%L", "value.$name").build()
            funSpecBuilder.addParameter(parameterSpec)
        }
    }

    private fun addFunctionBody(updateFunSpec: FunSpec.Builder) {
        val copyParams = updateFunSpec.parameters.joinToString { parameter ->
            "${parameter.name} = ${parameter.name}"
        }
        updateFunSpec.addCode("update { it.copy($copyParams) }".trimMargin())
    }
}