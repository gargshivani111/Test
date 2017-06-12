package com.aem.core.sling.models;

/**
 * Created by LENOVO on 5/15/2017.
 */
public class TestModel {
  private String name;
  private String title;

  TestModel(String n,String t)
  {
    name=n;
    title = t;
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
