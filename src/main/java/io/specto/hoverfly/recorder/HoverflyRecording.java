/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.recorder;

import java.util.Collection;
import java.util.Map;

public class HoverflyRecording {
    public final HoverFlyRequest request;
    public final HoverFlyResponse response;

    public HoverflyRecording(final HoverFlyRequest request, final HoverFlyResponse response) {
        this.request = request;
        this.response = response;
    }

    public static final class HoverFlyRequest {
        private final String path;
        private final String method;
        private final String destination;
        private final String query;
        private final String body;

        public HoverFlyRequest(final String path,
                               final String method,
                               final String destination,
                               final String query,
                               final String body) {
            this.path = path;
            this.method = method;
            this.destination = destination;
            this.query = query;
            this.body = body;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public String getDestination() {
            return destination;
        }

        public String getQuery() {
            return query;
        }

        public String getBody() {
            return body;
        }
    }

    public static final class HoverFlyResponse {
        private final int status;
        private final String body;
        private final Map<String, Collection<String>> headers;

        public HoverFlyResponse(final int status,
                                final String body,
                                final Map<String, Collection<String>> headers) {
            this.status = status;
            this.body = body;
            this.headers = headers;
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }

        public Map<String, Collection<String>> getHeaders() {
            return headers;
        }
    }
}
