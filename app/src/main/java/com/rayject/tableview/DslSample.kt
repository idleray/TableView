package com.rayject.tableview

import android.content.Context
import android.widget.Toast
import com.rayject.table.dsl.*
import com.rayject.table.model.DefaultSheetData
import com.rayject.table.model.ISheetData
import com.rayject.table.model.style.BorderLineStyle
import com.rayject.table.model.style.Font
import com.rayject.table.model.style.TableConst
import com.rayject.table.util.ExcelUtils

fun simpleData(context: Context, rowCount: Int, colCount: Int): ISheetData {
    return sheet(context) {
        maxRowCount = rowCount
        maxColumnCount = colCount

        val fi = font {
            color = 0xffff0000.toInt()
        }
        val f2 = font {
            typeOffset = Font.SS_SUB
        }
        val f3 = font {
            isUnderLine = true
            isStrikeLine = true
        }

        val si = cellStyle {
            fontIndex = fi
            alignment = TableConst.ALIGNMENT_CENTER
            border {
                type = BorderLineStyle.BORDER_DOT
                color = 0xffff0000.toInt()
                width = BorderLineStyle.BORDER_MEDIUMWIDTH
            }

            // set border separately
            leftBorder {
                type = BorderLineStyle.BORDER_DASH
                color = 0xffff00ff.toInt()
                width = BorderLineStyle.BORDER_MEDIUMWIDTH
            }
        }

        val s2 = cellStyle {
            fontIndex = f3
        }

        cell(0, 0) {
            value {
                text = "cell-1"
            }
        }

        cell(0, 1) {
            styleIndex = s2
            value {
                text = "00000000"
            }
        }

        cell(1, 1) {
            styleIndex = si
            value {
                text = "cell-2"
                textRun {
                    startPos = 5
                    length = 1
                    fontIndex = f2
                }
            }
        }
    }

}

fun dslData(context: Context, rowCount: Int, colCount: Int): ISheetData {
    val data = sheet(context) {
        val fontIndex1 = font {
            color = 0xffffffff.toInt()
        }

        val frStyleIndex = cellStyle {
            fontIndex = fontIndex1
            bgColor = 0xffff8c5d.toInt()
            alignment = TableConst.ALIGNMENT_CENTER
            verticalAlignment = TableConst.VERTICAL_ALIGNMENT_CENTRE
            indention = 0
            isAutoWrap = true
        }
        val oddRowStyleIndex = cellStyle {
            bgColor = 0xfffbe8d4.toInt()
            alignment = TableConst.ALIGNMENT_CENTER
            verticalAlignment = TableConst.VERTICAL_ALIGNMENT_CENTRE
        }

        maxRowCount = rowCount
        maxColumnCount = colCount
        freezedRowCount = 1

        for(i in 0..maxRowCount) {
            for(j in 0..maxColumnCount) {
                cell(i, j) {
                    val textInCell = "cell-${i+1}-${ExcelUtils.columnToString(j)}"
                    if(i == 2) {
                        styleIndex = frStyleIndex
                        if(j == 0) {
                            value {
                                text = textInCell
                                textRun {
                                    startPos = 0
                                    length = "cell".length
                                    backgroundColor = 0xffff0000.toInt()
                                    onAction {
                                        Toast.makeText(context, "click on text", Toast.LENGTH_SHORT).show()
                                        true
                                    }

                                }
                            }
                            drawableObject(context.resources.getDrawable(R.drawable.vip_star)) {
                                alignment = TableConst.ALIGNMENT_RIGHT
                                verticalAlignment = TableConst.VERTICAL_ALIGNMENT_CENTRE
                                onClick {
                                    Toast.makeText(context, "click DrawableObject", Toast.LENGTH_SHORT).show()
                                    true
                                }

                            }

                        } else {
                            value {
                                text = textInCell
                            }
                        }

                    } else {
                        if (i % 2 == 0) {
                            styleIndex = oddRowStyleIndex
                        }
                        value {
                            text = textInCell
                        }
                    }
                }
            }
        }

        merge {
            left = 0
            top = 2
            right = 1
            bottom = 3
        }

    }

    return data
}
