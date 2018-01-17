package com.example.owner.eee508opencvtutorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToMainActivity();

            }

        });

        Button button4 = (Button) findViewById(R.id.button4);

        button4.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToMain6Activity();

            }

        });

        Button button5 = (Button) findViewById(R.id.button5);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);

            }

        });
    }

    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }

    private void goToMain6Activity() {

        Intent intent = new Intent(this, Main6Activity.class);

        startActivity(intent);

    }
}

