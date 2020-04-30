package com.teamcool.touristum.ui.employees;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.ManagerEmployeeAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Employee;
import com.teamcool.touristum.data.model.Filter;

import java.util.ArrayList;

public class EmployeesFragment extends Fragment {

    private RecyclerView rv_data;
    private ManagerEmployeeAdapter managerEmployeeAdapter;
    private EditText et_search, et_filter, et_search_filter;

    private Employee currEmployee;
    private ArrayList<Employee> employees;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;


    private String[] filter_options;
    private AlertDialog.Builder builder;
    private View dialog_view;
    private Spinner sp_dialog_filter;
    private ChipGroup cg_filters;
    private ImageButton bv_filter;

    public static int VIEW_MODE;
    public static int VIEW_MODE_MANAGER = 1;
    public static int VIEW_MODE_CEO = 2;

    private int SEARCH_MODE;
    public static final int SEARCH_MODE_FILTER = 1;
    public static final int SEARCH_MODE_NON_FILTER = 2;

    private ArrayList<Filter> employee_filters;

    public static final String TAG = "EmployeesFragnent";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_employees, container, false);
//        final TextView textView = root.findViewById(R.id.text_employees);

        rv_data = root.findViewById(R.id.rv_employee_data);
        et_search = root.findViewById(R.id.et_filter_employees);
        et_search_filter = root.findViewById(R.id.et_search_employees);
        et_search.setFocusable(false);
        et_search_filter.setFocusable(false);

        currEmployee = LoginActivity.getLoggedInEmployee();

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        employees = getEmployees();
        employee_filters = new ArrayList<>();

        builder = new AlertDialog.Builder(getContext());

        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));
        managerEmployeeAdapter = new ManagerEmployeeAdapter(employees, getContext(), new ManagerEmployeeAdapter.onEmployeeClickListener() {
            @Override
            public void selectedEmployee(final Employee employee) {
                final View view = getLayoutInflater().inflate(R.layout.update_fire_employee, null);
                builder.setView(view);

                final AlertDialog dialog;

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == DialogInterface.BUTTON_POSITIVE){
                            String newSalary = ((EditText)view.findViewById(R.id.et_newSalary)).getText().toString();
                            updateSalary(newSalary,employee.getEmp_id());
                        }

                    }
                });
                builder.setNegativeButton("Terminate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDb.delete("employee","employeeID = ?",new String[]{employee.getEmp_id()});
                        updateData();
                    }
                });

                dialog = builder.create();
                view.findViewById(R.id.ib_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });
        rv_data.setAdapter(managerEmployeeAdapter);

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_FILTER;
                setup();
            }
        });

        et_search_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_NON_FILTER;
                setup();
            }
        });


        return root;
    }

    private void setup(){
        dialog_view = getLayoutInflater().inflate(R.layout.dialog_filter,null);
        sp_dialog_filter = dialog_view.findViewById(R.id.sp_filter);
        et_filter = dialog_view.findViewById(R.id.et_filter);
        bv_filter = dialog_view.findViewById(R.id.bv_filter);
        cg_filters = dialog_view.findViewById(R.id.cg_filters);

        addChips();

        filter_options = new String[]{"employeeID", "employeeName", "employeeAddress","employeeEmail", "employeeContact", "employeeType", "employeeSalary", "branchName", "branchID"};
        builder.setTitle("Filter Employees");


        launchDialog();
    }

    private void updateSalary(String newSalary, String emp_id) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("EmployeeSalary",newSalary);
        mDb.update("Employee",contentValues,"employeeID = ?",new String[]{emp_id});
        updateData();
    }

    private ArrayList<Employee> getEmployees(){

        ArrayList<Employee> employees = new ArrayList<>();
        String sql;


        Log.d(TAG, "getEmployee: " + currEmployee.getEmp_id());
        if(VIEW_MODE == VIEW_MODE_MANAGER) {
            sql = "SELECT employeeID, employeeName, employeeAddress, employeeEmail, employeeContact, employeeType, employeeSalary, branchName, e.branchID \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + currEmployee.getEmp_id() + "' and employeeType != 'manager' and employeeType != 'CEO' and e.branchID = '" + currEmployee.getBranchID() + "';";
        }
        else{
            sql = "SELECT employeeID, employeeName, employeeAddress, employeeEmail, employeeContact, employeeType, employeeSalary, branchName, e.branchID \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + currEmployee.getEmp_id() + "' and employeeType != 'CEO' ;";
        }
        Cursor cur = mDb.rawQuery(sql, null);

        Employee emp = null;
        while (cur != null && cur.moveToNext()) {

            emp = new Employee(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(5),
                    cur.getString(7),
                    String.format("%.0f",cur.getFloat(4)),
                    cur.getString(6),
                    cur.getString(8));

            employees.add(emp);
        }

        return employees;

    }

    private void addChips() {
        int size = employee_filters.size();
        for(int i=0;i<size;i++){
            final Chip chip = new Chip(cg_filters.getContext());
            chip.setCloseIconVisible(true);
            chip.setText(employee_filters.get(i).getType() + ":" + employee_filters.get(i).getFilter());
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = chip.getText().toString().split(":")[1];
                    String type = chip.getText().toString().split(":")[0];

                    if(SEARCH_MODE == SEARCH_MODE_FILTER)
                        employee_filters.remove(new Filter(type,text,false));
                    else
                        employee_filters.remove(new Filter(type,text,true));

                    cg_filters.removeView(chip);

                }
            });

            cg_filters.addView(chip);

        }
    }

    private void launchDialog(){
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filter_options);
        sp_dialog_filter.setAdapter(filterAdapter);

        sp_dialog_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String type = filter_options[position];

                et_filter.setHint(type);

                bv_filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(et_filter.getText().length() != 0){

                            Filter filter;
                            if(SEARCH_MODE == SEARCH_MODE_FILTER)
                                filter = new Filter(type,et_filter.getText().toString(),false);
                            else
                                filter = new Filter(type,et_filter.getText().toString(),true);

                            employee_filters.add(filter);
                            final Chip chip = new Chip(getContext());
                            chip.setCloseIconVisible(true);
                            chip.setText(type + ":" + filter.getFilter());
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String text = chip.getText().toString().split(":")[1];
                                    String type = chip.getText().toString().split(":")[0];

                                    if(SEARCH_MODE == SEARCH_MODE_FILTER)
                                        employee_filters.remove(new Filter(type,text,false));
                                    else
                                        employee_filters.remove(new Filter(type,text,true));

//                                    booking_filters.remove(new Filter(type,text));
                                    cg_filters.removeView(chip);

                                }
                            });

                            cg_filters.addView(chip);
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

    private void updateData() {

        ArrayList<Employee> employees = getFilteredEmployees(employee_filters);
        managerEmployeeAdapter.setEmployees(employees);
        managerEmployeeAdapter.notifyDataSetChanged();
    }

    private ArrayList<Employee> getFilteredEmployees(ArrayList<Filter> employee_filters) {

        String sql = generateSqlEmployees(employee_filters);
        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Employee> employees = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){

            Employee emp = new Employee(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(5),
                    cur.getString(7),
                    String.format("%.0f",cur.getFloat(4)),
                    cur.getString(6),
                    cur.getString(8));

            employees.add(emp);


        }
        Log.d(TAG, "generated: employees" + employees.size());

        return employees;

    }

    private String generateSqlEmployees(ArrayList<Filter> employee_filters) {
        StringBuilder sql;

        if(VIEW_MODE == VIEW_MODE_MANAGER) {
            sql = new StringBuilder("SELECT employeeID, employeeName, employeeAddress, employeeEmail, employeeContact, employeeType, employeeSalary, branchName, e.branchID \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + currEmployee.getEmp_id() + "' and employeeType != 'manager' and employeeType != 'CEO' and e.branchID = '" + currEmployee.getBranchID() + "';");
        }
        else{
            sql = new StringBuilder("SELECT employeeID, employeeName, employeeAddress, employeeEmail, employeeContact, employeeType, employeeSalary, branchName, e.branchID \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + currEmployee.getEmp_id() + "' and employeeType != 'CEO' ;");
        }

        for(int i=0;i<employee_filters.size();i++){

            if(employee_filters.get(i).isSearch()) {

                sql.append("and ");

                if (employee_filters.get(i).getType().equalsIgnoreCase("employeeID")) {
                    sql.append("e.employeeID = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeName")) {
                    sql.append("e.employeeName = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeAddress")) {
                    sql.append("e.employeeAddress = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeEmail")) {
                    sql.append("e.employeeEmail = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeContact")) {
                    sql.append("e.employeeContact = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeType")) {
                    sql.append("e.employeeType = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeSalary")) {
                    sql.append("e.employeeSalary = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("branchName")) {
                    sql.append("b.branchName = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("branchID")) {
                    sql.append("e.branchID = '" + employee_filters.get(i).getFilter() + "' ");

                }

            }

        }

        boolean inside = false;

        int maxSalary = 0;

        for(int i=0;i<employee_filters.size();i++){
            if(!employee_filters.get(i).isSearch()){
                if(employee_filters.get(i).getType().equalsIgnoreCase("employeeSalary"))
                    if(Integer.parseInt(employee_filters.get(i).getFilter()) > maxSalary)
                        maxSalary = Integer.parseInt(employee_filters.get(i).getFilter());

            }
        }

        boolean alert = false;
        int i=0;

        while(i<employee_filters.size()){
            alert = false;
            if(!employee_filters.get(i).isSearch()){
                if(employee_filters.get(i).getType().equalsIgnoreCase("employeeSalary"))
                    if(Integer.parseInt(employee_filters.get(i).getFilter()) < maxSalary){
                        employee_filters.remove(i);
                        alert = true;
                    }


            }
            if(!alert)
                i++;

        }

        for(i=0;i<employee_filters.size();i++){
            if(!employee_filters.get(i).isSearch()) {

                if (inside) {
                    sql.append("or ");

                }

                if (!inside) {
                    sql.append("and (");
                    inside = true;
                }


                if (employee_filters.get(i).getType().equalsIgnoreCase("employeeID")) {
                    sql.append("e.employeeID = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeName")) {
                    sql.append("e.employeeName = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeAddress")) {
                    sql.append("e.employeeAddress = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeEmail")) {
                    sql.append("e.employeeEmail = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeContact")) {
                    sql.append("e.employeeContact = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeType")) {
                    sql.append("e.employeeType = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("employeeSalary")) {
                    sql.append("e.employeeSalary = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("branchName")) {
                    sql.append("b.branchName = '" + employee_filters.get(i).getFilter() + "' ");

                } else if (employee_filters.get(i).getType().equalsIgnoreCase("branchID")) {
                    sql.append("e.branchID = '" + employee_filters.get(i).getFilter() + "' ");

                }

            }

        }

        if (inside) {
            sql.append(");");
        }
        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

}