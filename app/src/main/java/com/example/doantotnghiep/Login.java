package com.example.doantotnghiep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText mEmail, mMatkhau;
    Button buttondangnhap;
    TextView textdangkyngay, textquenmatkhau;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.textemaillogin);
        mMatkhau = findViewById(R.id.textmatkhaulogin);
        fAuth = FirebaseAuth.getInstance();
        buttondangnhap = findViewById(R.id.buttondangnhap);
        textdangkyngay = findViewById(R.id.textdangkyngay);
        textquenmatkhau = findViewById(R.id.textquenmatkhau);

        buttondangnhap.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  String email = mEmail.getText().toString().trim();
                                                  String matkhau = mMatkhau.getText().toString().trim();

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

                                                  //xác thực danh tính người dùng

                                                  fAuth.signInWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<AuthResult> task) {
                                                          if (task.isSuccessful()) {
                                                              Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                                              startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                          } else {
                                                              Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                          }
                                                      }

                                                  });
                                              }
                                          });
        textdangkyngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),register.class));
            }
        });
        textquenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(v.getContext());
                passwordresetDialog.setTitle("Reset Password?");
                passwordresetDialog.setMessage("Nhâp Email của bạn để lấy liên kết");
                passwordresetDialog.setView(resetMail);

                passwordresetDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this,"Reset Link Sent To Your Email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"Error ! Reset Link is not sent" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordresetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
            }
        });
            }}

