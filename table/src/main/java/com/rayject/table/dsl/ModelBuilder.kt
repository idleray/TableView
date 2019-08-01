package com.rayject.table.dsl

import android.content.Context
import android.graphics.drawable.Drawable
import com.rayject.table.model.*
import com.rayject.table.model.`object`.CellObject
import com.rayject.table.model.`object`.DrawableObject
import com.rayject.table.model.style.CellStyle
import com.rayject.table.model.style.Font

@DslMarker
annotation class TableViewMarker


fun DefaultSheetData.font(init: Font.()->Unit): Int {
    val font = Font.createDefault(context)
    font.init()
    return fontManager.addFont(font)
}

fun DefaultSheetData.cellStyle(init: CellStyle.() -> Unit): Int {
    val style = CellStyle()
    style.init()
    return cellStyleManager.addCellStyle(style)
}

fun DefaultSheetData.cell(row: Int, column: Int, init: DefaultCellData.() -> Unit) {
    val cell = DefaultCellData(this)
    cell.init()
    setCellData(cell, row, column)
}

fun DefaultSheetData.merge(init: Range.() -> Unit) {
    val range = Range()
    range.init()
    addMergedRange(range)
}

fun DefaultCellData.value(init: RichText.() -> Unit) {
    val richText = RichText()
    richText.init()
    setCellValue(richText)
}

fun DefaultCellData.drawableObject(d: Drawable, init: DrawableObject.() -> Unit) {
    val drawableObject = DrawableObject(this, d)
    drawableObject.init()
    addObject(drawableObject)

}

fun RichText.textRun(init: TextRun.() -> Unit) {
    val run = TextRun()
    run.init()
    addRun(run)

}

fun TextRun.onAction(action: (ICellData) -> Boolean) {
    setAction(action)
}

fun CellObject.onClick(click: (CellObject) -> Boolean) {
    setOnClickListener(click)

}

fun sheet(context: Context, init: DefaultSheetData.() -> Unit): DefaultSheetData {
    val sheet = DefaultSheetData(context)
    sheet.init()
    return sheet
}
