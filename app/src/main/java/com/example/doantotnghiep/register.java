package com.example.doantotnghiep;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class register extends AppCompatActivity {
    String TAG = "TAG";
    EditText mHovaten, mEmail, mMatkhau, xacnhanmatkhau, mSodienthoai;
    Button buttondangky;
    TextView mtextdacotaikhoan;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mHovaten = findViewById(R.id.texthovaten);
        mEmail = findViewById(R.id.textemail);
        mMatkhau = findViewById(R.id.textmatkhau);
        xacnhanmatkhau = findViewById(R.id.textxacnhanmatkhau);
        buttondangky = findViewById(R.id.buttondangky);
        mtextdacotaikhoan = findViewById(R.id.textdacotaikhoan);
        mSodienthoai = findViewById(R.id.sodienthoai);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        fstore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        buttondangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String matkhau = mMatkhau.getText().toString().trim();
                String hovaten = mHovaten.getText().toString();
                String sodienthoai = mSodienthoai.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email không hợp lệ!");
                    return;
                }
                if (TextUtils.isEmpty(matkhau)) {
                    mMatkhau.setError("Mật khẩu không hợp lệ!");
                    return;
                }
                if (matkhau.length() < 6) {
                    mMatkhau.setError("Mật khẩu cần >= 6 ký tự!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //đăng ký người dùng firebase
                fAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //gửi link xác thực
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(register.this,"Email xác thực đã được gửi.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailure: Email chưa được gửi"+ e.getMessage());
                                }
                            });


                            Toast.makeText(register.this, "Tạo người dùng", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("hovaten",hovaten);
                            user.put("email",email);
                            user.put("sodienthoai",sodienthoai);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "onSuccess: Thông tin người dùng đã được tạo cho "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(register.this, "Error !" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    ;
                });
            }
        });
        mtextdacotaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });




    }}