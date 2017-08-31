package com.rayject.tableview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.rayject.table.model.DefaultCellData;
import com.rayject.table.model.DefaultSheetData;
import com.rayject.table.model.ISheetData;
import com.rayject.table.model.RichText;
import com.rayject.table.model.object.CellObject;
import com.rayject.table.model.object.DrawableObject;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.Font;
import com.rayject.table.model.style.TableConst;
import com.rayject.table.util.ConstVar;

public class SheetTemplate1 {

    public static ISheetData get(final Context context, int rowCount, int colCount) {
        DefaultSheetData sheet = new DefaultSheetData(context);
        sheet.setMaxRowCount(rowCount);
        sheet.setMaxColumnCount(colCount);
        sheet.setFreezedRowCount(1);

        CellStyle firstRowStyle = new CellStyle();
        firstRowStyle.setBgColor(0xffff8c5d);
        firstRowStyle.setAlignment(TableConst.ALIGNMENT_CENTER);
        firstRowStyle.setVerticalAlignment(TableConst.VERTICAL_ALIGNMENT_CENTRE);

        Font firstRowFont = Font.createDefault(context);
        firstRowFont.setColor(0xffffffff);
        int frIndex = sheet.getFontManager().addFont(firstRowFont);
        firstRowStyle.setFontIndex(frIndex);
        int frStyleIndex = sheet.getCellStyleManager().addCellStyle(firstRowStyle);

        CellStyle cellStyle = new CellStyle();
        cellStyle.setBgColor(0xfffbe8d4);
        cellStyle.setAlignment(TableConst.ALIGNMENT_CENTER);
        cellStyle.setVerticalAlignment(TableConst.VERTICAL_ALIGNMENT_CENTRE);
        int oddRowStyleIndex = sheet.getCellStyleManager().addCellStyle(cellStyle);

        for(int i = 0; i < rowCount; i++) {
            for(int j = 0; j < colCount; j++) {
                DefaultCellData cell = new DefaultCellData(sheet);
                RichText richText = new RichText();
                if(i == 0) {
//                    if(j == 0) {
//                        Drawable d = getResources().getDrawable(R.drawable.unread);
//                        DrawableObject bo = new DrawableObject(cell, d);
//                        bo.setOnClickListener(new CellObject.OnClickListener() {
//                            @Override
//                            public boolean onClick(CellObject cellObject) {
//                                Toast.makeText(context, "click DrawableObject", Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
//                        });
//                        bo.setAlignment(TableConst.ALIGNMENT_CENTER);
//                        bo.setAlignment(TableConst.ALIGNMENT_RIGHT);
//                        bo.setvAlignment(TableConst.VERTICAL_ALIGNMENT_CENTRE);
//                        bo.setvAlignment(TableConst.VERTICAL_ALIGNMENT_BOTTOM);
//                        cell.addObject(bo);
//                    }
                    cell.setStyleIndex(frStyleIndex);
                    richText.setText("cell-" + i + "-" + j);
                } else {
                    if(i % 2 == 0) {
                        cell.setStyleIndex(oddRowStyleIndex);
                    }
                    richText.setText("cell-" + i + "-" + j);
                }
                cell.setCellValue(richText);
                sheet.setCellData(cell, i, j);
            }
        }

        return sheet;
    }
}
