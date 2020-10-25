/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.hadoop.hdds.scm.pipeline;

import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.MockDatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos.ReplicationType;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos.ReplicationFactor;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.safemode.SCMSafeModeManager;
import org.apache.hadoop.hdds.server.events.EventPublisher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mock PipelineManager implementation for testing.
 */
public final class MockPipelineManager implements PipelineManager {

  private PipelineStateManager stateManager;

  public static PipelineManager getInstance() {
    return new MockPipelineManager();
  }

  private MockPipelineManager() {
    this.stateManager = new PipelineStateManager();
  }

  @Override
  public Pipeline createPipeline(final ReplicationType type,
                                 final ReplicationFactor factor)
      throws IOException {
    final List<DatanodeDetails> nodes = Stream.generate(
        MockDatanodeDetails::randomDatanodeDetails)
        .limit(factor.getNumber()).collect(Collectors.toList());
    final Pipeline pipeline = Pipeline.newBuilder()
        .setId(PipelineID.randomId())
        .setType(type)
        .setFactor(factor)
        .setNodes(nodes)
        .setState(Pipeline.PipelineState.OPEN)
        .build();
    stateManager.addPipeline(pipeline);
    return pipeline;
  }

  @Override
  public Pipeline createPipeline(final ReplicationType type,
                                 final ReplicationFactor factor,
                                 final List<DatanodeDetails> nodes) {
    return Pipeline.newBuilder()
        .setId(PipelineID.randomId())
        .setType(type)
        .setFactor(factor)
        .setNodes(nodes)
        .setState(Pipeline.PipelineState.OPEN)
        .build();
  }

  @Override
  public Pipeline getPipeline(final PipelineID pipelineID)
      throws PipelineNotFoundException {
    return stateManager.getPipeline(pipelineID);
  }

  @Override
  public boolean containsPipeline(final PipelineID pipelineID) {
    try {
      return stateManager.getPipeline(pipelineID) != null;
    } catch (PipelineNotFoundException e) {
      return false;
    }
  }

  @Override
  public List<Pipeline> getPipelines() {
    return stateManager.getPipelines();
  }

  @Override
  public List<Pipeline> getPipelines(final ReplicationType type) {
    return stateManager.getPipelines(type);
  }

  @Override
  public List<Pipeline> getPipelines(final ReplicationType type,
                                     final ReplicationFactor factor) {
    return stateManager.getPipelines(type, factor);
  }

  @Override
  public List<Pipeline> getPipelines(final ReplicationType type,
                                     final Pipeline.PipelineState state) {
    return stateManager.getPipelines(type, state);
  }

  @Override
  public List<Pipeline> getPipelines(final ReplicationType type,
                                     final ReplicationFactor factor,
                                     final Pipeline.PipelineState state) {
    return stateManager.getPipelines(type, factor, state);
  }

  @Override
  public List<Pipeline> getPipelines(final ReplicationType type,
      final ReplicationFactor factor, final Pipeline.PipelineState state,
      final Collection<DatanodeDetails> excludeDns,
      final Collection<PipelineID> excludePipelines) {
    return stateManager.getPipelines(type, factor, state,
        excludeDns, excludePipelines);
  }

  @Override
  public void addContainerToPipeline(final PipelineID pipelineID,
                                     final ContainerID containerID)
      throws IOException {
    stateManager.addContainerToPipeline(pipelineID, containerID);
  }

  @Override
  public void removeContainerFromPipeline(final PipelineID pipelineID,
                                          final ContainerID containerID)
      throws IOException {
    stateManager.removeContainerFromPipeline(pipelineID, containerID);
  }

  @Override
  public NavigableSet<ContainerID> getContainersInPipeline(
      final PipelineID pipelineID) throws IOException {
    return getContainersInPipeline(pipelineID);
  }

  @Override
  public int getNumberOfContainers(final PipelineID pipelineID)
      throws IOException {
    return getContainersInPipeline(pipelineID).size();
  }

  @Override
  public void openPipeline(final PipelineID pipelineId)
      throws IOException {
    stateManager.openPipeline(pipelineId);
  }

  @Override
  public void closePipeline(final Pipeline pipeline, final boolean onTimeout)
      throws IOException {
    stateManager.finalizePipeline(pipeline.getId());
  }

  @Override
  public void scrubPipeline(final ReplicationType type,
                            final ReplicationFactor factor)
      throws IOException {

  }

  @Override
  public void startPipelineCreator() {

  }

  @Override
  public void triggerPipelineCreation() {

  }

  @Override
  public void incNumBlocksAllocatedMetric(final PipelineID id) {

  }

  @Override
  public int minHealthyVolumeNum(Pipeline pipeline) {
    return 0;
  }

  @Override
  public int minPipelineLimit(Pipeline pipeline) {
    return 0;
  }

  @Override
  public void activatePipeline(final PipelineID pipelineID)
      throws IOException {

  }

  @Override
  public void deactivatePipeline(final PipelineID pipelineID)
      throws IOException {
    stateManager.deactivatePipeline(pipelineID);
  }

  @Override
  public boolean getSafeModeStatus() {
    return false;
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public Map<String, Integer> getPipelineInfo() {
    return null;
  }

  @Override
  public void onMessage(final SCMSafeModeManager.SafeModeStatus safeModeStatus,
                        final EventPublisher publisher) {

  }
}