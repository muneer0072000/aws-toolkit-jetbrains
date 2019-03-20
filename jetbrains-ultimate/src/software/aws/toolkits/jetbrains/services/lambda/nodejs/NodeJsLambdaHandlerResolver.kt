// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.nodejs

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import software.aws.toolkits.jetbrains.services.lambda.LambdaHandlerResolver

class NodeJsLambdaHandlerResolver : LambdaHandlerResolver {

    override fun version(): Int = 1

    // TODO
    override fun findPsiElements(
        project: Project,
        handler: String,
        searchScope: GlobalSearchScope
    ): Array<NavigatablePsiElement> = NavigatablePsiElement.EMPTY_NAVIGATABLE_ELEMENT_ARRAY

    // TODO
    override fun determineHandler(element: PsiElement): String? = null

    override fun determineHandlers(element: PsiElement, file: VirtualFile): Set<String> =
        determineHandler(element)?.let { setOf(it) }.orEmpty()
}
