package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class AutomatischesAbschalten extends AppCompatActivity {
    int sekunden = 0;
    int minute = 0;
    int stunde = 0;
    int shutdown = 0;

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void favFunction(View view) {
        Intent intent = new Intent(getApplicationContext(), Favorites.class);

        startActivity(intent);
    }

    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    public void sekundeFunc(View view){


        sekunden = sekunden + 5;

        Toast.makeText(this, sekunden + " Sekunden!", Toast.LENGTH_SHORT).show();

    }

    public void minuteFunc(View view){


        minute = minute + 1;

        Toast.makeText(this, minute + " Minuten!", Toast.LENGTH_SHORT).show();

    }

    public void stundeFunc(View view){


        stunde = stunde + 1;

        Toast.makeText(this, stunde + " Stunden!", Toast.LENGTH_SHORT).show();

    }

    public void bestätigenFunc(View view) {



        shutdown = 1000*sekunden + 60000 * minute + 3600000 * stunde;

        Toast.makeText(this, "Ihr Gerät wird in " + stunde + " Stunden" + minute + " Minuten und " + sekunden + " Sekunden ausgeschaltet!", Toast.LENGTH_SHORT).show();


        final long period = 0;
        new CountDownTimer(shutdown, 1000) {

            public void onTick(long millisUntilFinished) {
              //  Toast.makeText(AutomatischesAbschalten.this, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
            }

            public void onFinish() {
                Toast.makeText(AutomatischesAbschalten.this, "Ihr Gerät wird Ausgeschaltet!", Toast.LENGTH_SHORT).show();

            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatisches_abschalten);


    }
}
