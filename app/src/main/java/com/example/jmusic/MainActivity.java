package com.example.jmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        dexter_per(listView);
    }

    public void dexter_per(ListView listView){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //Toast.makeText(MainActivity.this, "Runtime Permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> my_songs= fetch_songs(Environment.getExternalStorageDirectory());
                        String[] items = new String[my_songs.size()];
                        for (int i = 0 ; i <my_songs.size();i++){
                            items[i] = my_songs.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter<String> array_adapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(array_adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,Playsong.class);
                                String current_song = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songslist",my_songs);
                                intent.putExtra("currentSong",current_song);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
   public ArrayList<File> fetch_songs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if (songs != null){
            for (File my_file : songs){
                if(!my_file.isHidden() && my_file.isDirectory()){
                    arrayList.addAll(fetch_songs(my_file));
                }
                else{
                    if(my_file.getName().endsWith(".mp3")&& !my_file.getName().startsWith(".")){
                        arrayList.add(my_file);
                    }
                }
            }
        }
        return arrayList;
   }
}