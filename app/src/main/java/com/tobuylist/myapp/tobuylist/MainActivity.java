package com.tobuylist.myapp.tobuylist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.LoggingMXBean;

public class MainActivity extends Activity {
    private EditText userInput;
    private Button sendButton;
    private ListView itemList;
    private SQLiteDatabase dataStorage;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> itemsArray;
    private ArrayList<Integer> idArray;


        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            try {

                userInput = findViewById(R.id.userInputId);
                sendButton = findViewById(R.id.sendButtonId);
                itemList = findViewById(R.id.itemListId);

                dataStorage = openOrCreateDatabase("apptobuylist", MODE_PRIVATE, null);

                dataStorage.execSQL("CREATE TABLE IF NOT EXISTS purchaselist (id INTEGER PRIMARY KEY AUTOINCREMENT, itemtobuy VARCHAR)");

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String typedText = userInput.getText().toString();
                        saveItem(typedText);

                    }
                });

                itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        deleteItems(idArray.get(position));
                        loadItems();
                        return false;
                    }
                });

                loadItems();

            } catch (Exception e){
                e.printStackTrace();
            }
    }
    private void saveItem(String textToSave){
            try{
                if (textToSave.equals("")){
                    Toast.makeText(MainActivity.this, "Can't create an empty item.", Toast.LENGTH_SHORT).show();
                } else {
                    dataStorage.execSQL("INSERT INTO purchaselist (itemtobuy) VALUES ('" + textToSave + "')");
                    Toast.makeText(MainActivity.this, "Item saved successfully.", Toast.LENGTH_SHORT).show();
                    loadItems();
                    userInput.setText("");
                }


            } catch (Exception e){
                e.printStackTrace();
            }

    }
    private void loadItems (){
            try{
                Cursor selectItem = dataStorage.rawQuery("SELECT * FROM purchaselist ORDER BY id DESC", null);
                int indexColumnId = selectItem.getColumnIndex("id");
                int indexColumnItemToBuy = selectItem.getColumnIndex("itemtobuy");
                itemsArray = new ArrayList<String>();
                idArray = new ArrayList<Integer>();
                itemsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        itemsArray);
                itemList.setAdapter(itemsAdapter);

                selectItem.moveToFirst();
                while (selectItem != null){

                    Log.i("dataStorage", selectItem.getString(indexColumnId) + " - " + selectItem.getString(indexColumnItemToBuy));
                    itemsArray.add(selectItem.getString(indexColumnItemToBuy));
                    idArray.add(Integer.parseInt( selectItem.getString(indexColumnId) ));
                    selectItem.moveToNext();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
    }
    private void deleteItems (Integer idToDelete){
            try {
                dataStorage.execSQL("DELETE FROM purchaselist WHERE id=" + idToDelete);
            } catch (Exception e){
                e.printStackTrace();
            }

    }
}
