package org.jsoup.nodes;

import org.jsoup.internal.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.nodes.keystore.KeyStore;
import org.jsoup.nodes.keystore.KeyStoreFactory;

import java.io.IOException;

/**
 * A {@code <!DOCTYPE>} node.
 */
public class DocumentType extends LeafNode {

  private KeyStoreFactory keyStoreFactory = KeyStoreFactory.getInstance();
  private KeyStore keyStore;

  /**
   * Create a new doctype element.
   *
   * @param name     the doctype's name
   * @param publicId the doctype's public ID
   * @param systemId the doctype's system ID
   */
  public DocumentType(String name, String publicId, String systemId) {

    this.keyStore = keyStoreFactory.getKeyStore("default");

    Validate.notNull(name);
    Validate.notNull(publicId);
    Validate.notNull(systemId);

    this.keyStore.setName(name);
    this.keyStore.setPublicId(publicId);
    this.keyStore.setSystemId(systemId);

    attr(this.keyStore.getNameType(), this.keyStore.getName());
    attr(this.keyStore.getPublicIdType(), this.keyStore.getPublicId());
    if (has(keyStore.getPublicIdType())) {
      attr(keyStore.getPublicSystemKeyType(), this.keyStore.getPublicKey());
    }
    attr(this.keyStore.getSystemIdType(), this.keyStore.getSystemId());
  }

  /**
   * Create a new doctype element.
   *
   * @param name     the doctype's name
   * @param publicId the doctype's public ID
   * @param systemId the doctype's system ID
   * @param baseUri  unused
   * @deprecated
   */
  public DocumentType(String name, String publicId, String systemId, String baseUri) {

    this.keyStore = keyStoreFactory.getKeyStore("default");

    this.keyStore.setName(name);
    this.keyStore.setPublicId(publicId);
    this.keyStore.setSystemId(systemId);

    attr(this.keyStore.getNameType(), this.keyStore.getName());
    attr(this.keyStore.getPublicIdType(), this.keyStore.getPublicId());

    if (has(this.keyStore.getPublicIdType())) {
      attr(this.keyStore.getPublicSystemKeyType(), this.keyStore.getPublicKey());
    }
    attr(this.keyStore.getSystemIdType(), this.keyStore.getSystemId());
  }

  public void setKeyStore(String name) {
    this.keyStore = keyStoreFactory.getKeyStore(name);
  }

  /**
   * Create a new doctype element.
   *
   * @param name     the doctype's name
   * @param publicId the doctype's public ID
   * @param systemId the doctype's system ID
   * @param baseUri  unused
   * @deprecated
   */
  public DocumentType(String name, String pubSysKey, String publicId, String systemId, String baseUri) {
    this.keyStore = keyStoreFactory.getKeyStore("default");

    this.keyStore.setName(name);
    this.keyStore.setPublicSystemKey(pubSysKey);
    this.keyStore.setPublicId(publicId);
    this.keyStore.setSystemId(systemId);

    attr(this.keyStore.getNameType(), this.keyStore.getName());


    if (pubSysKey != null) {
      attr(this.keyStore.getPublicSystemKeyType(), this.keyStore.getPublicSystemKey());
    }
    attr(this.keyStore.getPublicIdType(), this.keyStore.getPublicId());
    attr(this.keyStore.getSystemIdType(), this.keyStore.getSystemId());
  }

  public void setPubSysKey(String value) {
    if (value != null)
      this.keyStore.setPublicSystemKey(value);
    attr(this.keyStore.getPublicSystemKeyType(), this.keyStore.getPublicSystemKey());
  }

  @Override
  public String nodeName() {
    return "#doctype";
  }

  @Override
  void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
    if (out.syntax() == Syntax.html && !has(this.keyStore.getPublicIdType()) && !has(this.keyStore.getSystemIdType())) {
      // looks like a html5 doctype, go lowercase for aesthetics
      accum.append("<!doctype");
    } else {
      accum.append("<!DOCTYPE");
    }
    if (has(this.keyStore.getNameType()))
      accum.append(" ").append(attr(this.keyStore.getNameType()));
    if (has(this.keyStore.getPublicSystemKeyType()))
      accum.append(" ").append(attr(this.keyStore.getPublicSystemKeyType()));
    if (has(this.keyStore.getPublicIdType()))
      accum.append(" \"").append(attr(this.keyStore.getPublicIdType())).append('"');
    if (has(this.keyStore.getSystemIdType()))
      accum.append(" \"").append(attr(this.keyStore.getSystemIdType())).append('"');
    accum.append('>');
  }

  @Override
  void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
  }

  public static String getSystemKeyType() {
    return "SYSTEM";
  }

  public static String getPublicKeyType() {
    return "PUBLIC";
  }

  private boolean has(final String attribute) {
    return !StringUtil.isBlank(attr(attribute));
  }
}
