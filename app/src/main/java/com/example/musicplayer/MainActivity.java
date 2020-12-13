package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private List<String> music_nameList = new ArrayList<>();
    private List<String> urlList=new ArrayList<>();
    ArrayAdapter simpleAdapter;
    ListView musicList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music_nameList.add("Empty Playground");
        music_nameList.add("Love is Here");
        music_nameList.add("Playing With Shadows");
        music_nameList.add("Night Sky");
        music_nameList.add("Endless Rivers");
            String url1="https://files.freemusicarchive.org/storage-freemusicarchive-org/tracks/RIIGymORSJBaI5Wx9sF8DbZYtYZlaXQhQcugFiIp.mp3?download=1";
            String url2="https://files.freemusicarchive.org/storage-freemusicarchive-org/tracks/C8ADQUFFdQX75kEXsoNfx0PL1SYKR5S950Y0Pe1F.mp3?download=1";
            String url3="https://files.freemusicarchive.org/storage-freemusicarchive-org/tracks/gJxfWoUaxAjABtqFjHqkcFKuRHy61tCIgp8arQV7.mp3?download=1";
            String url4="https://files.freemusicarchive.org/storage-freemusicarchive-org/tracks/kaj3p3avYy3noMSdxYgjiEIACwUYtXt3pJK7K9pZ.mp3?download=1";
            String url5="https://files.freemusicarchive.org/storage-freemusicarchive-org/tracks/x1mmRXGoAdIjD0bpFYombfpza2g4aGu4H8Nmt73T.mp3?download=1";


        urlList.add(url1);
        urlList.add(url2);
        urlList.add(url3);
        urlList.add(url4);
        urlList.add(url5);

        simpleAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,music_nameList);
        //适配器
        musicList = findViewById(R.id.list_view);
        musicList.setAdapter(simpleAdapter);
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                String name = music_nameList.get(position);
                Intent intent = new Intent(MainActivity.this, MusicPlayer.class);
                intent.putExtra("position",position);
                String[] namelist={"","","","",""};
                for(int i=0;i<5;i++){
                    namelist[i]=music_nameList.get(i);
                }
                String[] urlStr = {"","","","",""};
                for(int i=0;i<5;i++){
                    urlStr[i]=urlList.get(i);
                }
                intent.putExtra("nameList",namelist);
                intent.putExtra("urlStr",urlStr);
                startActivity(intent);
            }
        });

    }

}