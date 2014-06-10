# Volley Requests

Volley Requests is a library project which contains several request
implementations and utilities based on Android's Volley library.

# Usage

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

### Rest support

Create the corresponding <code>GsonRequest</code> for a particular resource. For any
resource you can get the collection resource executing getAll(...) method and
this will create the resource for you.
Available request methods are, <code>Method.GET, Method.PUT, Method.POST</code> 
and <code>Method.DELETE</code>

#### How to use:

<code> RestSupport mRestSupport = new RestSupport("http://www.example.com/user/:userId"
, new Gson()); </code>

> If you want to get the user with id 12, add:

<code>
Map<String, String> resourceParams = new HashMap<String, String>();	
resourceParams.put("userId", "12");

GsonRequest<YouObjectType> mGsonRequest = mRestSupport.getObject(resourceParams, 
listener, errListener);
</code>

> Likewise, if you want to get all available users, add:

<code>GsonRequest<List<YouObjectType>> mGsonRequest = mRestSupport.getAll(resourceParams, 
listener, errListener);</code>

In this case, the generated url, by <code>RestSupport</code>, is "http://www.example.com/user/"

# Contributing
We encourage you to contribute to this project!

We are also looking forward to your bug reports, feature requests and questions
regarding android-volley.

# Copyright and License
Copyright 2010-2014 Monits.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this work except in compliance with the License. You may obtain a copy of the
License at:

http://www.apache.org/licenses/LICENSE-2.0
