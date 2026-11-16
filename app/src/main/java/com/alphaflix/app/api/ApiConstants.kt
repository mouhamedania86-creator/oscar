package com.alphaflix.app.api

object ApiConstants {
    const val BASE_URL = "https://nextdaywithme.shop/"
    const val IMAGE_BASE = "https://nextdaywithme.shop"

    // From captured traffic (OscarTV / Drama app)
    const val CERT_SHA256 = "6e2fcda8631eb49ebcba4ca8ef4c597abe84654c7d3e8096db32bdd21ecf763f"
    const val PKG_NAME = "com.drama.mp4"
    const val APP_VERSION = "18"

    // Static auth from captured bearer — refresh if expired
    const val BEARER_TOKEN = "2bfb366076401ffd433eb59a460e6d3b664ab4d93b6bca91004b77dcc8560eef"
    const val DEVICE_ID = "02ae8a20-2f87-3fb5-b44b-2d644948bba6"
    const val KEY_ID = "3c3c14e9512687444f42597ec01a091efe559abdd5d8f3071d2022c10270fbb7"

    // Endpoints (from live traffic)
    const val MOVIES = "api/movies/"
    const val SERIES = "api/series/"
    const val CHANNELS = "api/channels/"
    const val COLLECTIONS = "api/movies/collections.php"
    const val SERIES_SHOW = "api/series/show.php"
    const val V2_HOME = "api/v2/home.php"
    const val CONTENT_CATEGORIES = "api/content-categories.php"
}
