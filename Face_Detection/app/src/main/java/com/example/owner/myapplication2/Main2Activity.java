package com.example.owner.myapplication2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToMain3Activity();

            }

        });


        Button button5 = (Button) findViewById(R.id.button5);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //finish();
                //System.exit(0);
                goToMainActivity();

            }

        });
    }

    private void goToMain3Activity() {

        Intent intent = new Intent(this, Main3Activity.class);

        startActivity(intent);

    }

    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }
}


