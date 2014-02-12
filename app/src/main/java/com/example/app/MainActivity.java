package com.example.app;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import rx.Observable;
import rx.Subscription;
import rx.content.Query;
import rx.content.ReactiveContentResolver;
import rx.content.ReactiveLoaderManager;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import static android.provider.ContactsContract.Contacts.CONTENT_ITEM_TYPE;
import static android.provider.ContactsContract.Contacts.CONTENT_URI;
import static android.provider.ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
import static android.provider.ContactsContract.Contacts.LOOKUP_KEY;
import static android.provider.ContactsContract.Contacts._ID;
import static android.provider.ContactsContract.Contacts.getLookupUri;

public class MainActivity extends ListActivity {

  // TODO this should be injected
  private static final ReactiveLoaderManager LOADER_MANAGER = new ReactiveLoaderManager();

  private final BetterListAdapter<Contact> adapter = new BetterListAdapter<Contact>() {
    @Override public long getItemId(int position) {
      return getItem(position).id;
    }

    @Override public View newView(int position, ViewGroup parent, LayoutInflater layoutInflater) {
      return layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override public void bindView(int position, View convertView) {
      ((TextView) convertView).setText(getItem(position).name);
    }
  };

  // If the query changes remove the previous loader from the manager and start a new one
  // You can easily @Icicle this and call buildUpon() if needed
  private final Query contactsQuery = new Query.Builder(CONTENT_URI).withProjection(
      new String[] { _ID, LOOKUP_KEY, DISPLAY_NAME_PRIMARY }).build();

  private Subscription subscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ListView listView = getListView();
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(editContact());

    // TODO ReactiveContentResolver should be injected too
    Observable<ContactList> dbQuery = new ReactiveContentResolver(getContentResolver())
        .query(contactsQuery).map(toContactList());
    subscription = LOADER_MANAGER.create(contactsQuery, dbQuery).subscribe(intoAdapter());
  }

  private AdapterView.OnItemClickListener editContact() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        Contact contact = adapter.getItem(position);
        Uri selectedContactUri = getLookupUri(contact.id, contact.lookupIndex);
        editIntent.setDataAndType(selectedContactUri, CONTENT_ITEM_TYPE);
        editIntent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(editIntent);
      }
    };
  }

  private Func1<? super Cursor, ContactList> toContactList() {
    return new Func1<Cursor, ContactList>() {
      @Override public ContactList call(Cursor cursor) {
        return new ContactList(cursor, new Func1<Cursor, Contact>() {
          @Override public Contact call(Cursor cursor) {
            return new Contact(cursor.getLong(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(LOOKUP_KEY)),
                cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY)));
          }
        });
      }
    };
  }

  private Action1<ContactList> intoAdapter() {
    return new Action1<ContactList>() {
      @Override public void call(ContactList contacts) {
        adapter.adapt(contacts);
      }
    };
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    subscription.unsubscribe();
    if (isFinishing()) {
      LOADER_MANAGER.remove(contactsQuery);
    }
  }
}
