package com.example.musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import static android.os.SystemClock.sleep;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener{
    private static final int UPDATE = 1;
    private static final int MUSICDURATION = 0;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private boolean isStop;
    private String musicName="";
    private SeekBar seekBar;
    private TextView timeNow;
    private TextView time;
    private TextView musicNameT;
    private TextView nowTimeT;
    private TextView timeT;
    private int position;
    private int nowTimeInt=0;
    private int timeInt=0;
    private String url;
    private boolean isSeekBarChanging;
    private boolean fileExist=false;
    private boolean hadPlay=false;
    private String[] nameList={"","","","",""};
    private String[] urlStr={"","","","",""};
    private DownloadService.DownloadBinder downloadBinder;
    private boolean change=false;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadBinder=null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG","--downLoadBinder--");
            downloadBinder = (DownloadService.DownloadBinder) service;

        }

    };
    private Handler handler=new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MUSICDURATION:
                    seekBar.setMax(mediaPlayer.getDuration());
                    break;
                case UPDATE:
                        try {
//                        change=false;
//                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                        change=true;
                            nowTimeInt = mediaPlayer.getCurrentPosition();
                            nowTimeT.setText(formatTime(nowTimeInt));
                            seekBar.setProgress((int) (nowTimeInt*100.0/(timeInt*1.0)));
                            //onProgressChanged(seekBar,nowTimeInt,false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    //handler.sendEmptyMessageDelayed(UPDATE);
                    handler.sendEmptyMessageDelayed(UPDATE,500);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);
        Button play= findViewById(R.id.play);
        Button pause=findViewById(R.id.pause);
        Button last=findViewById(R.id.last);
        Button next=findViewById(R.id.next);
        Button back=findViewById(R.id.back);
        timeNow=findViewById(R.id.music_now_time);
        time=findViewById(R.id.music_time);
        seekBar=findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        musicNameT=findViewById(R.id.music_name);
        nowTimeT=findViewById(R.id.music_now_time);
        timeT=findViewById(R.id.music_time);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        Intent intent = getIntent();
        nowTimeT.setText(formatTime(nowTimeInt));
        timeT.setText(formatTime(timeInt));
        position=intent.getIntExtra("position",-1);
        nameList =intent.getStringArrayExtra("nameList");
        urlStr = intent.getStringArrayExtra("urlStr");
        musicName= nameList[position];
        url=urlStr[position];
        musicNameT.setText(nameList[position]);
        if(ContextCompat.checkSelfPermission(MusicPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MusicPlayer.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        else{
            initMediaPlayer();
        }


    }

    private void initMediaPlayer(){
        Log.e("TAG","--initMediaPlayer()--");
        File file = new File(getFilesDir(), musicName + ".mp3");
        //File file=new File(Environment.getExternalStorageDirectory(),musicName+".mp3");
        //if(file.exists()){fileExist=true;}
            if(!hadPlay) {
                Intent intent = new Intent(MusicPlayer.this, DownloadService.class);
                startService(intent); // 启动服务
                bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
                if (ContextCompat.checkSelfPermission(MusicPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MusicPlayer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                }else{
                    Toast.makeText(this,"拒绝权限将无法使用程序",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        Log.e("TAG","--onClick()--");
        switch (view.getId()){
            case R.id.play:
                if(hadPlay==false) {
                    File file = new File(getFilesDir(), musicName + ".mp3");
                    String filePath=getFilesDir()+"/"+musicName + ".mp3";
                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (downloadBinder != null) {
                            downloadBinder.startDownload(url + ";" + filePath);
                        }
                    }
                    try {
                        mediaPlayer.setDataSource(file.getPath());
                        Log.e("TAG", "-- mediaPlayer.prepare()--");
                        mediaPlayer.prepare();
                        timeInt = mediaPlayer.getDuration();
                        timeT.setText(formatTime(timeInt));
                        Log.e("TAG", "--initMediaPlayer()---END---");
                        hadPlay=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        hadPlay=false;
                    }
                }
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    handler.sendEmptyMessage(UPDATE);  //发送Message
                }
                break;
            case R.id.pause:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                break;
            case R.id.last:
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position>0) {
                    Intent intent = new Intent(MusicPlayer.this, MusicPlayer.class);
                    String name = nameList[position - 1];
                    intent.putExtra("urlStr",urlStr);
                    intent.putExtra("position", position-1);
                    intent.putExtra("nameList", nameList);
                    startActivity(intent);
                }
                break;
            case R.id.next:{
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position< nameList.length-1) {
                    Intent intent = new Intent(MusicPlayer.this, MusicPlayer.class);
                    String name = nameList[position + 1];
                    intent.putExtra("position", position+1);
                    intent.putExtra("nameList", nameList);
                    intent.putExtra("urlStr",urlStr);
                    startActivity(intent);
                }
                break;



            }
            case R.id.back:{
                Intent intent=new Intent(MusicPlayer.this,MainActivity.class);
                startActivity(intent);
                break;
            }


        }
    }
    protected void onDestroy() {
        Log.e("TAG","--onDestroy()--");
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        unbindService(connection);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b==true) {
            nowTimeInt = (int) (i * 1.0 / 100.0 * timeInt);
        }
            nowTimeT.setText(formatTime(nowTimeInt));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
            //nowTimeInt = (int) (seekBar.getProgress() * 1.0 / 100.0 * timeInt);
            nowTimeT.setText(formatTime(nowTimeInt));
            mediaPlayer.seekTo(nowTimeInt);

    }
    private String formatTime(int length){
        Date date = new Date(length);
//时间格式化工具
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String totalTime = sdf.format(date);
        return totalTime;
    }
}