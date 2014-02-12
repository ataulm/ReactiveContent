package com.example.app;

import android.database.Cursor;
import rx.content.LazyList;
import rx.util.functions.Func1;

public class ContactList extends LazyList<Contact> {
  public ContactList(Cursor cursor, Func1<Cursor, Contact> func1) {
    super(cursor, func1);
  }
}
