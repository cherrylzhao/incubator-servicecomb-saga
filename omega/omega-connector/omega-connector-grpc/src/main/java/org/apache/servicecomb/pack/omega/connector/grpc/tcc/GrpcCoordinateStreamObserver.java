/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.pack.omega.connector.grpc.tcc;

import io.grpc.stub.StreamObserver;
import java.lang.invoke.MethodHandles;
import org.apache.servicecomb.pack.contract.grpc.GrpcTccCoordinateCommand;
import org.apache.servicecomb.pack.omega.connector.grpc.core.ErrorHandleEngineManager;
import org.apache.servicecomb.pack.omega.connector.grpc.core.StreamObserverManager;
import org.apache.servicecomb.pack.omega.transaction.MessageHandlerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcCoordinateStreamObserver implements StreamObserver<GrpcTccCoordinateCommand> {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void onNext(GrpcTccCoordinateCommand command) {
    LOG.info("Received coordinate command, global tx id: {}, local tx id: {}, call method: {}",
        command.getGlobalTxId(), command.getLocalTxId(), command.getMethod());
    MessageHandlerManager.getTccHandler().onReceive(command.getGlobalTxId(), command.getLocalTxId(), command.getParentTxId(), command.getMethod());
  }

  @Override
  public void onError(Throwable t) {
    LOG.error("Failed to process grpc coordinate command.", t);
    ErrorHandleEngineManager.getEngine().offerTask(StreamObserverManager.getMessageSender(this));
  }

  @Override
  public void onCompleted() {
    // Do nothing here
  }
}
