/*
 * Copyright © 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.atlassian.dev.resource.provider;

import org.dom4j.Element;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformer;

/**
 * TBD.
 */
public class DevResourceProviderTransformer implements WebResourceTransformer {

  private static final String BASE_URL = "EVERIT_DEV_RESOURCE_PROVIDER_URL";

  private static final String DEFAULT_BASE_URL = "http://localhost:9990";

  @Override
  public DownloadableResource transform(
      final Element configElement,
      final ResourceLocation location,
      final String filePath,
      final DownloadableResource nextResource) {
    String baseUrl = System.getenv(DevResourceProviderTransformer.BASE_URL);
    if (baseUrl == null) {
      baseUrl = DevResourceProviderTransformer.DEFAULT_BASE_URL;
    }
    return new UrlBasedDownloadableResource(baseUrl, location);
  }

}
