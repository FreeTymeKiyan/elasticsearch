/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.cluster.node.tasks.list;

import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.TaskOperationFailure;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.tasks.TransportTasksAction;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.tasks.TaskManager;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class TransportListTasksAction extends TransportTasksAction<ListTasksRequest, ListTasksResponse, TaskInfo> {

    @Inject
    public TransportListTasksAction(Settings settings, ClusterName clusterName, ThreadPool threadPool, ClusterService clusterService, TransportService transportService, ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver) {
        super(settings, ListTasksAction.NAME, clusterName, threadPool, clusterService, transportService, actionFilters, indexNameExpressionResolver, ListTasksRequest::new, ListTasksResponse::new, ThreadPool.Names.MANAGEMENT);
    }

    @Override
    protected ListTasksResponse newResponse(ListTasksRequest request, List<TaskInfo> tasks, List<TaskOperationFailure> taskOperationFailures, List<FailedNodeException> failedNodeExceptions) {
        return new ListTasksResponse(tasks, taskOperationFailures, failedNodeExceptions);
    }

    @Override
    protected TaskInfo readTaskResponse(StreamInput in) throws IOException {
        return new TaskInfo(in);
    }

    @Override
    protected TaskInfo taskOperation(ListTasksRequest request, Task task) {
        return task.taskInfo(clusterService.localNode(), request.detailed());
    }

    @Override
    protected boolean accumulateExceptions() {
        return true;
    }
}
