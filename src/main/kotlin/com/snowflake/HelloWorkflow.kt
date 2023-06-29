package com.snowflake

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.common.SearchAttributeKey
import io.temporal.common.SearchAttributeUpdate
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

val CUSTOM_SEARCH_KEY: SearchAttributeKey<String> = SearchAttributeKey.forText("MY_KEY")

@WorkflowInterface
interface HelloWorkflow {
    @WorkflowMethod
    fun getGreeting(key: UUID, name: String): String
}


@ActivityInterface
interface GreetingActivities {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String
}


class HelloWorkflowImpl : HelloWorkflow {
    private val activities = Workflow.newActivityStub(
        GreetingActivities::class.java,
        ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build()
    )

    override fun getGreeting(key: UUID, name: String): String {
        Workflow.upsertTypedSearchAttributes(
            SearchAttributeUpdate.valueSet(CUSTOM_SEARCH_KEY, key.toString()));
        return activities.composeGreeting("Hello", name)
    }
}

internal class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {
        log.info("Composing greeting...")
        return "$greeting $name!"
    }

    companion object {
        private val log = LoggerFactory.getLogger(GreetingActivitiesImpl::class.java)
    }
}
