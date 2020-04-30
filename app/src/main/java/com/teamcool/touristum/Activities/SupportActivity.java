package com.teamcool.touristum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamcool.touristum.Adapters.ChatAdapter;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Chat;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Employee;
import com.teamcool.touristum.data.model.LoggedInUser;
import com.teamcool.touristum.data.model.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SupportActivity extends AppCompatActivity {


    public static int VIEW_MODE;
    public static final int VIEW_MODE_CLIENT = 1;
    public static final int VIEW_MODE_EMPLOYEE = 2;
    public static final int VIEW_MODE_HOTEL = 3;
    public static final int VIEW_MODE_AGENCY = 4;

    private Button sendButton;
    private TextView messageBox;
    private RecyclerView rv_Chat;
    private ChatAdapter chatAdapter;
    private TextView tv_name;

    private Chat chat;
    private ArrayList<Message> messages;

    private Client client;
    private Employee employee;

    public static final String TAG  = "SupportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        sendButton = findViewById(R.id.sendButton);
        messageBox = findViewById(R.id.messageBox);
        rv_Chat = findViewById(R.id.rv_chat);
        tv_name = findViewById(R.id.tv_name);

        rv_Chat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this);

        chat = (Chat) getIntent().getSerializableExtra("ViewChat");

        messages = chat.getMessages();

        if(VIEW_MODE == VIEW_MODE_CLIENT){
            chatAdapter.setVIEW_MODE(VIEW_MODE);
            tv_name.setText("Talking To : " + chat.getEmployeeName() + " (ID:" + chat.getEmployeeID() + ")");

        }
        else if(VIEW_MODE == VIEW_MODE_EMPLOYEE){
            chatAdapter.setVIEW_MODE(VIEW_MODE);
            tv_name.setText("Talking To : " + chat.getClientName() + " (ID:" + chat.getClientID() + ")");
        }

        chatAdapter.setMessages(messages);
        rv_Chat.setAdapter(chatAdapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(VIEW_MODE == VIEW_MODE_CLIENT)
                    messages.add(new Message("client",messageBox.getText().toString(),chat.getClientName()));
                else if(VIEW_MODE == VIEW_MODE_EMPLOYEE)
                    messages.add(new Message("employee",messageBox.getText().toString(),chat.getEmployeeName()));


                chatAdapter.setMessages(messages);
                chatAdapter.notifyDataSetChanged();
                messageBox.setText("");

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try
        {
            HashMap<String,Chat> saveFile;


            File file = new File(this.getFilesDir() + "/chats.ser");
            if(file.exists()){

                FileInputStream fileInputStream = new FileInputStream(this.getFilesDir()+"/chats.ser");
                ObjectInputStream in = new ObjectInputStream(fileInputStream);

                saveFile = (HashMap<String, Chat>) in.readObject();
                in.close();
                fileInputStream.close();


            }

            else {
                saveFile = new HashMap<>();
            }

            FileOutputStream fos = this.openFileOutput("chats.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            Log.d(TAG, "onPause: " + this.getFilesDir());

            chat.setMessages(messages);

            String key = "client:" + chat.getClientID() + ",employee:" + chat.getEmployeeID();
            saveFile.put(key,chat);

            oos.writeObject(saveFile);
            oos.close();
            fos.close();
            Log.d(TAG, "onPause: " + "Serialized HashMap data is saved in chats.ser");
        }catch(IOException | ClassNotFoundException ioe)
        {
            ioe.printStackTrace();
        }
    }


}
