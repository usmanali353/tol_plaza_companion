package fyp.toll_tax_collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {
    ProgressBar pbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024,1024);
        setContentView(R.layout.splash_screen);
        pbar=findViewById(R.id.pbar);
        if(pbar.getVisibility()== View.GONE){
            pbar.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pbar.getVisibility()== View.VISIBLE){
                    pbar.setVisibility(View.GONE);
                }
                startActivity(new Intent(splash_screen.this,Login.class));
                finish();
            }
        },5000);
    }
}
