package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static rothaugealex.de.tv_fernbedienung.MainActivity.PARAM_CHANNEL_CHANGE;
import static rothaugealex.de.tv_fernbedienung.MainActivity.executeTask;

public class Favorites extends MainActivity {

    public void toHome(View view){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        ListView mylistFavorites = (ListView) findViewById(R.id.listFavorites);

        SharedPreferences senderliste = getSharedPreferences("PREFS", 0);
        String senderString = senderliste.getString("Favoriten", "");
        String[] senderWords = senderString.split(",");
        final List<String> favList = new ArrayList<String>();

        for (int i = 0; i <senderWords.length; i++){
            favList.add(senderWords[i]);

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, favList);
        mylistFavorites.setAdapter(arrayAdapter);


        mylistFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
                //-----------------------------------------------------------------------
                executeTask(new Favorites.SendHttpReqTask(PARAM_CHANNEL_CHANGE + item, new Favorites.RespHandler() {
                    @Override
                    public void run() {
                        try {
                            // Check status before notify the user that the action is successful

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }));
                //-----------------------------------------------------------------------

            }
        });





        //---------------------------------------------------------------------------------------------------------------------------------




    }
}
