package com.rayject.tableview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
        mTableView.setLayoutChagneListener(new TableView.LayoutChagneListener() {
            @Override
            public void onLayoutChange(View v, boolean changed, int left, int top, int right, int bottom) {
                if(changed) {
                    int tableViewWidth = mTableView.getWidth();
                    int colWidth = tableViewWidth / 4;
                    colWidth = colWidth * 3 / 4;
                    DefaultSheetData sheetData = (DefaultSheetData) mTableView.getSheet();
                    for(int i = 0; i < sheetData.getMaxColumnCount(); i++) {
                        sheetData.setColumnWidth(i, colWidth);
                    }
                    calcRowHeight(sheetData);
                    mTableView.clearCacheData();

                }
            }
        });


        ISheetData sheet = SheetTemplate1.get(this, rowCount, colCount);

        mTableView.setSheetData(sheet);
        TableViewConfigure configure = new TableViewConfigure();
        configure.setShowHeaders(true);
        configure.setEnableResizeRow(true);
        configure.setEnableResizeColumn(true);
        mTableView.setConfigure(configure);
    }

    private void calcRowHeight(DefaultSheetData sheet) {
        int rowCount = sheet.getMaxRowCount();
        int colCount = sheet.getMaxColumnCount();
        for(int i = 0; i < rowCount; i++) {
            int rowHeight = 0;
            for(int j = 0; j < colCount; j++) {
                BaseCellData cell = (BaseCellData)sheet.getCellData(i, j);
                int cellHeight = cell.calcTextHeightByWidth(sheet.getColumnWidth(j));
                rowHeight = Math.max(rowHeight, cellHeight);
            }
            sheet.setRowHeight(i, rowHeight);
        }

    }

}
