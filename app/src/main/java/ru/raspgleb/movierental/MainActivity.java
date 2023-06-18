package ru.raspgleb.movierental;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Context context = this;
    Thread initThread, addUserThread, searchUserThread;
    Runnable initRunnable, addUserRunnable, searchUserRunnable;
    Button registration, entrance;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); // Подключение к БД

        registration = findViewById(R.id.registration);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);

                View promptsView = li.inflate(R.layout.window_registration, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

                mDialogBuilder.setView(promptsView);

                final EditText userInputSurname = promptsView.findViewById(R.id.surname);
                final EditText userInputName =  promptsView.findViewById(R.id.name);
                final EditText userInputLastname = promptsView.findViewById(R.id.lastname);
                final EditText userInputMail =  promptsView.findViewById(R.id.mail);
                final EditText userInputPassword =  promptsView.findViewById(R.id.registration_password);

                ////////////////////////////////////  РЕГИСТРАЦИЯ  ////////////////////////////////////
                mDialogBuilder
                        .setCancelable(false)
                        // Подтверждение на добавление новой задачи
                        .setPositiveButton("Зарег",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // После регистрации переход на активность с фильмами
                                        Intent all_films = new Intent(context,Films.class);
                                        all_films.putExtra("email",userInputMail.getText().toString());
                                        add_in_user_data(userInputSurname.getText().toString(),
                                                userInputName.getText().toString(),
                                                userInputLastname.getText().toString(),
                                                userInputMail.getText().toString(),
                                                userInputPassword.getText().toString());
                                        context.startActivity(all_films);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                Button okey = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                okey.setTextColor(Color.RED);
                Button cancel = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancel.setTextColor(Color.GRAY);

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean wantToCloseDialog = false;
                        TextView error_message = promptsView.findViewById(R.id.error_message);
                        // Условие для числового поля
                        if(!userInputSurname.getText().toString().equals("") && !userInputName.getText().toString().equals("") &&
                                !userInputLastname.getText().toString().equals("") && !userInputMail.getText().toString().equals("") &&
                                !userInputPassword.getText().toString().equals("")){
                            // Проверка на наличие дубликата новых данных и уже имеющихся о пользователях
                            String userControl = search_in_user_data(userInputMail.getText().toString());

                            if(userControl.equals("")){
                                Intent all_films = new Intent(context,Films.class);
                                all_films.putExtra("email",userInputMail.getText().toString());
                                add_in_user_data(userInputSurname.getText().toString(),
                                        userInputName.getText().toString(),
                                        userInputLastname.getText().toString(),
                                        userInputMail.getText().toString(),
                                        userInputPassword.getText().toString());
                                error_message.setText("");
                                wantToCloseDialog = true;

                                alertDialog.dismiss();
                                context.startActivity(all_films);
                            }
                            else{
                                error_message.setText("Вы уже зарегистрированы!");
                                wantToCloseDialog = false;
                            }
                            
                        }
                        else{
                            error_message.setText("Заполните все поля!");
                            wantToCloseDialog = false;

                        }
                    }
                });
            }
        });
        
        ////////////////////////////////////  ВОЙТИ  ////////////////////////////////////
        entrance = findViewById(R.id.entrance);
        entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li2 = LayoutInflater.from(context);
                View promptsView2 = li2.inflate(R.layout.window_entrance, null);
                AlertDialog.Builder mDialogBuilder2 = new AlertDialog.Builder(context);
                mDialogBuilder2.setView(promptsView2);

                final EditText userInputLoginMail =  promptsView2.findViewById(R.id.entrance_mail);
                final EditText userInputLoginPwd =  promptsView2.findViewById(R.id.entrance_password);

                mDialogBuilder2
                        .setCancelable(false)
                        // Подтверждение на добавление новой задачи
                        .setPositiveButton("Войти",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Переход на активность с фильмами
                                        Intent all_films = new Intent(context,Films.class);
                                        all_films.putExtra("email",userInputLoginMail.getText().toString());
                                        context.startActivity(all_films);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });


                AlertDialog alertDialog = mDialogBuilder2.create();
                alertDialog.show();
                Button okey = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                okey.setTextColor(Color.RED);
                Button cancel = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancel.setTextColor(Color.GRAY);

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean wantToCloseDialog = false;
                        TextView entrance_error_message = promptsView2.findViewById(R.id.entrance_error_message);
                        if(!userInputLoginMail.getText().toString().equals("") && !userInputLoginPwd.getText().toString().equals("")){
                            // Проверка на наличие дубликата новых данных и уже имеющихся о пользователях
                            String userControl = search_in_user_data(userInputLoginMail.getText().toString());

                            if((!userControl.equals("")) && (userControl.equals(userInputLoginPwd.getText().toString()))){
                                Intent all_films = new Intent(context,Films.class);
                                all_films.putExtra("email",userInputLoginMail.getText().toString());
                                entrance_error_message.setText("");
                                wantToCloseDialog = true;
                                alertDialog.dismiss();
                                context.startActivity(all_films);
                            }
                            else{
                                entrance_error_message.setText("Вы не зарегистрированы / Неверный пароль!");
                                wantToCloseDialog = false;
                            }

                        }
                        else{
                            entrance_error_message.setText("Заполните все поля");
                            wantToCloseDialog = false;
                        }
                    }
                });
            }
        });


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

    /* Добавление пользователя в БД */
    private void add_in_user_data(String surname, String name,String lastname, String email, String password){
        addUserRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Database.connection("192.168.92.184","5432");
                    Database.addUser(surname, name, lastname, email, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        addUserThread = new Thread(addUserRunnable);
        addUserThread.start();
    }

    /* Поиск пользователя в БД */
    private String search_in_user_data(String email) {
        final String[] password = {""}; // Получает пароль зарегистрированного пользователя
        //final boolean[] bb = {false};
        searchUserRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if(!search_in(email).equals(""))
                        password[0] = search_in(email);
                    else
                        password[0] = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        searchUserThread = new Thread(searchUserRunnable);
        searchUserThread.start();
        try{
            searchUserThread.join();
        }catch (InterruptedException ee){
            System.out.println(ee);
        }
        System.out.println(password[0]);
        return password[0];
    }

    /* Метод поиска пользователя в БД */
    private String search_in(String email){
        try {
            Database.connection("192.168.92.184","5432");
            String result = Database.searchUser(email);
            if(!result.equals("")){
                return result;
            }
            else{
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}