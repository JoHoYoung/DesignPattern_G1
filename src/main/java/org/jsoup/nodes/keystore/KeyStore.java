package org.jsoup.nodes.keystore;

public abstract class KeyStore {
  private String PUBLIC_KEY_TYPE;
  private String SYSTEM_KEY_TYPE;
  private String NAME_TYPE;
  private String PUB_SYS_KEY_TYPE;
  private String PUBLIC_ID_TYPE;
  private String SYSTEM_ID_TYPE;

  public KeyStore(){
    this.PUBLIC_KEY_TYPE = "PUBLIC";
    this.SYSTEM_KEY_TYPE = "SYSTEM";
    this.NAME_TYPE = "name";
    this.PUB_SYS_KEY_TYPE = "pubSysKey";
    this.PUBLIC_ID_TYPE = "publicId";
    this.SYSTEM_ID_TYPE= "systemId";
  }

  public String getPublicKeyType() {
    return this.PUBLIC_KEY_TYPE;
  }
  public String getSystemKeyType() {
    return this.SYSTEM_KEY_TYPE;
  }
  public String getNameType() {
    return this.NAME_TYPE;
  }
  public String getPublicSystemKeyType() {
    return this.PUB_SYS_KEY_TYPE;
  }
  public String getPublicIdType() {
    return this.PUBLIC_ID_TYPE;
  }
  public String getSystemIdType() {
    return this.SYSTEM_ID_TYPE;
  }

  abstract public String getPublicKey();
  abstract public String getSystemKey();
  abstract public String getName();
  abstract public String getPublicSystemKey();
  abstract public String getPublicId();
  abstract public String getSystemId();

  abstract public void setPublicKey(String value);
  abstract public void setSystemKey(String value);
  abstract public void setName(String value);
  abstract public void setPublicSystemKey(String value);
  abstract public void setPublicId(String value);
  abstract public void setSystemId(String value);

}
