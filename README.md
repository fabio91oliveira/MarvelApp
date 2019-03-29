# Marvel App

This is an application written in Kotlin that consumes [Marvel Api](http://developer.marvel.com/).  The app **list**, **show details**, **search** and **favorite** the heroes listed.


## Notes
The application includes two Activities, **CharactersList** and **Character Details** Activities. The Character Activity is consist of two Fragments, **RegularList** and **FavoriteList** Fragments and Search.

All lifecycle and network behaviors of the application are implemented according to a easy UI experiencie with native libs of Android.
Some simple Test Cases was designed to test application UI functionality and core classes using jUnit and Mockito for Unit Tests.

## Architecture

The Application implemented and structured based on the MVVM with Android Architecture Components behaviors and pattern.

Whole application functionality is implemented with **AndroidX Library** and using Kotlin.

The **view** (CharactersList), contain two fragments. **RegularList** and **FavoriteList**, they are using a **shared view model** injected by **Koin**.

The **view model** are responsible to act as the middleman between views and models. They retrieve data from the API or Database with **Room** and returns it formatted to the view. It also handle what should happens when user interacts with the view.

The **models**, would only be the gateway to the service domain layer like the responses from the API, doing the parse to load at the application. In this case, it provides the data needed to be displayed in the view from Network.

The **API** calls are managed by **Retrofit** and **OkHttp** as its httpclient with the personal interceptor. It also shows decent logs while the application is running in Debug mode.

The **Repository** layer is the middle to call API and Database.

The **database** calls and management are handled by **Room**.
Layers communications are managed by  **RxJava**  and  **RxAndroid**.
Dependency Injections are being managed by  **Koin**.

## Libs
* AndroidX Libraries
* Architecture Components Library ( Lifecycle with LiveData and ViewModels )
* Room
* Retrofit
* OkHttp
* RxJava
* RxAndroid
* Glide
* Koin

## Libs for Test
* Junit
* MockServer
* RequestMatcher
* Mockito
* Espresso
* KoinTest
