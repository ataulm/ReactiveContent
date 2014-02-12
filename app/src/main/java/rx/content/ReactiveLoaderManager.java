package rx.content;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ReactiveLoaderManager {

  private final Map<Object, Object> cachedResults = new HashMap<>();
  private final Map<Object, PublishSubject<?>> cachedRequests = new HashMap<>();

  public <K, V> Observable<V> create(final K key, Observable<V> task) {
    PublishSubject<V> cachedRequest = typeSafeRequest(key);
    if (cachedRequest != null) {
      V cachedResult = typeSafeResult(key);
      if (cachedResult != null) {
        return cachedRequest.startWith(cachedResult);
      }
      return cachedRequest;
    }

    cachedRequest = PublishSubject.create();
    cachedRequests.put(key, cachedRequest);

    cachedRequest.subscribe(new Observer<V>() {
      @Override public void onCompleted() {
        cachedRequests.remove(key);
      }

      @Override public void onError(Throwable throwable) {
        cachedRequests.remove(key);
      }

      @Override public void onNext(V v) {
        cachedResults.put(key, v);
      }
    });

    task.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cachedRequest);

    return cachedRequest;
  }

  public <K> void remove(K key) {
    cachedRequests.remove(key);
    removeCachedResultSafely(key);
  }

  @SuppressWarnings("unchecked") private <V, K> V typeSafeResult(K key) {
    return (V) cachedResults.get(key);
  }

  @SuppressWarnings("unchecked") private <V, K> PublishSubject<V> typeSafeRequest(K key) {
    return (PublishSubject<V>) cachedRequests.get(key);
  }

  private <K> void removeCachedResultSafely(K key) {
    Object oldValue = cachedResults.get(key);
    if (oldValue != null) {
      cachedResults.remove(key);
    }
    if (oldValue instanceof Closeable) {
      try {
        ((Closeable) oldValue).close();
      } catch (IOException ignored) {
      }
    }
  }
}
