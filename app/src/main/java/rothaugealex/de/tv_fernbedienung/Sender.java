package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Sender extends AppCompatActivity {

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void toSettings(View view){
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        ListView mylist = (ListView) findViewById(R.id.listSender);

        ArrayList<String> myarraylist = new ArrayList<String>();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, myarraylist);

        mylist.setAdapter(arrayAdapter);
    }
}
