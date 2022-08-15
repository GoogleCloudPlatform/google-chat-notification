/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendNotificationToGoogleChat {

    public void sendMessage(@NonNull String chatURL, @NonNull String templateFile_bucket_URL,
            @NonNull String location_bucket, @NonNull JsonNode data)
            throws IOException, URISyntaxException, InterruptedException {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        Template template = handlebars.compileInline(getHBSTemplateAsString(templateFile_bucket_URL, location_bucket));
        Context context = Context
                .newBuilder(data)
                .resolver(JsonNodeValueResolver.INSTANCE)
                .build();
        String message = template.apply(context);
        log.debug("message to send {}",message);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(chatURL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, BodyHandlers.ofString());
        log.debug("Response code from chat message {}", response.statusCode());
        if (response.statusCode() >= 400) {
            System.exit(-1);
        }
    }

    private String getHBSTemplateAsString(String bucket_url, String location) {
        Storage storage = StorageOptions.newBuilder()
                .build()
                .getService();
        return new String(storage.readAllBytes(bucket_url, location));
    }

}
