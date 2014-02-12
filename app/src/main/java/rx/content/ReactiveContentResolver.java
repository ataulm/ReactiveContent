package rx.content;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class ReactiveContentResolver {

  private final ContentResolver contentResolver;

  public ReactiveContentResolver(ContentResolver contentResolver) {
    this.contentResolver = contentResolver;
  }

  public Observable<Cursor> query(final Query query) {
    return Observable.create(new Observable.OnSubscribeFunc<Cursor>() {
      @Override public Subscription onSubscribe(final Observer<? super Cursor> observer) {
        final ContentObserver contentObserver = new ContentObserver(null) {
          @Override public void onChange(boolean selfChange) {
            tryFetchCursor(query, observer);
          }
        };

        SystemClock.sleep(2000);

        subscribeToContentUpdates(query.uri, contentObserver);
        tryFetchCursor(query, observer);

        return new Subscription() {
          @Override public void unsubscribe() {
            unsubscribeFromContentUpdates(contentObserver);
          }
        };
      }
    });
  }

  private void tryFetchCursor(Query query, Observer<? super Cursor> observer) {
    try {
      observer.onNext(fetchCursor(query));
    } catch (Exception e) {
      observer.onError(e);
    }
  }

  private Cursor fetchCursor(Query query) {
    return contentResolver.query(query.uri, query.projection, query.selection, query.selectionArgs,
        query.sortOrder);
  }

  private void subscribeToContentUpdates(Uri uri, ContentObserver contentObserver) {
    contentResolver.registerContentObserver(uri, true, contentObserver);
  }

  private void unsubscribeFromContentUpdates(ContentObserver contentObserver) {
    contentResolver.unregisterContentObserver(contentObserver);
  }

  // TODO insert

  // TODO update

  // TODO delete
}
