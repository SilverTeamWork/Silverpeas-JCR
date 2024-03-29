/*
 * Copyright (C) 2000 - 2022 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.jcr.util;

import javax.jcr.Property;

/**
 * JCR property specific to Silverpeas to set also on nodes information related to the contributions
 * stored in Silverpeas.
 * @author Emmanuel Hugonnet
 */
@SuppressWarnings("unused")
public interface SilverpeasProperty extends Property {


  /**
   * Prefix for Silverpeas namespace.
   */
  String SILVERPEAS_PREFIX = "slv";
  /**
   * Silverpeas theme qname
   */
  String SLV_NODE = "slv:theme";
  /**
   * Silverpeas translation for theme qname
   */
  String SLV_I18N_NODE = "slv:i18nTheme";
  /**
   * Silverpeas versioning document node qname.
   */
  String SLV_SIMPLE_DOCUMENT = "slv:simpleDocument";
  /**
   * Silverpeas versioning document version node qname.
   */
  String SLV_SIMPLE_ATTACHMENT = "slv:simpleAttachment";
  /**
   * Silverpeas translation for link qname
   */
  String SLV_LINK = "slv:link";
  /**
   * Silverpeas kmelia component node.
   */
  String SLV_KMELIA = "slv:kmelia";
  /**
   * Silverpeas publication node qname.
   */
  String SLV_PUBLICATION = "slv:publication";
  /**
   * Silverpeas publication translation node qname.
   */
  String SLV_I18N_PUBLICATION = "slv:i18nPublication";
  /**
   * Silverpeas versioning document node qname.
   */
  String SLV_DOCUMENT = "slv:document";
  /**
   * Silverpeas versioning document attachment node qname.
   */
  String SLV_DOCUMENT_ATTACHMENT = "slv:documentAttachment";
  /**
   * Silverpeas attachment node qname.
   */
  String SLV_ATTACHMENT = "slv:attachment";
  /**
   * Silverpeas attachment translation node qname.
   */
  String SLV_I18N_ATTACHMENT = "slv:i18nAttachment";
  /**
   * Silverpeas Blog component node.
   */
  String SLV_BLOG = "slv:blog";
  /**
   * Silverpeas QuickInfo component node.
   */
  String SLV_QUICK_INFO = "slv:quickInfo";
  /**
   * Silverpeas News edito component node.
   */
  String SLV_NEWS_EDITO = "slv:newsEdito";
  /**
   * Silverpeas Forum component node.
   */
  String SLV_FORUM = "slv:forum";
  /**
   * Silverpeas Gallery component node.
   */
  String SLV_GALLERY = "slv:gallery";
  /**
   * Silverpeas Question/Reply component node.
   */
  String SLV_QUESTION_REPLY = "slv:questionReply";
  /**
   * Silverpeas WebSites component node.
   */
  String SLV_WEB_SITES = "slv:webSites";
  /**
   * Silverpeas Record (FormTemplate) component node.
   */
  String SLV_XML_FORM = "slv:xmlForm";
  /**
   * Silverpeas Data Record (FormTemplate) component node.
   */
  String SLV_XML_FORM_DATA = "slv:xmlFormData";
  /**
   * Silverpeas Mixin to add an owner to the node.
   */
  String SLV_OWNABLE_MIXIN = "slv:ownable";

  /**
   * Silverpeas Mixin to add a comment to the node.
   */
  String SLV_COMMENTABLE_MIXIN = "slv:commentable";

  /**
   * Silverpeas Mixin to add download data to the node.
   */
  String SLV_DOWNLOADABLE_MIXIN = "slv:downloadable";

  /**
   * Silverpeas Mixin to add viewable data to the node.
   */
  String SLV_VIEWABLE_MIXIN = "slv:viewable";

  /**
   * Silverpeas Mixin to add viewable data to the node.
   */
  String SLV_EDITABLE_MIXIN = "slv:editable";

  /**
   * Translation node 's name prefix. A translation's name should be TRANSLATION_NAME_PREFIX+ lang.
   */
  String TRANSLATION_NAME_PREFIX = "traduction_";

  /**
   * Root node for XPath Queries
   */
  String SLV_PROPERTY_NAME = "slv:name";
  String SLV_PROPERTY_DESCRIPTION = "slv:description";
  String SLV_PROPERTY_CREATION_DATE = "slv:creationDate";
  String SLV_PROPERTY_AUTHOR = "slv:author";
  String SLV_PROPERTY_ORDER = "slv:order";
  String SLV_PROPERTY_MODEL = "slv:modelid";
  String SLV_PROPERTY_LANG = "slv:lang";
  String SLV_PROPERTY_RIGHTS = "slv:rightsdependson";
  String SLV_PROPERTY_TYPE = "slv:type";
  String SLV_PROPERTY_STATUS = "slv:status";
  String SLV_PROPERTY_LINKS = "slv:links";
  String SLV_PROPERTY_KEYWORDS = "slv:keywords";
  String SLV_PROPERTY_CREATOR = "slv:creator";
  String SLV_PROPERTY_UPDATER = "slv:updatedBy";
  String SLV_PROPERTY_UPDATE_DATE = "slv:updateDate";
  String SLV_PROPERTY_IMPORTANCE = "slv:importance";
  String SLV_PROPERTY_CONTENT = "slv:content";
  String SLV_PROPERTY_VERSION = "slv:version";
  String SLV_PROPERTY_START_DATE = "slv:startDate";
  String SLV_PROPERTY_END_DATE = "slv:endDate";
  String SLV_PROPERTY_ICON = "slv:icon";
  String SLV_PROPERTY_VALIDATORS = "slv:validators";
  String SLV_PROPERTY_VALIDATOR = "slv:validatedBy";
  String SLV_PROPERTY_VALIDATION_DATE = "slv:validationDate";
  String SLV_PROPERTY_CLONE = "slv:clone";
  String SLV_PROPERTY_PUBLICATION = "slv:publication";
  String SLV_PROPERTY_DELETED = "slv:deleted";
  String SLV_PROPERTY_SIZE = "slv:size";
  String SLV_PROPERTY_TITLE = "slv:title";
  String SLV_PROPERTY_WORKER = "slv:worker";
  String SLV_PROPERTY_CONTEXT = "slv:context";
  String SLV_PROPERTY_EXPIRY_DATE = "slv:expiryDate";
  String SLV_PROPERTY_ALERT_DATE = "slv:alertDate";
  String SLV_PROPERTY_RESERVATION_DATE = "slv:reservationDate";
  String SLV_PROPERTY_FOREIGN_KEY = "slv:foreignKey";
  String SLV_PROPERTY_WORKING_COPY = "slv:workingCopy";
  String SLV_PROPERTY_INFO = "slv:info";
  String SLV_PROPERTY_OWNER = "slv:owner";
  String SLV_PROPERTY_WORKLIST_ORDER = "slv:workListOrder";
  String SLV_PROPERTY_WORKLIST_TYPE = "slv:workListType";
  String SLV_PROPERTY_MAJOR = "slv:major";
  String SLV_PROPERTY_MINOR = "slv:minor";
  String SLV_PROPERTY_DATA = "slv:data";
  String SLV_PROPERTY_TEMPLATE = "slv:template";
  String SLV_PROPERTY_INSTANCEID = "slv:instanceId";
  String SLV_PROPERTY_OLD_ID = "slv:oldSilverpeasId";
  String SLV_PROPERTY_VERSIONED = "slv:versioned";
  String SLV_PROPERTY_XMLFORM_ID = "slv:xmlFormId";
  String SLV_PROPERTY_COMMENT = "slv:comment";
  String SLV_PROPERTY_FORBIDDEN_DOWNLOAD_FOR_ROLES = "slv:forbiddenDownloadForRoles";
  String SLV_PROPERTY_DISPLAYABLE_AS_CONTENT = "slv:displayableAsContent";
  String SLV_PROPERTY_EDITABLE_SIMULTANEOUSLY = "slv:editableSimultaneously";
}
