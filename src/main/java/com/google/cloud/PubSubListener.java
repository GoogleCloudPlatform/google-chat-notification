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
import java.net.URISyntaxException;
import java.util.Base64;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.pubsub.v1.Message;
import com.google.events.cloud.pubsub.v1.MessagePublishedData;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubSubListener implements CloudEventsFunction {

    private final String CHAT_URL = System.getenv("CHAT_URL");
    private final String TEMPLATE_FILE_GCS = System.getenv("TEMPLATE_FILE_BUCKET");
    private final String LOCATION_BUCKET = System.getenv("TEMPLATE_FILE_OBJECT");

    @Override
    public void accept(CloudEvent event) throws IOException, URISyntaxException, InterruptedException {
        String cloudEventData = new String(event.getData().toBytes());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MessagePublishedData data = mapper.readValue(cloudEventData, MessagePublishedData.class);
        Message message = data.getMessage();
        String encodedData = message.getData();
        String decodedData = new String(Base64.getDecoder().decode(encodedData));
        log.debug("Pub/Sub message: " + decodedData);

        SendNotificationToGoogleChat send = new SendNotificationToGoogleChat();
        send.sendMessage(CHAT_URL, TEMPLATE_FILE_GCS, LOCATION_BUCKET, mapper.readTree(decodedData));

    }

}
