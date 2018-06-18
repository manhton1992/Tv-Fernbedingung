package rothaugealex.de.tv_fernbedienung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Einstellungen extends AppCompatActivity {


    public void toHome(View view){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    public void favFunction(View view) {
        Intent intent = new Intent(getApplicationContext(), Favorites.class);

        startActivity(intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);



        ListView mylist = (ListView) findViewById(R.id.listSettings);

        final ArrayList<String> myarraylist = new ArrayList<String>();

        myarraylist.add("Informationen");
        myarraylist.add("Reset");
        myarraylist.add("Automatisches Ausschlaten");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Einstellungen.this, android.R.layout.simple_list_item_1, myarraylist);

        mylist.setAdapter(arrayAdapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   switch (position) {


                       case 0: {
                           Intent intent = new Intent(getApplicationContext(), Informationen.class);
                           startActivity(intent);
                           }
                           break;

                       case 1:{

                           Intent intent = new Intent(getApplicationContext(), Reset.class);
                           startActivity(intent);

                       }
                           break;

                       case 2:{
                           Intent intent = new Intent(getApplicationContext(), AutomatischesAbschalten.class);
                           startActivity(intent);
                       }
                           break;

                   }
                }
                }
                );


    }
}
