package org.instedd.cdx.app;

import java.io.File;
import java.util.NavigableMap;

import org.instedd.sync4j.Settings;
import org.instedd.sync4j.settings.StringsSettingsStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;

class MapDBDataStore extends StringsSettingsStore {

  private NavigableMap<String, String> treeMap;
  private DB db;

  public MapDBDataStore(DB db, NavigableMap<String, String> treeMap) {
    this.db = db;
    this.treeMap = treeMap;
  }

  @Override
  public void setSettings(Settings settings) {
    super.setSettings(settings);
    db.commit();
  }

  protected String get(String key, String defaultValue) {
    return treeMap.get(key);
  }

  protected void set(String key, String value) {
    treeMap.put(key, value);
  }

  public static MapDBDataStore fromMapDB(String databaseFilename) {
    DB db = DBMaker.newFileDB(new File(databaseFilename)).closeOnJvmShutdown().make();
    NavigableMap<String, String> map = db.getTreeMap("settings");
    return new MapDBDataStore(db, map);
  }

}
