package com.uimainon.go4lunch.controllers.fragments;

import android.content.SearchRecentSuggestionsProvider;

public class PlacesSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public PlacesSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
