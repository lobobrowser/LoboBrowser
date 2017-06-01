/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Mar 14, 2005
 */
package com.lobobrowser.protocol.cobra;

import com.lobobrowser.LoboBrowser;
import org.cobraparser.util.Strings;
import org.cobraparser.util.Timing;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author J. H. S.
 */
public class CobraURLConnection extends URLConnection {
  public CobraURLConnection(final URL url) {
    super(url);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.net.URLConnection#connect()
   */
  @Override
  public void connect() throws IOException {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.net.URLConnection#getContentLength()
   */
  @Override
  public int getContentLength() {
    return -1;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.net.URLConnection#getContentType()
   */
  @Override
  public String getContentType() {
    return "text/html";
  }

  /*
   * (non-Javadoc)
   *
   * @see java.net.URLConnection#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.getURLText(this.getURL()).getBytes("UTF-8"));
  }

  private String getURLText(final URL url) {
    final String path = url.getPath();
    if ("blank".equalsIgnoreCase(path)) {
      return "";
    } else if ("java-properties".equals(path)) {
      return getSystemProperties();
    } else if ("welcome".equals(path)) {
      return getWelcomeMessage();
    } else {
      return "<p>Unknown cobra path: " + path + "</p>" +
          "<h3>Known paths are:</h3>" +
          "<ul>" +
          "<li><a href='cobra:blank'>cobra:blank</a></li>" +
          "<li><a href='cobra:welcome'>cobra:welcome</a></li>" +
          "<li><a href='about:java-properties'>cobra:java-properties</a></li>" +
          "</ul>";
    }
  }

  private static String getWelcomeMessage() {
    final Properties relProps = LoboBrowser.getInstance().relProps;

    return
        "<div style='max-width:900px;margin:0 auto;text-align:center;'>"
        +
        "<h1>Welcome to gngr</h1>" +
        "<p>Version: " + relProps.getProperty(LoboBrowser.RELEASE_VERSION_STRING) + "<br/>" +
        "Published on: " + relProps.getProperty(LoboBrowser.RELEASE_VERSION_RELEASE_DATE) + "</p>" +
        "<p><b><a href='https://gngr.info'>gngr</a></b> is a browser that cares deeply about privacy.</p>"
        +
        "<p>It is currently a proof-of-concept, and not very stable or secure.</p>"
        +
        "<div style='text-align:left;padding:1em;margin:1em auto; width:50em;background:#ffd;border:1px solid #bbb'>"
        +
        "<p>We recommend that you use this version for casual browsing only and follow the project's <a href='https://blog.gngr.info'>blog</a> to stay abreast of changes.</p>"
        +
        "<p>Other ways of reaching us:</p>" +
        "<ul>" +
          "<li style='margin:1em 0'><a href='https://github.com/uprootlabs/gngr'>Source code and issues</a> on GitHub</li>" +
          "<li style='margin:1em 0'>#gngr and #gngr-dev on Freenode IRC</li>" +
          "<li style='margin:1em 0'><a href='https://reddit.com/r/gngr'>/r/gngr</a> on Reddit</li>" +
          "<li style='margin:1em 0'><a href='https://twitter.com/gngrInfo'>@gngrInfo</a> on Twitter</li>" +
        "</ul>" +
        "</div>"
        +
        "<div style='padding:1em;border:1px solid #bbb;background:#efe;width:50em;margin:0 auto'>" +
        "<p><span style='border-bottom:2px solid red; font-weight:bold'>Tip:</span> Checkout the Request Manager button on the right of the URL bar. " +
        "The Request Manager allows you to control which URL requests are allowed on a given webpage.</p>" +
        "<p>By default, cookies, scripts and frames are disabled on all websites. " +
        "You can change these rules as per your preferences.</p>" +
        "<p>Note: the button is disabled on this page since it is an internal page and there are no external requests.</p>" +
        "<div style='position:fixed; right: 0; top: 0; background: #efe; color:#595; padding: 0.33em 1em; border:2px dotted #9f9; border-top:0'>" +
          "<p style='margin:0; font-weight:bold; text-align:right; font-size:120% '>&#11014;</p>" +
          "<p style='margin:0; font-weight:bold'>Request Manager</p>" +
          "<p style='margin:0; font-size: 90%'>(read the tip below)</p>" +
        "</div>" +
        "</div>" +
        "</div>";
  }

  private static String getSystemProperties() {
    final StringWriter swriter = new StringWriter();
    final PrintWriter writer = new PrintWriter(swriter);
    writer.println("<html>");
    writer.println("<head><title>Java Properties</title></head>");
    writer.println("<body>");
    writer.println("<pre>");
    final Properties properties = System.getProperties();
    properties.list(writer);
    writer.println("</pre>");
    writer.println("</body>");
    writer.println("</html>");
    writer.flush();
    return swriter.toString();
  }

}
