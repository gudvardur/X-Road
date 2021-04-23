/**
 * The MIT License
 * Copyright (c) 2019- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ee.ria.xroad.proxy.serverproxy;

import ee.ria.xroad.common.util.CachingStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/*
 * Class for parsing Openapi3 description and replacing strings in servers.url-node with anonymised value
 */
public class Openapi3Anonymiser {

    public static final String SERVERS = "servers";
    public static final String URL = "url";
    public static final String PLACEHOLDER = "\"http://example.org/xroad-example\"";
    private ObjectMapper objectMapper;

    public Openapi3Anonymiser(OpenapiDescriptionFiletype fileType) {
        if (OpenapiDescriptionFiletype.JSON.equals(fileType)) {
            objectMapper = new ObjectMapper(new JsonFactory());
        } else {
            objectMapper = new ObjectMapper(new YAMLFactory());
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        }
    }

    public void anonymise(InputStream input, CachingStream output) throws IOException {
        ObjectNode tree = (ObjectNode) objectMapper.readTree(input);
        JsonNode data = parseObject(tree, false);
        objectMapper.writeValue(output, data);
    }

    /*
     * Parse Json object recursively.
     */
    private JsonNode parseObject(JsonNode node, boolean isChildForServers) throws IOException {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();

            if (field.getValue().isObject()) {
                if (SERVERS.equals(field.getKey())) {
                    parseObject(field.getValue(), true);
                } else {
                    parseObject(field.getValue(), false);
                }

            } else if (field.getValue().isArray()) {
                boolean isChild = isChildForServers || SERVERS.equals(field.getKey());
                parseArray(field.getValue(), isChild);
            }

            if (isChildForServers && URL.equals(field.getKey())) {
                JsonNode jsonNode = objectMapper.readTree(PLACEHOLDER);
                field.setValue(jsonNode);

            }

        }
        return node;
    }

    // Parse array that are of type object or array
    private JsonNode parseArray(JsonNode node, boolean isChildForServers) throws IOException {
        Iterator<JsonNode> iterator = node.iterator();

        while (iterator.hasNext()) {
            JsonNode next = iterator.next();

            // Only objects and Arrays are worth parsing
            if (next.isArray()) {
                parseArray(next, isChildForServers);
            } else if (next.isObject()) {
                parseObject(next, isChildForServers);
            }
        }

        return node;
    }
}