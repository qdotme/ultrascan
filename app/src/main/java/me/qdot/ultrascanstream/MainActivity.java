package me.qdot.ultrascanstream;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import java.net.*;
import java.io.*;

//import android.view.KeyEvent;
//import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.StrictMode;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mSensorX;
    private float mSensorY;
    Socket pcserver = null;
    DataOutputStream os = null;
    DataInputStream is = null;

    private EditText mIpEditText;
    private Button mConnectButton;
    private Button mCloseButton;
    private Button mQuitButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        getAddr();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    private void getAddr(){
        //Connect Button Pressed - open connection to server
        // Get our EditText object.
        // Initialize the compose field with a listener for the return key
        mIpEditText = (EditText) findViewById(R.id.editipaddr);
        // mIpEditText.setOnEditorActionListener(mWriteListener);

        // Connect Button
        mConnectButton = (Button) findViewById(R.id.conbut);
        mConnectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.editipaddr);
                String ipAddr = view.getText().toString();
                setupConnection(ipAddr);
            }
        });

        // Close Connection button
        mCloseButton = (Button) findViewById(R.id.closebut);
        mCloseButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeShop();
            }
        });
        // Quit APP Connection button
        mQuitButton = (Button) findViewById(R.id.quitbut);
        mQuitButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                quitShop();
            }
        });
    }
 /* The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String ipAddr = view.getText().toString();
                //setupConnection(ipAddr);
            }
            //if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };*/


    private void setupConnection(String ipAddr){
        TextView view = (TextView) findViewById(R.id.msgView);
        try {
            pcserver = new Socket(ipAddr, 9999);
            os = new DataOutputStream(pcserver.getOutputStream());
            is = new DataInputStream(pcserver.getInputStream());
        } catch (UnknownHostException e) {
            view.setText("Don't know about host: hostname");
        } catch (IOException e) {
            view.setText("Couldn't get I/O for the connection to: hostname");
        }

        //TextView view = (TextView) findViewById(R.id.xval);
        //view.setText("Connected or at least tried...");
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        /*
         * record the accelerometer data, the event's timestamp as well as
         * the current time. The latter is needed so we can calculate the
         * "present" time during rendering. In this application, we need to
         * take into account how the screen is rotated with respect to the
         * sensors (which always return data in a coordinate space aligned
         * to with the screen in its native orientation).
         */
        mSensorX = event.values[0];
        mSensorY = event.values[1];
        String sensorsX = new Float(mSensorX).toString();
        String sensorsY = new Float(mSensorY).toString();
/*
        TextView view = (TextView) findViewById(R.id.yval);
        view.setText(sensorsX+"     "+sensorsY);
*/
        sendingInfo(sensorsX, sensorsY, mSensorX);//addded float
  }

    private void sendingInfo(String sensorsX, String sensorsY, Float mSensorsX){
//        TextView view = (TextView) findViewById(R.id.xval);
        if (pcserver != null && os != null && is != null) {
            try {
                //send the X sensor data
                os.writeFloat(mSensorsX);
                //os.writeBytes(sensorsY);
                // clean up:
                // close the output stream
                // close the input stream
                // close the socket
                //os.close();
                //is.close();
                //pcserver.close();
            } catch (UnknownHostException e) {
//                view.setText("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
//                view.setText("IOException:  " + e);
            }
            //view.setText("Sent stuff I guess");
        }
    }

    private void closeShop(){
//        TextView view = (TextView) findViewById(R.id.msgView);

        if (pcserver != null && os != null && is != null) {
            try {

                //os.writeBytes(sensorsX);
                //os.writeBytes(sensorsY);
                // clean up:
                // close the output stream
                // close the input stream
                // close the socket
                os.close();
                is.close();
                pcserver.close();
            } catch (UnknownHostException e) {
//                view.setText("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
//                view.setText("IOException:  " + e);
            }
        }
    }
    private void quitShop(){
//        TextView view = (TextView) findViewById(R.id.msgView);
//        view.setText("Exiting App");
        onDestroy();

    }
}
