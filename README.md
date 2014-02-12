ReactiveContent
===============

Target
--------
- From ContentProvider monitoring to network calls, everything should dance to the rythm of `Observable<T>`

Current state
--------
- ContentProvider is not hard to wrap.
- Rest libraries can support RxJava out of the box (Retrofit)
- A state manager that can provide an easy replacement for Loaders is needed. On the plus side, `Observables` can be composed, so you can define an `Observable<T>` as the task and return a wrapped `Subject<T>` that takes care of caches etc.

Todo
--------
- Example with Retrofit calls to showcase the equivalent of an `AsynTaskLoader` a.k.a. a non-infinite `Observable`.



