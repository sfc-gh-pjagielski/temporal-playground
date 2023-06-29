package com.snowflake

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.testing.TestWorkflowExtension
import io.temporal.worker.Worker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class UpsertSearchAttributeTest {

    @JvmField
    @RegisterExtension
    val testWorkflowExtension: TestWorkflowExtension = TestWorkflowExtension.newBuilder()
        .setWorkflowTypes(HelloWorkflowImpl::class.java)
        .registerSearchAttribute(CUSTOM_SEARCH_KEY.name, CUSTOM_SEARCH_KEY.valueType)
        .setDoNotStart(true)
        .build()

    @Test
    fun `should upsert search attribute from within workflow`(testEnv: TestWorkflowEnvironment, worker: Worker) {
        worker.registerActivitiesImplementations(GreetingActivitiesImpl())
        testEnv.start()
        val uuids = (0..1000).map { _ -> UUID.randomUUID() }
        val completableFutures: List<CompletableFuture<String>> = uuids
            .map { key: UUID ->
                val workflowOptions = WorkflowOptions.newBuilder()
                    .setWorkflowId(key.toString())
                    .setTaskQueue(worker.taskQueue)
                    .build()
                val newWorkflow: HelloWorkflow = testEnv
                    .workflowClient
                    .newWorkflowStub(HelloWorkflow::class.java, workflowOptions)
                WorkflowClient.execute(newWorkflow::getGreeting, key, "World")
            }
        CompletableFuture.allOf(*completableFutures.toTypedArray())
            .get(5, TimeUnit.SECONDS)

        testEnv.shutdown()
    }
}
