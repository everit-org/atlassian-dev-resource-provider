# Everit - Atalassian DEV Resource Provider

A WebResourceTransformer that can serve web resources from an external server like NodeJS.

## Usage

1. Download and install the "Everit - Atlassian DEV Resource Provider" app from [here](TBD).
2. Modify your app:

### Modify your ```atlassian-plugin.xml```

 * Diable caching by configuring the ```batch``` parameter dynamically.
 * Apply the extra transformers dynamically.

Before:

```
  <web-resource ...>
    ...
    <resource type="download" name="myapp.js" location="/js/myapp.js" />
    <transformation extension="js">
      <transformer key="myTransformer" />
    </transformation>
    ...
  </web-resource>
```

After: 

```
  <web-resource ...>
    ...
    <resource type="download" name="myapp.js" location="/js/myapp.js">
      <param name="batch" value="${webresource.batch.enabled}" />
    </resource>
    <transformation extension="js">
      <transformer key="myTransformer" />
      ${extraTransformers}
    </transformation>
    ...
  </web-resource>
```

### Modify your ```pom.xml```

 * Define the default values of parameters.
 * Define the development values of parameters in a separated Maven profile.
 
Default properties:

```
  <properties>
  ...
    <webresource.batch.enabled>true</webresource.batch.enabled>
    <extraTransformers></extraTransformers>
  ...
  </properties>
```

Development profile:

```
  <profiles>
    ...
    <profile>
      <id>dev</id>
      <properties>
        <webresource.batch.enabled>false</webresource.batch.enabled>
        <extraTransformers>&lt;transformer key=&quot;devResourceProviderTransformer&quot;/&gt;</extraTransformers>
      </properties>
    </profile>
    ...
  </profiles>
```

## Configuration

The web resources are served from ```http://localhost:9990``` by default. If you have a 
location defined in your ```atlassian-plugin.xml``` for example: ```location="/js/myapp.js"```
then it will be served from ```http://localhost:9990/js/myapp.js```.

This default address can be changed by defining the environment variable 
```EVERIT_DEV_RESOURCE_PROVIDER_URL```.

Example:

```
set EVERIT_DEV_RESOURCE_PROVIDER_URL=http://foo.com/bar
```

The resources will be served from ```http://foo.com/bar/js/myapp.js```.

## Troubleshooting

 * I have configured the ```EVERIT_DEV_RESOURCE_PROVIDER_URL``` environment variable but 
the resources are still served from the default URL.

Please double check that you configured the environment properly and the user that runs the Jira 
process can access it. If everything is set up correctly, restart your Jira.

 * Caused by: java.io.FileNotFoundException: http://wwwfoo.com/bar/js/myapp.js

Your server that serves the resources is not running or cannot be access by your Jira. Or it can be
a misconfigured URL.

