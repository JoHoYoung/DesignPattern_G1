package org.jsoup.nodes.keystore;

public class DefaultKeyStore extends KeyStore {

  private String PUBLIC_KEY;
  private String SYSTEM_KEY;
  private String NAME;
  private String PUB_SYS_KEY;
  private String PUBLIC_ID;
  private String SYSTEM_ID;

  public DefaultKeyStore(){
    super();
    this.PUBLIC_KEY = "PUBLIC";
    this.SYSTEM_KEY = "SYSTEM";
    this.NAME = "name";
    this.PUB_SYS_KEY = "pubSysKey";
    this.PUBLIC_ID = "publicId";
    this.SYSTEM_ID = "systemId";
  }

  @Override
  public String getPublicKey() {
    return this.PUBLIC_KEY;
  }

  @Override
  public String getSystemKey() {
    return this.SYSTEM_KEY;
  }
  @Override
  public String getName() {
    return this.NAME;
  }
  @Override
  public String getPublicSystemKey() {
    return this.PUB_SYS_KEY;
  }

  @Override
  public String getPublicId() {
    return this.PUBLIC_ID;
  }

  @Override
  public String getSystemId() {
    return this.SYSTEM_ID;
  }

  @Override
  public void setPublicKey(String value) {
    this.PUBLIC_KEY = value;
  }

  @Override
  public void setSystemKey(String value) {
    this.SYSTEM_KEY = value;
  }
  @Override
  public void setName(String value) {
    this.NAME = value;
  }
  @Override
  public void setPublicSystemKey(String value) {
    this.PUB_SYS_KEY = value;
  }

  @Override
  public void setPublicId(String value) {
    this.PUBLIC_ID = value;
  }

  @Override
  public void setSystemId(String value) {
    this.SYSTEM_ID = value;
  }

}
