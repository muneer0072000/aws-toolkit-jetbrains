// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.lambda.nodejs

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.openapi.project.Project
import com.intellij.util.text.SemVer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import software.amazon.awssdk.services.lambda.model.Runtime
import softwere.aws.toolkits.jetbrains.utils.rules.MockNodeJsInterpreter
import softwere.aws.toolkits.jetbrains.utils.rules.NodeJsCodeInsightTestFixtureRule

class NodeJsRuntimeGroupTest {

    @Rule
    @JvmField
    val projectRule = NodeJsCodeInsightTestFixtureRule()

    private val sut = NodeJsRuntimeGroup()

    @Test
    fun testRuntime403() {
        projectRule.project.setNodeJsInterpreterVersion(SemVer("v4.3.0", 4, 3, 0))
        val runtime = sut.determineRuntime(projectRule.project)
        assertThat(runtime).isNull()
    }

    @Test
    fun testRuntime610() {
        projectRule.project.setNodeJsInterpreterVersion(SemVer("v6.10.3", 6, 10, 3))
        val runtime = sut.determineRuntime(projectRule.module)
        assertThat(runtime).isEqualTo(Runtime.NODEJS6_10)
    }

    @Test
    fun testRuntime810() {
        projectRule.project.setNodeJsInterpreterVersion(SemVer("v8.10.0", 8, 10, 0))
        val runtime = sut.determineRuntime(projectRule.module)
        assertThat(runtime).isEqualTo(Runtime.NODEJS8_10)
    }

    @Test
    fun testRuntime1010() {
        projectRule.project.setNodeJsInterpreterVersion(SemVer("v10.10.0", 10, 10, 0))
        val runtime = sut.determineRuntime(projectRule.project)
        assertThat(runtime).isNull()
    }

    private fun Project.setNodeJsInterpreterVersion(version: SemVer) {
        NodeJsInterpreterManager.getInstance(this).setInterpreterRef(
            NodeJsInterpreterRef.create(MockNodeJsInterpreter(version)))
    }
}