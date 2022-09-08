package org.silverpeas.jcr.webdav;

/**
 * A class gathering the constants about the WebDAV protocol used in Silverpeas.
 * @author mmoquillon
 */
public final class WebDavProtocol {

  private WebDavProtocol() {
  }

  /**
   * The WebDAV scheme protocol used by Silverpeas to edit online a document.
   */
  public static final String WEBDAV_SCHEME = "spwebdav";

  /**
   * The secured WebDAV scheme protocol used by Silverpeas to edit online a document.
   */
  public static final String SECURED_WEBDAV_SCHEME = "spwebdavs";
}