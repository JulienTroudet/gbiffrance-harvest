package models.harvest.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author markus
 */
public class HttpUtil {
  public class Response {
    private HttpResponse response;
    public String content;

    public Response(HttpResponse resp) {
      response = resp;
    }

    public boolean containsHeader(String name) {
      return response.containsHeader(name);
    }

    public Header[] getAllHeaders() {
      return response.getAllHeaders();
    }

    public Header getFirstHeader(String name) {
      return response.getFirstHeader(name);
    }

    public Header[] getHeaders(String name) {
      return response.getHeaders(name);
    }

    public Header getLastHeader(String name) {
      return response.getLastHeader(name);
    }

    public Locale getLocale() {
      return response.getLocale();
    }

    public HttpParams getParams() {
      return response.getParams();
    }

    public ProtocolVersion getProtocolVersion() {
      return response.getProtocolVersion();
    }

    public int getStatusCode() {
      return response.getStatusLine().getStatusCode();
    }

    public StatusLine getStatusLine() {
      return response.getStatusLine();
    }

    public HeaderIterator headerIterator() {
      return response.headerIterator();
    }

    public HeaderIterator headerIterator(String name) {
      return response.headerIterator(name);
    }

  }

  private static final Logger LOG = Logger.getLogger(HttpUtil.class);
  public static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
  private static final String LAST_MODIFIED = "Last-Modified";
  private static final String MODIFIED_SINCE = "If-Modified-Since";

  private final DefaultHttpClient client;
  // date format see http://tools.ietf.org/html/rfc2616#section-3.3
  // example:
  // Wed, 21 Jul 2010 22:37:31 GMT
  protected static final SimpleDateFormat DATE_FORMAT_RFC2616 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",
      Locale.US);

  public HttpUtil() {
    super();
    this.client = newMultithreadedClient();
  }

  public HttpUtil(DefaultHttpClient client) {
    super();
    this.client = client;
  }

  @SuppressWarnings("deprecation")
public static DefaultHttpClient newMultithreadedClient() {
    HttpParams params = new BasicHttpParams();
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

    ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
    DefaultHttpClient client = new DefaultHttpClient(cm, params);
    return client;
  }

  //   DefaultHttpClient client = newMultithreadedClient();
  //  client.addRequestInterceptor(new PreemptiveAuthenticationInterceptor(), 0);
  //  return client;
  //}

  private HttpContext buildContext(String uri, UsernamePasswordCredentials credentials) throws URISyntaxException {
    HttpContext authContext = new BasicHttpContext();
    if (credentials != null) {
      URI authUri = new URI(uri);
      AuthScope scope = new AuthScope(authUri.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);

      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(scope, credentials);

      authContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
    }

    return authContext;
  }

  public UsernamePasswordCredentials credentials(String username, String password) {
    return new UsernamePasswordCredentials(StringUtils.trimToEmpty(username), StringUtils.trimToEmpty(password));
  }

  /**
   * Executes a generic DELETE request
   * 
   * @param uri
   * @param params
   * @param authenticate
   * @return
   * @throws IOException
   */
  @SuppressWarnings("deprecation")
public Response delete(String url, UsernamePasswordCredentials credentials) throws IOException, URISyntaxException {
    LOG.info("Http delete to " + url);
    HttpDelete delete = new HttpDelete(url);
    HttpContext authContext = buildContext(url, credentials);
    // HttpGet get = new HttpGet(url);
    HttpResponse response = client.execute(delete, authContext);
    Response result = new Response(response);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      result.content = EntityUtils.toString(entity);
      entity.consumeContent();
    }
    return result;
  }

  public void download(String uri, File downloadTo) throws MalformedURLException, IOException {
    download(new URL(uri), downloadTo);
  }

  public void download(URI url, File downloadTo) throws IOException {
    download(url.toURL(), downloadTo);
  }

  public String download(URL url) throws IOException {
    try {
      Response resp = get(url.toString());
      return resp.content;
    } catch (URISyntaxException e) {
      // comes from a URL instance - cant be wrong
      LOG.error("Exception thrown", e);
    }
    return null;
  }

  @SuppressWarnings("deprecation")
public void download(URL url, File downloadTo) throws IOException {
    HttpGet get = new HttpGet(url.toString());

    // execute
    HttpResponse response = client.execute(get);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      // copy stream to local file
      FileUtils.forceMkdir(downloadTo.getParentFile());
      OutputStream fos = new FileOutputStream(downloadTo, false);
      entity.writeTo(fos);
      fos.close();
    }

    // close http connection
    entity.consumeContent();

    LOG.debug("Successfully downloaded " + url + " to " + downloadTo.getAbsolutePath());
  }

  /**
   * @param url
   * @param lastModified
   * @return body content if changed or null if unmodified since lastModified
   * @throws IOException
   */
  public String downloadIfChanged(URL url, Date lastModified) throws IOException {
    Map<String, String> header = new HashMap<String, String>();
    header.put(MODIFIED_SINCE, DateFormatUtils.SMTP_DATETIME_FORMAT.format(lastModified));

    try {
      Response resp = get(url.toString(), header, null);
      if (resp.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
        LOG.debug("Content not modified since last request");
      }
      return resp.content;
    } catch (URISyntaxException e) {
      // comes from a URL instance - cant be wrong
      LOG.error("Exception thrown", e);
    }
    return null;
  }

  /**
   * Downloads a url to a file if its modified since the date given.
   * Updates the last modified file property to reflect the last servers modified http header.
   * 
   * @param url
   * @param lastModified
   * @param downloadTo file to download to
   * @return true if changed or false if unmodified since lastModified
   * @throws IOException
   */
  @SuppressWarnings("deprecation")
public boolean downloadIfChanged(URL url, Date lastModified, File downloadTo) throws IOException {
    HttpGet get = new HttpGet(url.toString());

    // prepare conditional GET request headers
    if (lastModified != null) {
      get.addHeader(MODIFIED_SINCE, DateFormatUtils.SMTP_DATETIME_FORMAT.format(lastModified));
      LOG.debug("Conditional GET: " + DateFormatUtils.SMTP_DATETIME_FORMAT.format(lastModified));
    }

    // execute
    boolean downloaded = false;
    HttpResponse response = client.execute(get);
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
      LOG.debug("Content not modified since last request");
    } else {
      Date serverModified = null;
      HttpEntity entity = response.getEntity();
      if (entity != null) {

        try {
          Header modHeader = response.getFirstHeader(LAST_MODIFIED);
          if (modHeader != null) {
            serverModified = DATE_FORMAT_RFC2616.parse(modHeader.getValue());
          }
        } catch (ParseException e) {
          LOG.debug("Cant parse http header Last-Modified date");
        }

        // copy stream to local file
        FileUtils.forceMkdir(downloadTo.getParentFile());
        OutputStream fos = new FileOutputStream(downloadTo, false);
        entity.writeTo(fos);
        fos.close();
        downloaded = true;
        // update last modified of file with http header date from server
        if (serverModified != null) {
          downloadTo.setLastModified(serverModified.getTime());
        }
      }

      // close http connection
      entity.consumeContent();

      LOG.debug("Successfully downloaded " + url + " to " + downloadTo.getAbsolutePath());
    }

    return downloaded;
  }

  /**
   * Downloads a url to a local file using conditional GET, i.e. only downloading the file again if it has been changed
   * since the last download
   * 
   * @param url
   * @param downloadTo
   * @return
   * @throws IOException
   */
  public boolean downloadIfChanged(URL url, File downloadTo) throws IOException {
    Date lastModified = null;
    if (downloadTo.exists()) {
      lastModified = new Date(downloadTo.lastModified());
    }
    return downloadIfChanged(url, lastModified, downloadTo);
  }

  public HttpResponse executeGetWithTimeout(HttpGet get, int timeout) throws ClientProtocolException, IOException {
    HttpParams httpParams = client.getParams();
    // keep old values to rest afterwards
    int ct = HttpConnectionParams.getConnectionTimeout(httpParams);
    int st = HttpConnectionParams.getSoTimeout(httpParams);

    HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
    HttpConnectionParams.setSoTimeout(httpParams, timeout);

    HttpResponse response = null;
    try {
      response = client.execute(get);
    } finally {
      // rest to previous values
      HttpConnectionParams.setConnectionTimeout(httpParams, ct);
      HttpConnectionParams.setSoTimeout(httpParams, st);
    }

    return response;
  }

  /**
   * @param url
   * @return
   * @throws JSONException if no proper json was returned
   * @throws IOException in case of a problem or the connection was aborted
   * @throws URISyntaxException
   */
  public Response get(String url) throws IOException, URISyntaxException {
    return get(url, null, null);
  }

  @SuppressWarnings("deprecation")
public Response get(String url, Map<String, String> headers, UsernamePasswordCredentials credentials) throws IOException, URISyntaxException {
    HttpGet get = new HttpGet(url);
    // http header
    if (headers != null) {
      for (String name : headers.keySet()) {
        get.addHeader(StringUtils.trimToEmpty(name), StringUtils.trimToEmpty(headers.get(name)));
      }
    }
    // authentication
    HttpContext authContext = buildContext(url, credentials);
    HttpResponse response = client.execute(get, authContext);

    Response result = new Response(response);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      // Adding a default charset in case it is not found
      result.content = EntityUtils.toString(entity, HTTP.UTF_8);
      entity.consumeContent();
    }
    return result;
  }

  public HttpParams params(Map<String, Object> params) {
    HttpParams p = new BasicHttpParams();
    for (String name : params.keySet()) {
      p.setParameter(name, params.get(name));
    }
    return p;
  }

  /**
   * Executes a generic POST request
   * 
   * @param uri
   * @param params
   * @param authenticate
   * @return
   * @throws URISyntaxException
   * @throws IOException
   */
  public Response post(String uri, HttpEntity encodedEntity) throws IOException, URISyntaxException {
    return post(uri, null, null, null, encodedEntity);
  }

  public Response post(String uri, HttpParams params, Map<String, String> headers, UsernamePasswordCredentials credentials)
      throws IOException, URISyntaxException {
    return post(uri, params, headers, credentials, null);
  }

  @SuppressWarnings("deprecation")
public Response post(String uri, HttpParams params, Map<String, String> headers, UsernamePasswordCredentials credentials, HttpEntity encodedEntity)
      throws IOException, URISyntaxException {
    HttpPost post = new HttpPost(uri);
    post.setHeader(HTTP.CONTENT_TYPE, FORM_URL_ENCODED_CONTENT_TYPE);
    // if (params != null) {
    // post.setParams(params);
    // }
    if (encodedEntity != null) {
      post.setEntity(encodedEntity);
    }
    // authentication
    HttpContext authContext = buildContext(uri, credentials);
    HttpResponse response = client.execute(post, authContext);

    // response
    if (response != null) {
      Response result = new Response(response);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.content = EntityUtils.toString(entity);
        entity.consumeContent();
      }
      return result;
    }
    return null;
  }

  /**
   * Whether a request has succedded, i.e.: 200 response code
   * 
   * @param method
   * @return
   */
  public boolean success(Response resp) {
    if (resp.getStatusLine().getStatusCode() >= 200 && resp.getStatusLine().getStatusCode() < 300) {
      return true;
    }
    return false;
  }

  public boolean verifyHost(HttpHost host) {
    if (host != null) {
      try {
        HttpHead head = new HttpHead(host.toURI());
        client.execute(host, head);
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        LOG.debug("Exception thrown", e);
      }
    }
    return false;
  }
}