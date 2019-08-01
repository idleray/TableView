package com.rayject.tableview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.rayject.table.model.DefaultCellData;
import com.rayject.table.model.DefaultSheetData;
import com.rayject.table.model.ICellData;
import com.rayject.table.model.ISheetData;
import com.rayject.table.model.Range;
import com.rayject.table.model.RichText;
import com.rayject.table.model.TextRun;
import com.rayject.table.model.action.Action;
import com.rayject.table.model.object.CellObject;
import com.rayject.table.model.object.DrawableObject;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.Font;
import com.rayject.table.model.style.TableConst;

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
                if(i == 2) {
                    String text = "cell-" + i + "-" + j;
                    if(j == 0) {
                        Drawable d = context.getResources().getDrawable(R.drawable.vip_star);
                        DrawableObject bo = new DrawableObject(cell, d);
                        bo.setOnClickListener(new CellObject.OnClickListener() {
                            @Override
                            public boolean onClick(CellObject cellObject) {
                                Toast.makeText(context, "click DrawableObject", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
//                        bo.setAlignment(TableConst.ALIGNMENT_CENTER);
                        bo.setAlignment(TableConst.ALIGNMENT_RIGHT);
                        bo.setVerticalAlignment(TableConst.VERTICAL_ALIGNMENT_CENTRE);
//                        bo.setVerticalAlignment(TableConst.VERTICAL_ALIGNMENT_BOTTOM);
                        cell.addObject(bo);

                        TextRun tr = new TextRun();
                        tr.setStartPos(0);
                        tr.setLength("cell".length());
                        tr.setBackgroundColor(0xffff0000);
                        tr.setAction(new Action() {
                            @Override
                            public boolean onAction(ICellData cell) {
                                Toast.makeText(context, "click on text", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                        richText.addRun(tr);
                    }
                    cell.setStyleIndex(frStyleIndex);
                    richText.setText(text);
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

        addMergeRange(sheet);

        return sheet;
    }

    private static void addMergeRange(DefaultSheetData sheet) {
        Range range = new Range(0, 2, 1, 3);
        sheet.addMergedRange(range);
    }
}
