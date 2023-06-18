package ru.raspgleb.movierental;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FilmsAdapter extends ArrayAdapter<Film> {
    private Context context;
    Thread initThread, takeFilmThread, checkFilmThread;
    Runnable initRunnable, takeFilmRunnable, checkFilmRunnable;

    private ArrayList<Film> allMovies;
    private String email = "";
    private String userControlMail;



    public FilmsAdapter(Context context, ArrayList<Film> movies, String email) {
        super(context,R.layout.card_film,movies);
        this.context = context;
        this.allMovies = movies;
        this.email = email;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.card_film, parent, false);
        init();

        TextView name = view.findViewById(R.id.name_stage);
        name.setText(this.allMovies.get(position).getFilm_name());
        TextView genre = view.findViewById(R.id.genre);
        genre.setText(this.allMovies.get(position).getFilm_genre());
        TextView money = view.findViewById(R.id.num_stage);
        money.setText("1$");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context,
                        "Удерживайте на нужном фильме", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.window_pay, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final TextView title = promptsView.findViewById(R.id.tv);
                title.setText("Аренда фильма");
                final TextView movie_name = promptsView.findViewById(R.id.movie_name);
                movie_name.setText(allMovies.get(position).getFilm_name());
                final TextView movie_genre = promptsView.findViewById(R.id.movie_genre);
                movie_genre.setText(allMovies.get(position).getFilm_genre());
                final EditText userInputDays = promptsView.findViewById(R.id.input_text);

                checkFilm(allMovies.get(position).getFilm_name(), allMovies.get(position).getFilm_genre());

                if(userControlMail.equals(email)){
                    Toast toast = Toast.makeText(context,
                            "Вы уже взяли этот фильм", Toast.LENGTH_LONG);
                    toast.show();
                    userControlMail = "";
                }
                else if(!userControlMail.equals("not_found_email_in_database")){
                    Toast toast = Toast.makeText(context,
                            "Фильм у другого пользователя", Toast.LENGTH_LONG);
                    toast.show();
                    userControlMail = "";
                }
                else{
                    mDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("ДА",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(!userInputDays.getText().toString().equals("")) {
                                                //pay.setText(String.format("К оплате %s$", userInputDays.getText().toString()));
                                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                                Date today = new Date();
                                                Date todayWithZeroTime = null;
                                                try {
                                                    todayWithZeroTime = formatter.parse(formatter.format(today));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                Timestamp date_take = new Timestamp(todayWithZeroTime.getTime());

                                                take_film(allMovies.get(position).getFilm_name(), allMovies.get(position).getFilm_genre(), date_take, date_take, email, Integer.parseInt(userInputDays.getText().toString()));
                                            }

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
                            TextView error_message = promptsView.findViewById(R.id.error_message);
                            //System.out.println("!!!!!!!!!!!!!!!");
                            //System.out.println(userInputDays.getText().toString());
                            if((!userInputDays.getText().toString().startsWith("0")) && (!userInputDays.getText().toString().equals("")) &&
                                    (Integer.parseInt(userInputDays.getText().toString())<=7) && (userInputDays.getText() != null)){
                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date today = new Date();
                                Date todayWithZeroTime = null;
                                try {
                                    todayWithZeroTime = formatter.parse(formatter.format(today));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                // Перевод дней аренды фильма в секунды
                                int sec = Integer.parseInt(userInputDays.getText().toString()) * 86400;
                                Timestamp date_take = new Timestamp(todayWithZeroTime.getTime());

                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(date_take.getTime());
                                cal.add(Calendar.SECOND,sec);

                                Timestamp date_out = new Timestamp(cal.getTime().getTime());

                                take_film(allMovies.get(position).getFilm_name(), allMovies.get(position).getFilm_genre(), date_take, date_out,email, Integer.parseInt(userInputDays.getText().toString()));
                                wantToCloseDialog = true;
                                alertDialog.dismiss();
                                error_message.setText("");

                            }
                            else{
                                try{
                                    if(Integer.parseInt(userInputDays.getText().toString())>7){
                                        error_message.setText("Максимум на 7 дней");
                                    }
                                }
                                catch (Exception e){
                                    error_message.setText("Введите корректное значение!");
                                    System.out.println(e);
                                }
                                wantToCloseDialog = false;

                            }
                        }
                    });
                    userControlMail = "";
                }
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

    /* Взятие выбранного фильма */
    private void take_film(String name_film, String genre_film, Timestamp date_take, Timestamp date_out, String email, int money){
        takeFilmRunnable = new Runnable() {
            @Override
            public void run() {
                Database.takeFilm(name_film,genre_film,date_take,date_out,email);
                Database.addProfit(money);
            }
        };
        takeFilmThread = new Thread(takeFilmRunnable);
        takeFilmThread.start();
    }

    /* Проверка бронирования фильма: свободен, уже взят, взят другим пользователем */
    private void checkFilm(String name_film, String genre_film){
        checkFilmRunnable = new Runnable() {
            @Override
            public void run() {
                userControlMail = Database.checkFilm(name_film,genre_film);
            }
        };
        checkFilmThread = new Thread(checkFilmRunnable);
        checkFilmThread.start();
        try{
            checkFilmThread.join();
        }catch (InterruptedException eeee){
            System.out.println(eeee);
        }
    }
}
