package ru.raspgleb.movierental;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class SaveFilmsAdapter extends ArrayAdapter<Film> {
    private Context context;
    Thread initThread, returnFilmThread;
    Runnable initRunnable, returnFilmRunnable;

    private ArrayList<Film> saveMovies;
    private String email = "";

    public SaveFilmsAdapter(Context context, ArrayList<Film> saveMovies, String email) {
        super(context, R.layout.card_film, saveMovies);
        this.context = context;
        this.saveMovies = saveMovies;
        this.email = email;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.save_card_film, parent, false);
        init();
        TextView name = view.findViewById(R.id.name_stage);
        name.setText(this.saveMovies.get(position).getFilm_name());
        TextView genre = view.findViewById(R.id.genre);
        genre.setText(this.saveMovies.get(position).getFilm_genre());
        TextView dateOut = view.findViewById(R.id.date);
        dateOut.setText(this.saveMovies.get(position).getDate_out());

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.window_return, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);
                final TextView movie_name = promptsView.findViewById(R.id.save_movie_name);
                movie_name.setText(saveMovies.get(position).getFilm_name());
                final TextView movie_genre = promptsView.findViewById(R.id.save_movie_genre);
                movie_genre.setText(saveMovies.get(position).getFilm_genre());

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("ДА",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        return_film(saveMovies.get(position).getFilm_name(), saveMovies.get(position).getFilm_genre(),email);
                                    }
                                })

                        .setNegativeButton("ОТМЕНА",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        boolean wantToCloseDialog = false;
                        return_film(saveMovies.get(position).getFilm_name(), saveMovies.get(position).getFilm_genre(),email);
                        wantToCloseDialog = true;
                        alertDialog.dismiss();
                    }
                });
                return false;
            }
        });
        return view;
    }

    /* Подключение к БД */
    private void init(){
        initRunnable = new Runnable() {
            @Override
            public void run() {
                Database.connection("192.168.92.184","5432");
            }
        };
        initThread = new Thread(initRunnable);
        initThread.start();
    }

    /* Возврат арендованного фильма */
    private void return_film(String name_film, String genre_film, String email){
        returnFilmRunnable = new Runnable() {
            @Override
            public void run() {
                Database.returnFilm(name_film,genre_film,email);
            }
        };
        returnFilmThread = new Thread(returnFilmRunnable);
        returnFilmThread.start();
        try{
            returnFilmThread.join();
        }catch (InterruptedException eeee4){
            System.out.println(eeee4);
        }
    }
}
