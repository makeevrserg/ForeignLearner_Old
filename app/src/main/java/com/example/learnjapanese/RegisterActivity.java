package com.example.learnjapanese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.learnjapanese.Adapters.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    Button birthButton;
    EditText editTextMail;
    EditText editTextName;
    EditText editTextLastName;
    EditText editTextPassword;
    EditText editTextRepPassword;
    RadioButton radioButtonSex;
    String monthBirth;
    String dayBirth;
    String yearBirth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        setContentView(R.layout.activity_register);
        birthButton = findViewById(R.id.buttonBirthSelect);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextName = findViewById(R.id.editTextName);
        editTextMail = findViewById(R.id.editTextRegEmail);
        editTextPassword = findViewById(R.id.editTextRegPassword);
        editTextRepPassword = findViewById(R.id.editTextRegRepeatPassword);
        radioButtonSex = findViewById(R.id.radioButtonFemale);
        radioButtonSex.setSelected(true);
        progressDialog = new ProgressDialog(this);
        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                monthBirth = Integer.toString(month);
                yearBirth = Integer.toString(year);
                dayBirth = Integer.toString(day);
                birthButton.setText(day + "/" + month + "/" + year);
            }
        };
    }

    DatePickerDialog.OnDateSetListener mDateListener;

    public void ChooseBirthOnClick(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateListener,
                year,
                month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public void onClickFinishRegistration(View view) {
        String password = editTextPassword.getText().toString();
        String name=editTextName.getText().toString();
        String lastName=editTextLastName.getText().toString();


        if (password.length()<6){
            Toast.makeText(this, "Пароль слишком короткий", Toast.LENGTH_SHORT).show();
            return;
        }
        String repPassword = editTextRepPassword.getText().toString();
        if (!password.equals(repPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
        String eMail = editTextMail.getText().toString();
        if (!eMail.contains("@")){
            Toast.makeText(this, "Почта не подходит", Toast.LENGTH_SHORT).show();
            return;
        } else if (eMail.isEmpty()){
            Toast.makeText(this, "Вы не ввели почту", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isMale = !radioButtonSex.isSelected();
        String sex = isMale?"Male":"Female";



        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog.setMessage("Регистрация пользователя");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final UserData user = new UserData(name,lastName,yearBirth,monthBirth,dayBirth,eMail,isMale?"Female":"Male");
        firebaseAuth.createUserWithEmailAndPassword(eMail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Регистрация завершена!", Toast.LENGTH_SHORT).show();
                            DatabaseReference dataUser;
                            dataUser = FirebaseDatabase.getInstance().getReference("UserData");

                            String id = dataUser.push().getKey();
                            Log.d(TAG, "onComplete: ID="+id);

                            user.uId=firebaseAuth.getUid();
                            dataUser.child(firebaseAuth.getUid()).setValue(user);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            RegisterActivity.this.finish();
                            RegisterActivity.this.overridePendingTransition(0, 0);
                            RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.overridePendingTransition(0, 0);


                        }else{
                            //Toast.makeText(RegisterActivity.this, "Не удалось завершить регистрацию", Toast.LENGTH_SHORT).show();
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(RegisterActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    private static final String TAG="RegisterActivity";
}


























