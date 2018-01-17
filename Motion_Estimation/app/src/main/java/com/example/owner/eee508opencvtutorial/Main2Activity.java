package com.example.owner.eee508opencvtutorial;

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
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                goToMain3Activity();

            }

        });
    }

    private void goToMain3Activity() {

        Intent intent = new Intent(this, Main3Activity.class);

        startActivity(intent);

    }
}
