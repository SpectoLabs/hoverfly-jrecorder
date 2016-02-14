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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toMap;

public class HoverflyFilter implements Filter {

    private final String simulatedBaseUrl;
    private final List<HoverflyRecording> hoverflyRecordings = Lists.newArrayList();
    private String outputDirectory;

    public HoverflyFilter(final String simulatedBaseUrl, final String outputDirectory) {
        this.simulatedBaseUrl = simulatedBaseUrl;
        this.outputDirectory = outputDirectory;
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) servletResponse);

        try {
            filterChain.doFilter(servletRequest, responseCopier);
            responseCopier.flushBuffer();
        } finally {
            final HoverflyRecording.HoverFlyRequest hoverFlyRequest = recordRequest((HttpServletRequest) servletRequest);
            final HoverflyRecording.HoverFlyResponse hoverFlyResponse = recordResponse(responseCopier);
            hoverflyRecordings.add(new HoverflyRecording(hoverFlyRequest, hoverFlyResponse));
        }
    }

    private HoverflyRecording.HoverFlyRequest recordRequest(final HttpServletRequest request) throws IOException {
        final String path = request.getPathInfo();
        final String query = request.getQueryString();
        final String requestMethod = request.getMethod();
        final String destination = simulatedBaseUrl;
        final String requestBody = request.getReader() != null ? CharStreams.toString(request.getReader()) : "";
        return new HoverflyRecording.HoverFlyRequest(path, requestMethod, destination, query, requestBody);
    }

    private HoverflyRecording.HoverFlyResponse recordResponse(final HttpServletResponseCopier responseCopier) throws UnsupportedEncodingException {
        final int status = responseCopier.getStatus();
        final String responseBody = new String(responseCopier.getCopy(), responseCopier.getCharacterEncoding());
        final Map<String, Collection<String>> headers = responseCopier.getHeaderNames().stream()
                .collect(toMap(h -> h, responseCopier::getHeaders));
        return new HoverflyRecording.HoverFlyResponse(status, responseBody, headers);
    }

    @Override
    public void destroy() {
        final File file = Paths.get(outputDirectory).toFile();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            new ObjectMapper().writeValue(file, new HoverflyData(hoverflyRecordings));
        } catch (IOException e) {
            throw new RuntimeException("Cannot save hoverfly json", e);
        }
    }
}
