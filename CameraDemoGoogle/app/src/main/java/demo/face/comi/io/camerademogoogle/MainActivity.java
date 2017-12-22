package demo.face.comi.io.camerademogoogle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import demo.face.comi.io.camerademogoogle.activity.CameraActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void show(View view){
        Intent intent=new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}
