package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    public void favFunction(View view) {
        Intent intent = new Intent(getApplicationContext(), Favorites.class);

        startActivity(intent);
    }

    public void senderFunction(View view){
        Intent intent = new Intent(getApplicationContext(), Sender.class);

        startActivity(intent);
    }

    public void vorFunction(View view){


        Toast.makeText(this, "Nächster Sender", Toast.LENGTH_SHORT).show();
    }

    public void zurückFunction(View view){

        Toast.makeText(this, "Vorheriger Sender", Toast.LENGTH_SHORT).show();
    }

    public void leiserFunction(View view)
    {
        Toast.makeText(this, "Fernseher wurde leiser gestellt", Toast.LENGTH_SHORT).show();
    }

    public void lauterFunction(View view)
    {
        Toast.makeText(this, "Fernseher wurde lauter gestellt", Toast.LENGTH_SHORT).show();
    }

    public void stummFunction(View view)
    {
        Toast.makeText(this, "Fernseher wurde stumm geschaltet", Toast.LENGTH_SHORT).show();
    }

    public void pauseFunction(View view)
    {
        ImageButton imagebutton = (ImageButton) findViewById(R.id.pause);
        imagebutton.setImageResource(R.drawable.play2);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            View view = this.getWindow().getDecorView();


    }
}
