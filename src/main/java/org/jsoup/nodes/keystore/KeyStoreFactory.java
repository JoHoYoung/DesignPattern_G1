package org.jsoup.nodes.keystore;

public class KeyStoreFactory {

  private volatile static KeyStoreFactory instance = null;

  private KeyStoreFactory(){};

  public static KeyStoreFactory getInstance(){
    if(instance == null){
      synchronized (KeyStoreFactory.class){
        if(instance == null){
          instance = new KeyStoreFactory();
        }
        return instance;
      }
    }
    return instance;
  }

  public KeyStore getKeyStore(String name){
    if(name == "default"){
      return new DefaultKeyStore();
    }
    return new DefaultKeyStore();
  }
}
