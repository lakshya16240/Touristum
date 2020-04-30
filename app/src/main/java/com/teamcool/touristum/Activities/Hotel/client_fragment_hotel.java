package com.teamcool.touristum.Activities.Hotel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.ClientAdapter;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.data.model.Hotel;

import java.util.ArrayList;
import java.util.List;

public class client_fragment_hotel extends Fragment implements ClientAdapter.onClientClickListner{
    private View v;

    private ClientAdapter clientAdapter;
    private List<Client> clients;
    private RecyclerView recyclerView;
    private EditText search_bar,edit_filter;
    private Hotel hotel;

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private View dialog_view;

    private String[] filter_options;
    private AlertDialog.Builder builder;
    private ArrayList<Filter> client_filter;

    private TextView textView;
    private ImageButton imageButton;
    private Spinner spinner;
    private Button button;
    private ChipGroup chipGroup;

    public static final String TAG = "ClientFragmentHotel";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.hotel_client,container,false);
        recyclerView=v.findViewById(R.id.client_recycler_hotel);
        search_bar=v.findViewById(R.id.search_client_hotel);search_bar.setFocusable(false);
        hotel= LoginActivity.getLoggedInHotel();
        helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();

        clients=getClients();
        client_filter=new ArrayList<>();
        builder = new AlertDialog.Builder(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clientAdapter=new ClientAdapter(clients, getContext(), this);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_view=getLayoutInflater().inflate(R.layout.search_filter,null);
                spinner=dialog_view.findViewById(R.id.sp_filter1);
                edit_filter=dialog_view.findViewById(R.id.et_filter1);
                imageButton=dialog_view.findViewById(R.id.bv_filter1);
                chipGroup=dialog_view.findViewById(R.id.cg_filters1);
                addChips();
                filter_options=new String[]{"ClientID","ClientName","ClientAddress","ClientContact","ClientEmail"};
                builder.setTitle("Filter Clients");
                launchDialog();
            }
        });
        //setadapter
        recyclerView.setAdapter(clientAdapter);
        return v;
    }

    private void launchDialog() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filter_options);
        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String type = filter_options[position];
                edit_filter.setHint(type);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edit_filter.getText().length() != 0){
                            Filter filter = new Filter(type,edit_filter.getText().toString(),false);
                            client_filter.add(filter);
                            final Chip chip = new Chip(getContext());
                            chip.setCloseIconVisible(true);
                            chip.setText(type + ":" + filter.getFilter());
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String text = chip.getText().toString().split(":")[1];
                                    String type = chip.getText().toString().split(":")[0];
                                    client_filter.remove(new Filter(type,text,false));
                                    chipGroup.removeView(chip);
                                }
                            });
                            chipGroup.addView(chip);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setView(dialog_view);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateData();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addChips() {
        int size=client_filter.size();
        for(int i=0;i<size;i++){
            final Chip chip = new Chip(chipGroup.getContext());
            chip.setCloseIconVisible(true);
            chip.setText(client_filter.get(i).getType() + ":" + client_filter.get(i).getFilter());
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = chip.getText().toString().split(":")[1];
                    String type = chip.getText().toString().split(":")[0];
                    client_filter.remove(new Filter(type,text,false));
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
    }
    private List<Client> getClients() {
        ArrayList<Client> c=new ArrayList<>();
        String sql="SELECT cl.clientID, clientName, clientContact, clientAddress, clientEmail, count(b.bookingID)"+
                "FROM  Booking b, hotelinformation hi, Client cl "+
                "where b.HotelID=hi.HotelID and cl.ClientID=b.ClientID and hi.HotelID = '"+hotel.getHotelID()+"' ;";
        Cursor cur = db.rawQuery(sql, null);
        Client cli=null;
        while (cur != null && cur.moveToNext()) {
            cli = new Client(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5));
            c.add(cli);
        }
        return c;
    }

    @Override
    public void selectedClient(int position) {
//        final Client curr=clients.get(position);
//        final View view = getLayoutInflater().inflate(R.layout.hotel_client_popup, null);
//        textView=view.findViewById(R.id.hotel_client_pop);
//
//        textView.setText("ClientName: "+curr.getClientName());
//        button=view.findViewById(R.id.pop_delete);
//        builder.setView(view);
//        final AlertDialog dialog;
//        dialog=builder.create();
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.delete("Client","ClientID = ?",new String[]{curr.getClientID()});
//                updateData();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    private void updateData() {
        ArrayList<Client> clie=getFilteredClients(client_filter);
        clientAdapter.setClients(clie);
        clientAdapter.notifyDataSetChanged();
    }
    private ArrayList<Client> getFilteredClients(ArrayList<Filter> client_filter) {
        String sql = generateSqlClients(client_filter);
        Cursor cur=db.rawQuery(sql,null);
        ArrayList<Client> Client=new ArrayList<>();
        while(cur!=null && cur.moveToNext()){
            Client cc=new Client(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    "");
            Client.add(cc);
        }

        Log.d(TAG, "getFilteredClients: " + Client.size());
        return Client;
    }

    private String generateSqlClients(ArrayList<Filter> client_filter) {
        String sql="SELECT cl.clientID, clientName, clientContact, clientAddress, clientEmail "+
                "FROM  Booking b, hotelinformation hi, Client cl "+
                "where b.HotelID=hi.HotelID and cl.ClientID=b.ClientID and hi.HotelID = '"+hotel.getHotelID()+"' ";
        for(int i=0;i<client_filter.size();i++){
            if(i>=1){
                sql=sql+"or ";
            }
            if(i==0){
                sql=sql+"and (";
            }
            if(client_filter.get(i).getType().equalsIgnoreCase("ClientID")){
                sql=sql+" cl.ClientID = '" + client_filter.get(i).getFilter() + "'" ;
            }
            else if(client_filter.get(i).getType().equalsIgnoreCase("ClientName")){
                sql=sql+" cl.ClientName = '" + client_filter.get(i).getFilter() +"'";
            }
            else if(client_filter.get(i).getType().equalsIgnoreCase("ClientContact")){
                sql=sql+" cl.ClientContact = '" + client_filter.get(i).getFilter() +"'";
            }
            else if(client_filter.get(i).getType().equalsIgnoreCase("ClientAddress")){
                sql=sql+" cl.ClientAddress = '" + client_filter.get(i).getFilter() +"'";
            }
            else if(client_filter.get(i).getType().equalsIgnoreCase("ClientEmail")){
                sql=sql+" cl.ClientEmail = '" + client_filter.get(i).getFilter() +"'";
            }
            if (i == client_filter.size() - 1){
                sql=sql+");";
            }
        }

        Log.d(TAG, "generateSqlClients: "+sql);
        return sql;
    }


}
