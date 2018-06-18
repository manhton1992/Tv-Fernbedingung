package rothaugealex.de.tv_fernbedienung;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Sender extends MainActivity  {

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void toSettings(View view){
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);
        startActivity(intent);
    }

    public void favFunction(View view){
        Intent intent = new Intent (getApplicationContext(), Favorites.class);
        startActivity(intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
       //----------------------------------------------------------------------------------------
        ListView mylist = (ListView) findViewById(R.id.listSender);
        SharedPreferences senderliste = getSharedPreferences("PREFS", 0);
        String senderString = senderliste.getString("Sender", "");
        String[] senderWords = senderString.split(",");
        final List<String> senderlist = new ArrayList<String>();


        for (int i = 0; i <senderWords.length; i++){
            senderlist.add(senderWords[i]);

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, senderlist);
        mylist.setAdapter(arrayAdapter);


        //TODO: on lick listener change Channel
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
                //-----------------------------------------------------------------------
                executeTask(new Sender.SendHttpReqTask(PARAM_CHANNEL_CHANGE + item, new Sender.RespHandler() {
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
        
        
        //TODO: Long lick listener add to favorites
        mylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);
                Toast.makeText(Sender.this, "Zu favoriten hinzugefügt!: " +  senderlist.get(pos) , Toast.LENGTH_SHORT).show();
                //TODO: Shared Preferecs Faavoriten erstellen
                StringBuilder stringBuilder = new StringBuilder();
                final List<String> allefavoriten = new ArrayList<String>();

               allefavoriten.add(senderlist.get(pos));

                for (String s : allefavoriten) {
                    stringBuilder.append(s);
                    stringBuilder.append(",");
                }

                SharedPreferences senderliste = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = senderliste.edit();
                editor.putString("Favoriten", stringBuilder.toString());
                editor.commit();

                return true;
            }
        });


        //----------------------------------------------------------------------------------------

       // ListView mylist = (ListView) findViewById(R.id.listSender);

        //ArrayList<String> myarraylist = new ArrayList<String>();


       // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, myarraylist);

        //mylist.setAdapter(arrayAdapter);
    }
}
