package com.example.movies;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieDetails {

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("yearDate")
    private String yearDate;

    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("belongs_to_collection")
    private Object belongsToCollection;

    @SerializedName("budget")
    private int budget;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("id")
    private int id;

    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;

    @SerializedName("production_countries")
    private List<ProductionCountry> productionCountries;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("revenue")
    private int revenue;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spokenLanguages;

    @SerializedName("status")
    private String status;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("title")
    private String title;

    @SerializedName("video")
    private boolean video;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("audio_tracks")
    private String[] audioTracks; // Audio tracks

    @SerializedName("subtitles")
    private String[] subtitles; // Subtitles

    public char[] getRating() {
        String ratingString = String.valueOf(voteAverage);
        return ratingString.toCharArray();
    }

    public String getSubtitle() {
        if (subtitles == null || subtitles.length == 0) {
            return null; // Or return an empty string
        }

        StringBuilder subtitlesString = new StringBuilder();
        for (int i = 0; i < subtitles.length; i++) {
            subtitlesString.append(subtitles[i]);
            if (i < subtitles.length - 1) {
                subtitlesString.append(" | ");
            }
        }
        return subtitlesString.toString();
    }

    public String getYear() {
        if (releaseDate == null || releaseDate.isEmpty()) {
            // Return String
            return null;
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = inputFormat.parse(releaseDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
    // Nested classes for complex objects

    public static class Genre {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;

        // Getters and setters
        public int getId() { return id; }
        public String getName() { return name; }
    }

    public static class ProductionCompany {
        @SerializedName("id")
        private int id;
        @SerializedName("logo_path")
        private String logoPath;
        @SerializedName("name")
        private String name;
        @SerializedName("origin_country")
        private String originCountry;

        // Getters and setters
        public int getId() { return id; }
        public String getLogoPath() { return logoPath; }
        public String getName() { return name; }
        public String getOriginCountry() { return originCountry; }
    }

    public static class ProductionCountry {
        @SerializedName("iso_3166_1")
        private String iso31661;
        @SerializedName("name")
        private String name;

        // Getters and setters
        public String getIso31661() { return iso31661; }
        public String getName() { return name; }
    }

    public static class SpokenLanguage {
        @SerializedName("english_name")
        private String englishName;
        @SerializedName("iso_639_1")
        private String iso6391;
        @SerializedName("name")
        private String name;

        // Getters and setters
        public String getEnglishName() { return englishName; }
        public String getIso6391() { return iso6391; }
        public String getName() { return name; }
    }

    // Getters and setters for MovieDetails

    public boolean isAdult() { return adult; }
    public String getBackdropPath() { return backdropPath; }
    public Object getBelongsToCollection() { return belongsToCollection; }
    public int getBudget() { return budget; }
    public String getGenres() {
        if (genres == null || genres.isEmpty()) {
            return null; // Or return an empty string
        }

        StringBuilder genresString = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            genresString.append(genres.get(i).getName()); // Or genres.get(i) if it's List<String>
            if (i < genres.size() - 1) {
                genresString.append(" | ");
            }
        }
        return genresString.toString();
    }
    public String getHomepage() { return homepage; }
    public int getId() { return id; }
    public String getImdbId() { return imdbId; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getOriginalTitle() { return originalTitle; }
    public String getOverview() { return overview; }
    public double getPopularity() { return popularity; }
    public String getPosterPath() { return posterPath; }
    public List<ProductionCompany> getProductionCompanies() { return productionCompanies; }
    public List<ProductionCountry> getProductionCountries() { return productionCountries; }
    public String getReleaseDate() { return releaseDate; }
    public int getRevenue() { return revenue; }
    public int getRuntime() { return runtime; }
    public List<SpokenLanguage> getSpokenLanguages() { return spokenLanguages; }
    public String getStatus() { return status; }
    public String getTagline() { return tagline; }
    public String getTitle() { return title; }
    public boolean isVideo() { return video; }
    public double getVoteAverage() { return voteAverage; }
    public int getVoteCount() { return voteCount; }
    public String[] getAudioTracks() { return audioTracks; }
    public String[] getSubtitles() { return subtitles; }
}