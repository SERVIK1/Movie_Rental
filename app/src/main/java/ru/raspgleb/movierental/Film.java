package ru.raspgleb.movierental;

public class Film {
    private String film_name;
    private String film_genre;
    private String date_out;


    Film(String name, String genre) {
        this.film_name = name;
        this.film_genre = genre;

    }

    Film(String name, String genre,String date) {
        this.film_name = name;
        this.film_genre = genre;
        this.date_out = date;

    }


    public void setFilm_genre(String film_genre) {
        this.film_genre = film_genre;
    }

    public void setFilm_name(String film_name) {
        this.film_name = film_name;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    public String getDate_out() {
        return date_out;
    }

    public String getFilm_name() {
        return film_name;
    }

    public String getFilm_genre() {
        return film_genre;
    }
}


