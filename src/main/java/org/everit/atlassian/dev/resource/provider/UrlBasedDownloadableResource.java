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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;

/**
 * TBD.
 */
public class UrlBasedDownloadableResource implements DownloadableResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(UrlBasedDownloadableResource.class);

  private final String baseUrl;

  private final ResourceLocation resourceLocation;

  public UrlBasedDownloadableResource(
      final String baseUrl,
      final ResourceLocation resourceLocation) {
    this.baseUrl = baseUrl;
    this.resourceLocation = resourceLocation;
  }

  @Override
  public String getContentType() {
    return this.resourceLocation.getContentType();
  }

  /**
   * TBD.
   */
  protected InputStream getResourceAsStream(final ResourceLocation resourceLocation) {
    try {
      return new URL(this.baseUrl + resourceLocation.getLocation()).openStream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean isResourceModified(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse) {
    return true;
  }

  @Override
  public void serveResource(final HttpServletRequest request, final HttpServletResponse response)
      throws DownloadException {
    LOGGER.debug("Serving: {}", this);

    final InputStream resourceStream = getResourceAsStream(this.resourceLocation);
    if (resourceStream == null) {
      LOGGER.warn("Resource not found: {}", this);
      return;
    }

    final String contentType = getContentType();
    if (StringUtils.isNotBlank(contentType)) {
      response.setContentType(contentType);
    }

    final OutputStream out;
    try {
      out = response.getOutputStream();
    } catch (final IOException e) {
      throw new DownloadException(e);
    }

    streamResource(resourceStream, out);
    LOGGER.debug("Serving file done.");
  }

  /**
   * Copy from the supplied OutputStream to the supplied InputStream. Note that the InputStream will
   * be closed on completion.
   *
   * @param in
   *          the stream to read from
   * @param out
   *          the stream to write to
   * @throws DownloadException
   *           if an IOException is encountered writing to the out stream
   */
  private void streamResource(final InputStream in, final OutputStream out)
      throws DownloadException {
    try {
      IOUtils.copy(in, out);
    } catch (final IOException e) {
      throw new DownloadException(e);
    } finally {
      IOUtils.closeQuietly(in);
      try {
        out.flush();
      } catch (final IOException e) {
        LOGGER.debug("Error flushing output stream", e);
      }
    }
  }

  @Override
  public void streamResource(final OutputStream out) throws DownloadException {
    InputStream resourceStream = getResourceAsStream(this.resourceLocation);
    if (resourceStream == null) {
      LOGGER.warn("Resource not found: {}", this);
      return;
    }

    streamResource(resourceStream, out);
  }

  @Override
  public String toString() {
    return "Resource: " + this.resourceLocation.getLocation() + " (" + getContentType() + ")";
  }

}
