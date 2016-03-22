package ie.tcd.scss.amhiggin.collegecalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Button3Activity extends ActionBarActivity {

    private AlarmManagerBroadcastReceiver alarm;


    MediaPlayer mySound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button3);
        mySound = MediaPlayer.create(this,R.raw.music);
        alarm = new AlarmManagerBroadcastReceiver();

        Button btn =(Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int min= 1;
                int max = 5;
                Random r = new Random();
                int number = r.nextInt(max - min +1) +min;
                String mystring = String.valueOf(number);

                final ImageView imgtable = (ImageView)findViewById(R.id.imageView3);

                if (mystring.equals("1")){

                    final Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.mipmap.images1);
                    imgtable.setImageBitmap(img1);

                }

                if (mystring.equals("2")){

                    final Bitmap img2 = BitmapFactory.decodeResource(getResources(), R.mipmap.images2);
                    imgtable.setImageBitmap(img2);

                }

                if (mystring.equals("3")){

                    final Bitmap img3 = BitmapFactory.decodeResource(getResources(), R.mipmap.images3);
                    imgtable.setImageBitmap(img3);

                }

                if (mystring.equals("4")){

                    final Bitmap img4 = BitmapFactory.decodeResource(getResources(), R.mipmap.images4);
                    imgtable.setImageBitmap(img4);

                }

                if (mystring.equals("5")){

                    final Bitmap img5 = BitmapFactory.decodeResource(getResources(), R.mipmap.images5);
                    imgtable.setImageBitmap(img5);

                }

            }
        });



    }

    public void startMusic(View view) {

        if (mySound.isPlaying()) {
            mySound.pause();
        }
        else {
            mySound.start();
        }


    }

    public void onetimeTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.setOnetimeTimer(context);
            Toast toast = Toast.makeText(context, "Starting 10 minute break.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }
}
