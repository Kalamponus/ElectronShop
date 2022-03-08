package com.example.electronicshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrdersOutput extends AppCompatActivity implements View.OnClickListener {
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderslist);
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        UpdateTable();
    }

    public String GetOrderGoods(String orderId)
    {
        String orderedGoodsList = "";
        Cursor c = database.rawQuery("SELECT idListProd, quantity FROM productList WHERE idListOrder = " + orderId, null);
        if (c.moveToFirst())
        {
            do
            {
                String goodId = c.getString(0);
                String quantity = c.getString(1);
                Cursor c2= database.rawQuery("SELECT prodName FROM products WHERE idProd = " + goodId, null);
                if (c2.moveToFirst())
                {
                    do
                    {
                        String goodName = c2.getString(0);
                        orderedGoodsList += goodName + " x " + quantity +";\n";
                    }
                    while(c2.moveToNext());
                }
                c2.close();
            }
            while(c.moveToNext());
        }
        c.close();
        return orderedGoodsList;
    }

    public void UpdateTable()

                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

    TextView outputID = new TextView(this);
    params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);
    {
        Cursor cursor = database.query(DBHelper.TABLE_ORDERS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ORDERID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_CUSTOMERNAME);
            int addressIndex = cursor.getColumnIndex(DBHelper.KEY_ADDREESS);
            int phoneIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
            int costIndex = cursor.getColumnIndex(DBHelper.KEY_COST);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do{
                TableRow dbOutputRow = new TableRow(this);

                TextView outputName = new TextView(this);
                params.weight = 3.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputName);

                TextView outputAddress = new TextView(this);
                params.weight = 3.0f;
                outputAddress.setLayoutParams(params);
                outputAddress.setText(cursor.getString(addressIndex));
                dbOutputRow.addView(outputAddress);

                TextView outputPhone = new TextView(this);
                params.weight = 3.0f;
                outputPhone.setLayoutParams(params);
                outputPhone.setText(cursor.getString(phoneIndex));
                dbOutputRow.addView(outputPhone);

                TextView outputCost = new TextView(this);
                params.weight = 3.0f;
                outputCost.setLayoutParams(params);
                outputCost.setText(cursor.getString(costIndex));
                dbOutputRow.addView(outputCost);

                TextView outputProd = new TextView(this);
                params.weight = 3.0f;
                outputProd.setLayoutParams(params);
                String id = cursor.getString(idIndex);
                outputProd.setText(GetOrderGoods(id));
                dbOutputRow.addView(outputProd);

                Button buttonDelete = new Button(this);
                buttonDelete.setOnClickListener(this);
                params.weight = 1.0f;
                buttonDelete.setLayoutParams(params);
                buttonDelete.setText("Удалить заказ");
                buttonDelete.setTag("delete");
                buttonDelete.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(buttonDelete);

                dbOutput.addView(dbOutputRow);
            }
            while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");

        cursor.close();
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() == "delete") {
            View outputDBRow = (View) v.getParent();
            ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
            outputDB.removeView(outputDBRow);
            outputDB.invalidate();
            database.delete(dbHelper.TABLE_ORDERS, dbHelper.KEY_ORDERID + " = ?", new String[]{String.valueOf(v.getId())});
            contentValues = new ContentValues();
            Cursor cursorUpdater = database.query(DBHelper.TABLE_ORDERS, null, null, null, null, null, null);
            if (cursorUpdater.moveToFirst()) {
                int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ORDERID);
                int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_CUSTOMERNAME);
                int addressIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ADDREESS);
                int phoneIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_PHONE);
                int costIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_COST);

                int realID = 1;
                do {
                    if (cursorUpdater.getInt(idIndex) > realID) {
                        contentValues.put(dbHelper.KEY_ORDERID, realID);
                        contentValues.put(dbHelper.KEY_CUSTOMERNAME, cursorUpdater.getString(nameIndex));
                        contentValues.put(dbHelper.KEY_ADDREESS, cursorUpdater.getString(addressIndex));
                        contentValues.put(dbHelper.KEY_PHONE, cursorUpdater.getString(phoneIndex));
                        contentValues.put(dbHelper.KEY_COST, cursorUpdater.getString(costIndex));
                        database.replace(dbHelper.TABLE_ORDERS, null, contentValues);
                    }
                    realID++;
                }
                while (cursorUpdater.moveToNext());
                if (cursorUpdater.moveToLast()) {
                    if (cursorUpdater.moveToLast() && v.getId() != realID) {
                        database.delete(dbHelper.TABLE_ORDERS, dbHelper.KEY_ORDERID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                }
                Cursor c = database.rawQuery("DELETE FROM productList WHERE idListOrder = " + cursorUpdater.getString(idIndex), null);
                UpdateTable();
            }
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
