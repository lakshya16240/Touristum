package com.teamcool.touristum.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.teamcool.touristum.Adapters.AllChatsAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Chat;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Employee;
import com.teamcool.touristum.data.model.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ChatAcivity extends AppCompatActivity {


    public static int VIEW_MODE;
    public static final int VIEW_MODE_CLIENT = 1;
    public static final int VIEW_MODE_EMPLOYEE = 2;
    public static final int VIEW_MODE_HOTEL = 3;
    public static final int VIEW_MODE_AGENCY = 4;
    private Client client;
    private Employee employee;

    private ArrayList<Chat> chats ;

    private RecyclerView rv_allChats;
    private Button bv_newChat;
    private AllChatsAdapter allChatsAdapter;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private AlertDialog.Builder builder;

    public static final String TAG  = "ChatActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acivity);

        rv_allChats = findViewById(R.id.rv_allChats);
        bv_newChat = findViewById(R.id.bv_newChat);
        rv_allChats.setLayoutManager(new LinearLayoutManager(this));
        builder = new AlertDialog.Builder(this);

        mDbHelper = new DatabaseHelper(this);
        mDb = mDbHelper.getReadableDatabase();

        allChatsAdapter = new AllChatsAdapter(this, new AllChatsAdapter.onChatClickListener() {
            @Override
            public void selectedChat(Chat chat) {
                if (VIEW_MODE == VIEW_MODE_CLIENT)
                    SupportActivity.VIEW_MODE = VIEW_MODE;
                else if (VIEW_MODE == VIEW_MODE_EMPLOYEE)
                    SupportActivity.VIEW_MODE = VIEW_MODE;

                Intent intent = new Intent(ChatAcivity.this, SupportActivity.class);
                intent.putExtra("ViewChat", chat);
                startActivityForResult(intent, 1);

            }
        }, new AllChatsAdapter.onChatLongClickListener() {
            @Override
            public void selectedChat(final Chat chat) {

                if(VIEW_MODE == VIEW_MODE_EMPLOYEE) {

                    builder.setPositiveButton("Escalate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == DialogInterface.BUTTON_POSITIVE) {

                                String employeeID = chat.getEmployeeID();
                                String sql = "select employeeID, employeeName from employee " +
                                        "where employeeID =  (Select manager from Employee e, Branch b Where e.branchID = b.branchID and e.employeeID = '" +chat.getEmployeeID() +  "') ;";

                                Cursor cur = mDb.rawQuery(sql,null);
                                String manager = "";
                                String manager_name = "";
                                while(cur!=null && cur.moveToNext()){
                                    manager = cur.getString(0);
                                    manager_name = cur.getString(1);
                                }
                                chat.setEmployeeID(manager);
                                chat.setEmployeeName(manager_name);

                                saveChanges(employeeID,chat);
                            }

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                dialog.dismiss();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });


        if(VIEW_MODE == VIEW_MODE_CLIENT){
            client = LoginActivity.getLoggedInClient();
            bv_newChat.setVisibility(View.VISIBLE);

            chats = getChatsClient(client.getClientID());
            allChatsAdapter.setChats(chats);
            allChatsAdapter.setVIEW_MODE(VIEW_MODE_CLIENT);
            allChatsAdapter.notifyDataSetChanged();



            bv_newChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> employee_ids = new ArrayList<>();
                    ArrayList<String> employee_names = new ArrayList<>();
                    String sql = "Select employeeID,employeeName from employee where employeeType = 'customer_executive';";
                    Cursor cur = mDb.rawQuery(sql,null);
                    while(cur!=null && cur.moveToNext()){
                        employee_ids.add(cur.getString(0));
                        employee_names.add(cur.getString(1));

                    }

                    cur.close();
                    Random rand = new Random();
                    int employee_position = rand.nextInt(employee_ids.size());

                    String employeeID = employee_ids.get(employee_position);
                    String employeeName = employee_names.get(employee_position);
                    Intent intent = new Intent(ChatAcivity.this,SupportActivity.class);
                    SupportActivity.VIEW_MODE = VIEW_MODE;
                    Chat chat = new Chat(client.getClientID(),client.getClientName(),employeeID,employeeName,new ArrayList<Message>());
                    intent.putExtra("ViewChat",chat);
                    startActivityForResult(intent,1);

                }
            });

        }
        else if(VIEW_MODE == VIEW_MODE_EMPLOYEE){
            employee = LoginActivity.getLoggedInEmployee();

            chats = getChatsEmployee(employee.getEmp_id());
            allChatsAdapter.setChats(chats);
            allChatsAdapter.setVIEW_MODE(VIEW_MODE_EMPLOYEE);
            allChatsAdapter.notifyDataSetChanged();

        }

        rv_allChats.setAdapter(allChatsAdapter);




    }

    private void saveChanges(String employeeID, Chat chat) {

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

                String key = "client:" + chat.getClientID() + ",employee:" + employeeID;

                saveFile.remove(key);


            }

            else {
                saveFile = new HashMap<>();
            }

            FileOutputStream fos = this.openFileOutput("chats.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            Log.d(TAG, "onPause: " + this.getFilesDir());


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

    private ArrayList<Chat> getChatsEmployee(String emp_id) {

        HashMap<String,Chat> saveFile;
        ArrayList<Chat> chats = new ArrayList<>();
        File file = new File(this.getFilesDir() + "/chats.ser");
        if(file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.getFilesDir() + "/chats.ser");
                ObjectInputStream in = new ObjectInputStream(fileInputStream);

                saveFile = (HashMap<String, Chat>) in.readObject();

                for (String name : saveFile.keySet()) {
                    Log.d(TAG, "getChatsClient: " + name);
                    if (name.split(",")[1].split(":")[1].equalsIgnoreCase(emp_id)) {
                        chats.add(saveFile.get(name));
                    }

                    // do whatever you want do as key will be match pattern to reach this code.
                }
                in.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException i) {
                i.printStackTrace();
            }
        }
        return chats;
    }

    private ArrayList<Chat> getChatsClient(String clientID) {

        HashMap<String,Chat> saveFile;
        ArrayList<Chat> chats = new ArrayList<>();
        File file = new File(this.getFilesDir() + "/chats.ser");
        if(file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.getFilesDir() + "/chats.ser");
                ObjectInputStream in = new ObjectInputStream(fileInputStream);

                saveFile = (HashMap<String, Chat>) in.readObject();

                for (String name : saveFile.keySet()) {
                    Log.d(TAG, "getChatsClient: " + name);
                    if (name.split(",")[0].split(":")[1].equalsIgnoreCase(clientID)) {
                        chats.add(saveFile.get(name));
                    }

                    // do whatever you want do as key will be match pattern to reach this code.
                }
                in.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException i) {
                i.printStackTrace();
            }
        }

        Log.d(TAG, "getChatsClient: " + chats.size());

        return chats;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent refresh = new Intent(this, ChatAcivity.class);
        startActivity(refresh);
        this.finish();

    }
}
