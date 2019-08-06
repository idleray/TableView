package com.rayject.tableview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

import com.rayject.table.model.BaseCellData;
import com.rayject.table.model.CellStyleManager;
import com.rayject.table.model.DefaultSheetData;
import com.rayject.table.model.FontManager;
import com.rayject.table.model.ICellData;
import com.rayject.table.model.ISheetData;
import com.rayject.table.model.Range;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.Font;
import com.rayject.table.model.style.TableConst;
import com.rayject.table.util.TableViewConfigure;
import com.rayject.table.view.TableView;


public class MainActivity extends AppCompatActivity {
    int rowCount = 40;
    int colCount = 15;
    TableView mTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTableView = (TableView) findViewById(R.id.table_view);


//        ISheetData sheet = SheetTemplate1.get(this, rowCount, colCount);
        ISheetData sheet = DslSampleKt.dslData(this, rowCount, colCount);
//        ISheetData sheet = DslSampleKt.simpleData(this, rowCount, colCount);

        mTableView.setSheetData(sheet);
        TableViewConfigure configure = new TableViewConfigure();
        configure.setShowHeaders(true);
        configure.setEnableResizeRow(true);
        configure.setEnableResizeColumn(true);
        configure.setEnableSelection(true);
        mTableView.setConfigure(configure);
    }

}
