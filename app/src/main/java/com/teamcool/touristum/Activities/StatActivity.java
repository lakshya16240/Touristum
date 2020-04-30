package com.teamcool.touristum.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Employee;

public class StatActivity extends AppCompatActivity {


    private TextView tv_avg, tv_std, tv_var;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private Employee employee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        tv_avg = findViewById(R.id.tv_avgSalary);
        tv_std = findViewById(R.id.tv_stdDev);
        tv_var = findViewById(R.id.tv_variance);

        mDbHelper = new DatabaseHelper(this);
        mDb = mDbHelper.getReadableDatabase();

        employee = LoginActivity.getLoggedInEmployee();

        String avg = getAvg(employee);
        tv_avg.setText("Avg Salary : " + avg);

        String var = getVariance(employee);
        tv_var.setText("Variance : " +  var);

        tv_std.setText("Std Dev : " + String.valueOf(Math.sqrt(Double.parseDouble(var))));
    }

    private String getAvg(Employee employee) {
        String sql = "";
        if(employee.getEmp_type().equalsIgnoreCase("manager")) {
            sql = "SELECT avg(employeeSalary) \n" +
            " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + employee.getEmp_id() + "' and employeeType != 'manager' and employeeType != 'CEO' and e.branchID = '" + employee.getBranchID() + "';";
        }
        else if(employee.getEmp_type().equalsIgnoreCase("CEO")){
            sql = "SELECT avg(employeeSalary) \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + employee.getEmp_id() + "' and employeeType != 'CEO' ;";
        }

        Cursor cur = mDb.rawQuery(sql,null);
        String avg = ";";
        while(cur!=null && cur.moveToNext()){
            avg = cur.getString(0);
        }
        return avg;
    }

    private String getVariance(Employee employee) {
        String sql = "";
        if(employee.getEmp_type().equalsIgnoreCase("manager")) {
            sql = "Select avg((e2.employeeSalary - sub.a)*(e2.employeeSalary - sub.a)) as var from employee e2, " +
                    "(SELECT avg(employeeSalary) as a \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + employee.getEmp_id() + "' and employeeType != 'manager' and employeeType != 'CEO' and e.branchID = '" + employee.getBranchID() + "') as sub;";
        }
        else if(employee.getEmp_type().equalsIgnoreCase("CEO")){
            sql = "Select avg((e2.employeeSalary - sub.a)*(e2.employeeSalary - sub.a)) as var from employee e2, " +
                    "(SELECT avg(employeeSalary) as a \n" +
                    " FROM employee e,branch b " +
                    "where e.BranchID=b.BranchID and e.employeeID != '" + employee.getEmp_id() + "' and employeeType != 'CEO') as sub;";
        }

        Cursor cur = mDb.rawQuery(sql,null);
        String var = ";";
        while(cur!=null && cur.moveToNext()){
            var = cur.getString(0);
        }
        return var;
    }

}
