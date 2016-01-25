/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.concurrent;

import org.eclipse.persistence.json.bind.defaultmapping.specific.CustomerTest;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Customer;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests consistency along sharing instances of Jsonb with different JsonbConfig between threads.
 * Tests simulates web server like environment, where threads dispatching http requests are pooled and reused.
 *
 * Each of jsonb instances has different JsonbConfig, forcing to produce / parse
 * different JSON string during marshalling to / unmarshalling from json.
 *
 * If ThreadLocal JsonbContext will be inconsistently shared between pooled threads
 * this test should fail while testing results of calls to shared jsonb instances marshalling / unmarshalling methods.
 *
 * @author Roman Grigoriadi
 */
public class MultiTenancyTest extends CustomerTest {

    /**
     * Expected JSON for defaultJsonBinding instance, default key names.
     */
    private static final String DEFAULT_CONFIG_JSON_STRING = "{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"MULTI_TENANCY_TEST\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]}";

    /**
     * Expected JSON for customizedJsonBinding instance, configured with LOWER_CASE_WITH_UNDERSCORES property naming strategy key names and also skips some properties according to custom PropertyVisibilityStrategy.
     */
    private static final String CUSTOMIZED_CONFIG_JSON_STRING = "{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"list_of_lists_of_integers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"MULTI_TENANCY_TEST\",\"string_integer_map\":{\"first\":1,\"second\":2}}";

    /**
     * A size of thread pool. Should be lesser than TOTAL_JOB_COUNT, so that threads are reused for different tasks,
     * causing more likeness to fail ThreadLocal state managing.
     */
    private static final int THREAD_COUNT = 35;
    /**
     * A total count of marshalling / unmarshalling operations.
     */
    private static final int TOTAL_JOB_COUNT = 2000;

    /**
     * After results of marshalling / unmarshalling are checked this count is incremented.
     * When test finishes, this count is required to be equal to TOTAL_JOB_COUNT.
     */
    private final AtomicLong resultsCheckedCount = new AtomicLong();

    /**
     * Latch firing first THREAD_COUNT tasks instantly.
     */
    private final static CountDownLatch fireJsonbProcessing = new CountDownLatch(1);

    private static final Jsonb defaultJsonBinding;
    private static final Jsonb customizedJsonBinding;

    public enum ConfigurationType {
        DEFAULT(defaultJsonBinding, DEFAULT_CONFIG_JSON_STRING),
        CUSTOMIZED(customizedJsonBinding, CUSTOMIZED_CONFIG_JSON_STRING);

        ConfigurationType(Jsonb jsonbInstance, String expectedJson) {
            this.jsonbInstance = jsonbInstance;
            this.expectedJson = expectedJson;
        }

        private Jsonb jsonbInstance;
        private String expectedJson;

        public Jsonb getJsonbInstance() {
            return jsonbInstance;
        }

        public String getExpectedJson() {
            return expectedJson;
        }
    }

    /**
     * Thread pool for JSONB processing.
     */
    private ExecutorService jsonbProcessingThreadPool;
    private CompletionService<JsonProcessingResult<MarshallerTaskResult>> marshallingCompletion;
    private CompletionService<JsonProcessingResult<Customer>> unmarshallingCompletion;

    /**
     * Executor for checking results.
     */
    private ExecutorService resultCheckService;

    /**
     * Jsonb instances configuration initialisation.
     */
    static {
        JsonbConfig defaultConfig = new JsonbConfig();

        JsonbConfig customizedConfig = new JsonbConfig();
        customizedConfig.setProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY, PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        customizedConfig.setProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY, new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                final String name = field.getName();
                return !(name.equals("strings") || name.equals("integers"));
            }

            @Override
            public boolean isVisible(Method method) {
                final String name = method.getName();
                return !(name.equals("getStrings") || name.equals("getIntegers"));
            }
        });


        defaultJsonBinding = JsonbBuilder.create(defaultConfig);
        customizedJsonBinding = JsonbBuilder.create(customizedConfig);
    }

    @Before
    public void setUp() throws Exception {
        jsonbProcessingThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        marshallingCompletion = new ExecutorCompletionService<>(jsonbProcessingThreadPool);
        unmarshallingCompletion = new ExecutorCompletionService<>(jsonbProcessingThreadPool);
        resultCheckService = Executors.newFixedThreadPool(2);
    }

    @Test
    public void testDataConsistency() throws Exception {
        submitJsonbProcessingTasks();
        fireJsonbProcessing.countDown();
        submitResultCheckingTasks();


        jsonbProcessingThreadPool.shutdown();
        jsonbProcessingThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        resultCheckService.shutdown();
        resultCheckService.awaitTermination(5000, TimeUnit.MILLISECONDS);

        //Final check, that successful task result check count matches count of tasks that were submitted.
        assertEquals(TOTAL_JOB_COUNT, resultsCheckedCount.get());
    }

    /**
     * Picks marshalling / unmarshalling results from completion service queue and check if they
     * are equal their expected prototypes.
     *
     * If ThreadLocal JsonbContext is incorrectly shared between pooled threads, configuration of jsonb instances
     * would be stale and results will not match.
     */
    private void submitResultCheckingTasks() {
        resultCheckService.execute(new ResultChecker<MarshallerTaskResult>(marshallingCompletion) {
            @Override
            protected void checkResult(JsonProcessingResult<MarshallerTaskResult> result) {
                MarshallerTaskResult marshallerResult = result.getResult();
                //actual check, produced json by marshaller is equal to expected by configuration, which triggered marshalling task.
                assertEquals(marshallerResult.getConfigurationType().getExpectedJson(), marshallerResult.getProducedJson());
                resultsCheckedCount.incrementAndGet();
                System.out.println(String.format("Job %-32s dispatched by thread %-16s completed successfully.", result.getJobId(), result.getDispatcherThreadName()));
            }
        });

        resultCheckService.execute(new ResultChecker<Customer>(unmarshallingCompletion) {
            @Override
            protected void checkResult(JsonProcessingResult<Customer> result) {
                //actual check, unmarshalled json result have all expected values.
                //if JsonbConfig is stale, key names will not match, and this assert will not pass.
                assertCustomerValues(result.getResult(), "MULTI_TENANCY_TEST");
                resultsCheckedCount.incrementAndGet();
                System.out.println(String.format("Job %-32s dispatched by thread %-16s completed successfully.", result.getJobId(), result.getDispatcherThreadName()));
            }
        });
    }

    /**
     * Chooses shared jsonb instance, either with default or customized key names
     * and submits marshaller an unmarshaller task for it.
     */
    private void submitJsonbProcessingTasks() {
        for(int i = 0; i< TOTAL_JOB_COUNT; i+=2) {
            boolean even = (i % 4 == 0);
            ConfigurationType jsonbConfiguration = even ? ConfigurationType.DEFAULT : ConfigurationType.CUSTOMIZED;

            final String unmarshallerJobId = jsonbConfiguration.name() + "_Unmarshaller_ID_" + i;
            final UnmarshallerTask task = new UnmarshallerTask(fireJsonbProcessing, jsonbConfiguration.getExpectedJson(), jsonbConfiguration.getJsonbInstance(), unmarshallerJobId);
            unmarshallingCompletion.submit(task);

            final String marshallerJobId = jsonbConfiguration.name() + "_Marshaller_ID_" + i;
            final MarshallerTask marshallerTask = new MarshallerTask(fireJsonbProcessing, jsonbConfiguration, marshallerJobId, createCustomer("MULTI_TENANCY_TEST"));
            marshallingCompletion.submit(marshallerTask);
        }
    }

}
