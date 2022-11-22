package dev.pinkroom.peanuts

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class PeanutProcessProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment) =
        PeanutStateProcessor(environment.codeGenerator)
}