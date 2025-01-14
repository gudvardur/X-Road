/**
 * The MIT License
 *
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
package org.niis.xroad.restapi.openapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.niis.xroad.restapi.exceptions.ErrorDeviation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import static org.niis.xroad.restapi.exceptions.DeviationCodes.ERROR_OPENAPI_FILE_NOT_FOUND;

/**
 * OpenAPI controller
 */
@Controller
@RequestMapping(ApiUtil.API_V1_PREFIX)
@Slf4j
@RequiredArgsConstructor
public class OpenapiApiController implements OpenapiApi {

    static final String OPENAPI_DEFINITION_FILENAME = "openapi-definition.yaml";

    @Override
    public ResponseEntity<Resource> downloadOpenApi() {
        try {
            byte[] bytes = IOUtils.toByteArray(new ClassPathResource(OPENAPI_DEFINITION_FILENAME).getInputStream());
            return ApiUtil.createAttachmentResourceResponse(bytes, OPENAPI_DEFINITION_FILENAME);
        } catch (IOException e) {
            log.error("Error reading OpenAPI definition file", e);
            throw new InternalServerErrorException(new ErrorDeviation(ERROR_OPENAPI_FILE_NOT_FOUND));
        }
    }
}
