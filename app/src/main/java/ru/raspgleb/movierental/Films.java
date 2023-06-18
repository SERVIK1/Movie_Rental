package ru.raspgleb.movierental;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class Films extends AppCompatActivity {
    Context context = this;
    Thread initThread, getSaveFilmThread;
    Runnable initRunnable, getSaveFilmRunnable;

    ArrayList<Film> all_movies = new ArrayList<>();
    ArrayList<Film> save_movies = new ArrayList<>();
    ListView all_films, my_films;
    String mailUser;

    FilmsAdapter allFilmsAdapter;
    SaveFilmsAdapter saveFilmsAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exit,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action1:
                Database.closeDB();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.window_films);

        TabHost tabHost = findViewById(R.id.tabHost);
        Intent getMailUser = getIntent();
        mailUser = getMailUser.getStringExtra("email");
        setTitle(mailUser);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.all_films);
        tabSpec.setIndicator("ФИЛЬМЫ");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.list_my_films);
        tabSpec.setIndicator("МОИ ФИЛЬМЫ");
        tabHost.addTab(tabSpec);

        /* При переключении вкладок идёт обоваление сохранённых фильмов */
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                get_save_film();
                saveFilmsAdapter.notifyDataSetChanged();
            }
        });

        all_films = findViewById(R.id.list_films);
        my_films = findViewById(R.id.list_my_films);
        init();
        allFilmsAdapter = new FilmsAdapter(context, all_movies, mailUser);
        all_films.setAdapter(allFilmsAdapter);
        saveFilmsAdapter = new SaveFilmsAdapter(context,save_movies, mailUser);
        my_films.setAdapter(saveFilmsAdapter);
    }


    /* Подключение к БД */
    private void init(){
        initRunnable = new Runnable() {
            @Override
            public void run() {
                Database.connection("192.168.92.184","5432");
                getAllFilmsDB();
                getSaveFilmsDB();
            }
        };
        initThread = new Thread(initRunnable);
        initThread.start();
        try{
            initThread.join();
        }catch (InterruptedException eee){
            System.out.println(eee);
        }
    }

    /* Получение сохранённых фильмов из БД */
    private void get_save_film(){
        getSaveFilmRunnable = new Runnable() {
            @Override
            public void run() {
                getSaveFilmsDB();
            }
        };
        getSaveFilmThread = new Thread(getSaveFilmRunnable);
        getSaveFilmThread.start();
        try{
            getSaveFilmThread.join();
        }catch (InterruptedException eeee){
            System.out.println(eeee);
        }
    }

    /* Получение всех фильмов из БД */
    private void getAllFilmsDB(){
        try{
            for (HashMap.Entry<String, String> entry : Database.readFilms().entrySet()) {
                String name_film = entry.getKey();
                String genre_film = entry.getValue();
                all_movies.add(new Film(name_film,genre_film));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* Получение сохранённых фильмов из БД */
    private void getSaveFilmsDB(){
        try{
            save_movies.clear();
            for (HashMap.Entry<String, String> entry : Database.read_saveFilms(mailUser).entrySet()) {
                String name_film = entry.getKey();
                /* "!!!!" - выступают в роли разделителя жанра фильма и даты возврата в одной строке */
                String genre_film = entry.getValue().split("!!!!")[0];
                String date_out = entry.getValue().split("!!!!")[1];
                save_movies.add(new Film(name_film,genre_film,date_out));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
