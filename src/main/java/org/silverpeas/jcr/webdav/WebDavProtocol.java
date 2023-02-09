package org.silverpeas.jcr.webdav;

/**
 * A class gathering the constants about the custom WebDAV protocol used in Silverpeas.
 * @author mmoquillon
 */
public final class WebDavProtocol {

  private WebDavProtocol() {
  }

  /**
   * The custom WebDAV scheme protocol used by Silverpeas to edit online a document.
   */
  public static final String WEBDAV_SCHEME = "spwebdav";

  /**
   * The custom secured WebDAV scheme protocol used by Silverpeas to edit online a document.
   */
  public static final String SECURED_WEBDAV_SCHEME = "spwebdavs";
}