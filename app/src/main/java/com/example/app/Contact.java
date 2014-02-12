package com.example.app;

public class Contact {
  public final long id;
  public final String lookupIndex;
  public final String name;

  public Contact(long id, String lookupIndex, String name) {
    this.id = id;
    this.lookupIndex = lookupIndex;
    this.name = name;
  }
}
