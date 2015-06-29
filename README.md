# Volley Requests

Volley Requests is a library project which contains several request
implementations and utilities based on Android's Volley library.

[![Build Status](https://travis-ci.org/Monits/volley-requests.svg?branch=master)](https://travis-ci.org/Monits/volley-requests)
[![Coverage Status](https://coveralls.io/repos/Monits/volley-requests/badge.svg?branch=master)](https://coveralls.io/r/Monits/volley-requests?branch=master)

# Usage

## Using Gradle

You can just add the dependency by adding our maven repositories

```
repositories {
    maven {
        url 'http://nexus.monits.com/content/repositories/oss-releases'
    }
}
```

And then you can add the library as dependency

```
dependencies {
    compile 'com.monits:volley-requests:1.0.1'
}
```

For `SNAPSHOT` versions you can use the repository url
`http://nexus.monits.com/content/repositories/oss-snapshots`

The latest current snapshot version is `1.1.0-SNAPSHOT`

## Using Eclipse ADT

This is an Android library project, just like Volley.

* Clone this repository.
* Import it to your workspace.
* Add reference to your project's build path. Find how
[over here](http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject)

# Why use Volley Requests?

We love Volley and use it extensively. In doing so, we found several patterns
and types of requests coming up over and over again.

Among other things, we include:

### A family of requests that behave as expected out of the box

Volley is extremely aggresive in it's caching strategies. If you are using a
RESTFull API, you may have noticed in some scenarios it doesn't behave out of
the box as it would be expected.

For instance, assume we make the following request:
> GET /users/23

And got a cacheable entity which Volley will keep. We then want to update this
user by doing:
> POST /users/23

If you ever tried this, you may notice that the POST never hits the server.
Volley will hit the cache and return the cached entity. This is because Volley
isn't compliant with RFC 2616 (HTTP). We reached ficusk to address this issue,
and provided a patch, but he insisted on keeping the current implementation
(which just honors whatever the developer wants to do, and hits the cache by
default).

Therefore, we decided to code a different patch, by extending Volley's Request,
so we may keep using it as Volley keeps moving forward.

Every request in this library extends our base RFC-compliant request, to make
sure they behave as any expirienced web developer would expect. Always.

### Requests for commons tasks
* Uploading images? Check!
* posting JSONs? Check!
* Getting JSONs back? Check! We even use <code>GSON</code> to give back POJOs
directly!

### Null-safe ImageLoader
If you used <code>ImageLoader</code> with any resource that may or may not
exist, you will probably know <code>ImageLoader</code> don't like getting null
as the image url, and will throw a nasty <code>NullPointerException</code>.
This forces developers to check every time if the resource is null and then
either call <code>ImageLoader</code> or set the default placeholder manually.

We extended <code>ImageLoader</code> to make nulls display the default
placeholder image and dealt with this. No conditionals bloating your code,
just tell the <code>ImageLoader</code> what you want and he will get it.

### Complex retry logic support
Volley provides a very simple retry support, which extends timeout
exponentially, and just tries over and over until it succeeds or desists.

However, we found ourselves wanting to do more. For instance, when dealing with
sites that based authentication on cookies, when a request failed due to cookie
or session expiration, we wanted to attempt a re-login before retrying our
request, but this was not possible through the normal RetryPolicy
since we would get a 302 to the login page instead of a 401 / 403.

<code>RequeueAfterRequestDecorator</code> is a decorator that lets you do just
that. By being a decorator you can easily wrap this behaviour around any
existing request, making it versatile and easy to apply across any application.

### Rest Api

RestApi is based on [Restangular] (https://github.com/mgonto/restangular) that consists
in building a request by method chaining. For now it only works with <code>GsonRequest</code>,
this means that your response must be in *json* format.

#### How to use

On application startup you need to set the base url and the gson instance that you want
to use throughout your app.

    Rest.setBaseUrl("http://api.com:8080");
    Rest.setGson(gson);

You can also optionally set an interceptor that can modify your request before it`s executed.

    Rest.setInterceptor(new RequestInterceptor() {...}) //Set an interceptor

Now you are ready to create requests for your resources. Here is an example.

Suppose that you have users in your resources.

To get one user without an id (such as "yourself"), you need an url that looks something like this
http://api.com:8080/me. So as you have previously set the base url, you must add
the following:

    Rest.one("me")
        .get(User.class)
        .onSuccess(successListener)
        .onError(errorListener)
        .onCancel(cancelListener)
        .request();

To get one user by id, you simply add the id to <code> one() </code> method:

    Rest.one("user", id)
        .get(User.class)
        .onSuccess(successListener)
        .onError(errorListener)
        .onCancel(cancelListener)
        .request();

The url generated will be http://api.com:8080/user/id.

If you need to get many users, your json response is
a json array like:

    [
        {"firstName":"Jon", "lastName":"Snow"},
        {"firstName":"Petyr", "lastName":"Baelish"},
        {"firstName":"Ned","lastName":"Stark"}
    ]

If your response is an object that contains a json array like:

    {
        "response":
          [
                {"firstName":"Jon", "lastName":"Snow"},
                {"firstName":"Petyr", "lastName":"Baelish"},
                {"firstName":"Ned","lastName":"Stark"}
          ]
    }

then you must set the elements key with the name of the object key.
For this example you have to add:

    Rest.setElementsKey("response");

Now that you set the elements key you are ready to create the request.

    Rest.all("users")
        .get(User.class)
        .onSuccess(successListener)
        .onError(errorListener)
        .onCancel(cancelListener)
        .request();

Methods POST, PATCH, PUT have the same syntax as GET, but DELETE, HEAD,
TRACE and OPTIONS have no parameters. You can also use custom verbs with
<code>method(int method, Class<U> clazz)</code>. If your response is empty, you
can pass a <code>Void.class</code>zº as parameter.

    Rest.one("user")
        .post(Void.class)
        ...

If you want to add a query string to your request, add:

    Rest.one("user")
        .get(User.class)
        .query("id", "1")
        .query("timestamp", "1234")
        ...
        .request();

or

    final Map<String, String> map = ...

    map.put("id", "1");
    map.put("timestamp", "1234");

    Rest.one("user")
            .get(User.class)
            .query(map);
            ...
            .request();

If you want to add headers the syntax is the same as query string, but you
have to call <code>headers(...)</code> instead of <code>query(...)</code>


Here is a full example of a complex request:

    Rest.setBaseUrl("http://api.com:8080");
    Rest.setElementsKey("users");
    Rest.one("user", "12")
        .all("subjects")
        .get(Subject.class)
        .query("year", "2015")
        .query("school", "ITBA")
        .onSuccess(successListener)
        .onError(errorListener)
        .onCancel(cancelListener)
        .request();

The request url should look like this http://api.com:8080/user/12/subjects?year=2015&school=ITBA

### Request Loader
<code>RequestLoader</code> is a subclass of [Android v4 Loader] (http://developer.android.com/reference/android/support/v4/content/Loader.html)
that "binds" a <code>Request</code> to an Activity's lifecycle and refreshes data periodically,
similar to [AsyncTaskLoader] (http://developer.android.com/reference/android/support/v4/content/AsyncTaskLoader.html).

#### How to use
If you are familiarized with Android's Loaders, you shouldn't have trouble with this one. Aside
from some minor differences, its behaviour is basically the same. Here is what you need to know:

You can stop worrying about canceling your <code>Request</code>, as the <code>LoaderManager</code> is
attached to an Activity/Fragment's life cycle, <code>RequestLoader</code> automatically cancels any
pending <code>Requests</code> when your Activity/Fragment is no longer active. This will also avoid
potential problems, like unattached <code>Views</code> and null pointers.

As mentioned before, it refreshes your data periodically with a delay between reloads set by an
<code>updateThrottle</code> in milliseconds. Note that its default value is 0, meaning that you can
still use <code>RequestLoader</code> even if you don't need constant data refreshing, as it will
load only once.You can update its value whenever you want by calling <code>setUpdateThrottle(long delayMS)</code>.
Here is a brief example:

    /* MyClass is the data type that the loader will refresh */
    public class MyActivity extends Activity implements LoaderManager.LoaderCallbacks<MyClass> {

            private RequestQueue requestQueue;
            private Request request;

        	protected void onCreate(final Bundle savedInstanceState) {
        	    /* Set your activity's fields */
        	    getSupportLoaderManager().restartLoader(0, null, this);
        	}

        	public Loader<MyClass> onCreateLoader(int id, Bundle args) {
            		RequestLoader<MyClass> loader = new RequestLoader<MyClass>(this, request, requestQueue);
            		loader.setUpdateThrottle(10000l); //Set to refresh data every 10 seconds
            		return loader;
            }

            public void onLoadFinished(Loader<MyClass> loader, Cause data) {
            	/* What to do when loader finishes refreshing */
            }

            public void onLoaderReset(Loader<MyClass> loader) {
                /* What to do when LoaderManager resets your Loader*/
            }
    }

And that's it! Now you have a full functioning loader.


# Contributing
We encourage you to contribute to this project!

We are also looking forward to your bug reports, feature requests and questions
regarding android-volley.
	
# Copyright and License
Copyright 2010-2015 Monits.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this work except in compliance with the License. You may obtain a copy of the
License at:

http://www.apache.org/licenses/LICENSE-2.0
