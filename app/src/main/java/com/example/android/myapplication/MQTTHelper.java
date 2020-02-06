package com.example.android.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;
    final String ServerUri = "tcp://192.168.137.251:1883";
    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "cmnd/sw1/POWER1";
    String changeSwitch="OFF";
    // final String username = "hqnetbea";
    //final String password = "oHcizBy0VFdh";
    Context context;
    boolean isOn=false;
    public MQTTHelper(final Context context) {
        this.context= context;
        mqttAndroidClient = new MqttAndroidClient(context, ServerUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("mqtt", serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(context, "Connection_lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("Mqtt", message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.w("Mqtt", token.toString());
            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }
    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        //  mqttConnectOptions.setUserName(username);
        // mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();

                    // Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + ServerUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }
    public void toPublish()
    {
        Log.w("Mqtt", "publishing");
        String topic = "cmnd/sw1/POWER1";
        if(isOn)
        {changeSwitch="OFF";
            isOn=!isOn;}
        else
        {changeSwitch="ON";
            isOn=!isOn;}
        //byte[] encodedpayload = new byte[0];
        try {
            //   encodedpayload = payload.getBytes("UTF-8");
            //   MqttMessage message = new MqttMessage(encodedpayload);
            mqttAndroidClient.publish(topic, changeSwitch.getBytes(),0,true);
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }
}
