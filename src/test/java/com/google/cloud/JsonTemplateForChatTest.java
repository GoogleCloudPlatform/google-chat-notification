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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonTemplateForChatTest {

    @DisplayName("Single test for Card")
    @Test
    void testCard() throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode masterJSON = mapper.readTree(this.getClass().getClassLoader().getResource("input_01.json"));
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        Template template = handlebars.compile("template_01");

        Context context = Context
                .newBuilder(masterJSON)
                .resolver(JsonNodeValueResolver.INSTANCE)
                .build();
        log.debug(template.apply(context));
        log.info("Success");
    }

    @DisplayName("Single test for Message")
    @Test
    void testMessage() throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode masterJSON = mapper.readTree(this.getClass().getClassLoader().getResource("input_02.json"));
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        Template template = handlebars.compile("message");

        Context context = Context
                .newBuilder(masterJSON)
                .resolver(JsonNodeValueResolver.INSTANCE)
                .build();
        log.debug(template.apply(context));
    
    }
}
