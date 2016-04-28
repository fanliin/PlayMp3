package com.jikexueyuan.playmp3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlayRaw;
    private Button btnStopPlay;
    private Button btnPlayIntent;
    private Button btnPlayMediaPlayer;
    private MediaPlayer mediaPlayer;
    String fileName = "music.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnPlayRaw = (Button) findViewById(R.id.btnPlayRaw);
        btnStopPlay = (Button) findViewById(R.id.btnStopPlat);
        btnPlayIntent = (Button) findViewById(R.id.btnPlayIntent);
        btnPlayMediaPlayer = (Button) findViewById(R.id.btnPlayMediaPlayer);

        btnPlayRaw.setOnClickListener(this);
        btnStopPlay.setOnClickListener(this);
        btnPlayIntent.setOnClickListener(this);
        btnPlayMediaPlayer.setOnClickListener(this);

        if (!fileExist(fileName)) {
            copyToMobile(fileName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlayRaw:
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.music);
                    mediaPlayer.start();
                }
                break;
            case R.id.btnStopPlat:
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;
            case R.id.btnPlayIntent:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + getDir() + fileName), "audio/mp3");
                startActivity(intent);
                break;
            case R.id.btnPlayMediaPlayer:
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(getDir() + fileName);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mediaPlayer.start();
                }
        }
    }


    /**
     * 获取文件目录
     *
     * @return 文件路径
     */
    private String getDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/myplayer/";
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return 文件是否存在
     */
    private boolean fileExist(String fileName) {
        File file = new File(getDir() + fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 复制文件到扩展存储
     *
     * @param fileName
     */
    private void copyToMobile(final String fileName) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                File dir = new File(getDir());
                if (!dir.exists()) {
                    dir.mkdir();
                }

                InputStream fis = null;
                OutputStream fos = null;
                fis = getResources().openRawResource(R.raw.music);
                File to = new File(getDir(), fileName);
                try {
                    fos = new FileOutputStream(to);
                    byte[] buf = new byte[4096];
                    while ((fis.read(buf)) != -1) {
                        fos.write(buf);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }
}
