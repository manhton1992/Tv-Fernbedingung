package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity {

    private ArrayList<String> favList;

    public void toHome(View view){
        onBackPressed();
    }

    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ListView mylist = findViewById(R.id.listFavorites);

        favList = new ArrayList<>();

        for (String fav: MainActivity.favorites)
            favList.add(MainActivity.programNames.get(MainActivity.programs.indexOf(fav)));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.select_dialog_item, favList);
        mylist.setAdapter(arrayAdapter);


        //TODO: on lick listener change Channel
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();

                MainActivity.changeProg(MainActivity.programNames.indexOf(item));
            }
        });
    }
}
