// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package softwere.aws.toolkits.jetbrains.utils.rules

import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.runInEdtAndGet
import com.intellij.util.text.SemVer
import com.intellij.webcore.moduleType.WebModuleTypeManager
import software.amazon.awssdk.services.lambda.model.Runtime
import software.aws.toolkits.jetbrains.utils.rules.CodeInsightTestFixtureRule

/**
 * JUnit test Rule that will create a Light [Project] and [CodeInsightTestFixture] with NodeJs support. Projects are
 * lazily created and are torn down after each test.
 *
 * If you wish to have just a [Project], you may use Intellij's [com.intellij.testFramework.ProjectRule]
 */
class NodeJsCodeInsightTestFixtureRule : CodeInsightTestFixtureRule() {
    override fun createTestFixture(): CodeInsightTestFixture {
        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        val projectFixture = fixtureFactory.createLightFixtureBuilder(NodeJsLightProjectDescriptor())
        val codeInsightFixture = fixtureFactory.createCodeInsightFixture(projectFixture.fixture)
        codeInsightFixture.setUp()
        codeInsightFixture.testDataPath = testDataPath
        PsiTestUtil.addContentRoot(codeInsightFixture.module, codeInsightFixture.tempDirFixture.getFile("."))
        return codeInsightFixture
    }
}

class NodeJsLightProjectDescriptor : LightProjectDescriptor() {
    override fun getModuleType(): ModuleType<*> = WebModuleTypeManager.getInstance().defaultModuleType
    override fun getSdk(): Sdk? = null
}

class MockNodeJsInterpreter(private var version: SemVer) : NodeJsLocalInterpreter("/path/to/$version/mock/node") {
    init {
        NodeJsLocalInterpreterManager.getInstance().interpreters =
            NodeJsLocalInterpreterManager.getInstance().interpreters + listOf(this)
    }

    override fun getCachedVersion(): Ref<SemVer>? = Ref(version)
}

fun CodeInsightTestFixture.addNodeJsHandler(
    subPath: String = ".",
    fileName: String = "app",
    handlerName: String = "lambdaHandler"
): PsiElement {
    val fileContent =
        """
        exports.$handlerName = function (event, context, callback) {
            return 'HelloWorld'
        };
        """.trimIndent()

    val psiFile = this.addFileToProject("$subPath/$fileName.js", fileContent) as JSFile

    return runInEdtAndGet {
        psiFile.findElementAt(fileContent.indexOf(handlerName))!!
    }
}

fun CodeInsightTestFixture.addPackageJsonFile(
    subPath: String = ".",
    content: String = """
        {
            "name": "hello-world",
            "version": "1.0.0"
        }
    """.trimIndent()
): PsiFile = this.addFileToProject("$subPath/package.json", content)

fun CodeInsightTestFixture.addSamTemplate(
    logicalName: String = "Function",
    codeUri: String,
    handler: String,
    runtime: Runtime
): PsiFile = this.addFileToProject(
        "template.yaml",
        """
        Resources:
          $logicalName:
            Type: AWS::Serverless::Function
            Properties:
              CodeUri: $codeUri
              Handler: $handler
              Runtime: $runtime
              Timeout: 900
        """.trimIndent()
    )
