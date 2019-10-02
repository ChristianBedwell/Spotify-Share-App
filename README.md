# Spotify Share App
Having recently acquired the skill-set for Android development, I created the Spotify Share App to apply what I have learned and to create an app that would be useful to me, since I am a hard-core music enthusiast. Essentially, the Spotify Share App authenticates an Android user with the Spotify RESTful API, allows the user to query their top artists and tracks with parameters entered via TextViews/Spinners, and displays the results in a RecyclerView. Data that is loaded into the RecyclerView is then cached into a SQLlite database, to reduce the number of network requests that would be incurred if the app was to reload the data. The user may also click on each query result to view an artist/track in detail and share it with a friend.

![Authentication Demo](images/authentication_demo_optimized.gif?raw=true "Authenticating User with Spotify API")
![RecyclerView Demo](images/recyclerview_demo_optimized.gif?raw=true "Loading Data Into RecyclerView")
![Playback Demo](images/playback_demo_optimized.gif?raw=true "Track Player")

## Technologies Used
* **Gson** - deserializes JSON objects to Java objects
* **OAuth2** - authorization standard which provides secure delegated access to Spotify data
* **OkHttp** - handles HTTP network requests
* **Picasso** - downloads and caches remote images
* **Retrofit** - retrieves JSON data from the Spotify API 
* **Spotify App Remote Library** - manages audio playback via the Spotify Music app
* **Spotify Authentication Library** - handles authentication flow and Spotify API calls
* **Spotify RESTful API** - returns metadata about music artists, albums, and tracks from the Spotify Data Catalogue

## Prepare Your Environment

###  Register Your App
You will need to [register your application](https://beta.developer.spotify.com/documentation/general/guides/app-settings/#register-your-app) on [the Developer Dashboard](https://developer.spotify.com/dashboard/) and obtain a client ID. When you register your app you will also need to whitelist a redirect URI that the Spotify Accounts Service will use to callback to your app after authorization. You also should [add your package name and app fingerprint](https://beta.developer.spotify.com/documentation/android/quick-start/#register-application-fingerprints) as they’re used to verify the identity of your application.

### Install Spotify App
App Remote SDK requires the Spotify app to be installed on the device. Install the [latest version of Spotify](https://play.google.com/store/apps/details?id=com.spotify.music) from Google Play on the device you want to use for development. Run the Spotify app and login or sign up.
