
package com.example.withfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegActivity extends AppCompatActivity
{
    private EditText email;
    private EditText pass;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    Button btnReg;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        email=findViewById(R.id.email_reg);
        pass=findViewById(R.id.password_reg);

        btnReg=findViewById(R.id.reg_btn);

        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);
    }
    public void clck2(View view)
    {
        Intent intet=new Intent(RegActivity.this,MainActivity.class);
        startActivity(intet);
    }

    public void btnclk(View view)
    {
        mDialog = new ProgressDialog(this);
        String mEmail=email.getText().toString().trim();
        String mPass=pass.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail))
        {
            email.setError("Required field");
            return;
        }
        if (TextUtils.isEmpty(mPass))
        {
            pass.setError("Required field");
            return;
        }

        mDialog.setMessage("Processing");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            public void onComplete(@NonNull Task<AuthResult> task)
            { if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"Successfull",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    mDialog.dismiss();
                }
                else {
                    Toast.makeText(getApplicationContext(),"problem",Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                }
            }
        });

    }

}


