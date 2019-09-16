# Spotify Share App
Having recently acquired the skill-set for Android development, I created the Spotify Share App to apply what I have learned and to create an app that would be useful to me, since I am a hard-core music enthusiast. Essentially, the Spotify Share App authenticates an Android user with the Spotify RESTful API, allows the user to query their top artists and tracks with parameters entered via TextViews/Spinners, and displays the results in a RecyclerView. Data that is loaded into the RecyclerView is then cached into a SQLlite database, to reduce the number of network requests that would be incurred if the app was to reload the data. The user may also click on each query result to view an artist/track in detail and share it with a friend.

![Authentication Demo](app/src/main/res/drawable/authentication_demo_optimized.gif?raw=true "Authenticating User with Spotify API")
![RecyclerView Demo](app/src/main/res/drawable/recyclerview_demo_optimized.gif?raw=true "Loading Data Into RecyclerView")
![Playback Demo](app/src/main/res/drawable/playback_demo_optimized.gif?raw=true "Track Player")

## Technologies Used
* **Spotify Authentication Library** - handles authentication flow and Spotify API calls
* **Spotify App Remote Library** - manages audio playback via the Spotify Music app
* **Gson** - deserializes JSON objects to Java objects
* **OAuth2** - authorization standard which provides secure delegated access to Spotify data
* **Picasso** - downloads and caches remote images
* **Retrofit** - retrieves JSON data from the Spotify API 
* **OkHttp** - handles HTTP network requests
* **Spotify RESTful API** - returns metadata about music artists, albums, and tracks from the Spotify Data Catalogue

