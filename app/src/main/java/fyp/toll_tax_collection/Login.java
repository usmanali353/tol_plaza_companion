package fyp.toll_tax_collection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    TextInputEditText email,password;
    Button login_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        email=findViewById(R.id.email_txt);
        password=findViewById(R.id.password_txt);
        login_btn=findViewById(R.id.btn);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText().toString())){
                  email.setError("Enter Your Email");
                }else if(!Utils.isEmailValid(email.getText().toString())){
                    email.setError("Invalid Email");
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter Your Password");
                }else if(password.length()<6){
                    password.setError("Password should be atleast 6 Characters Long");
                }else {
                    Firebase_Operations.login(email.getText().toString(), password.getText().toString(), Login.this);
                }
            }
        });
    }
}
