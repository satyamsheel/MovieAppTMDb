in class App add api_key at "YOUR_KEY_HERE".
get your key at https://developer.themoviedb.org/reference/intro/getting-started


private fun initTMDbSdk(){
        val configuration = ApiKeyConfiguration()
        configuration.setApiKey("YOUR_KEY_HERE")
        TMDbKey.instance.init(applicationContext, configuration)
    }
