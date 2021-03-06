/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.compiler.server;

import io.netty.channel.Channel;
import org.jetbrains.jps.api.CmdlineRemoteProto;

import java.util.UUID;

/**
 * @author Eugene Zhuravlev
 *         Date: 4/25/12
 */
public interface BuilderMessageHandler {
  BuilderMessageHandler DEAF = new BuilderMessageHandler() {
    @Override
    public void buildStarted(UUID sessionId) {
    }

    @Override
    public void handleBuildMessage(Channel channel, UUID sessionId, CmdlineRemoteProto.Message.BuilderMessage msg) {
    }

    @Override
    public void handleFailure(UUID sessionId, CmdlineRemoteProto.Message.Failure failure) {
    }

    @Override
    public void sessionTerminated(UUID sessionId) {
    }
  };
  
  void buildStarted(UUID sessionId);
  
  void handleBuildMessage(Channel channel, UUID sessionId, CmdlineRemoteProto.Message.BuilderMessage msg);

  void handleFailure(UUID sessionId, CmdlineRemoteProto.Message.Failure failure);

  void sessionTerminated(UUID sessionId);
}
