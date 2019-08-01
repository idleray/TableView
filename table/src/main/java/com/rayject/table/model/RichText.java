package com.rayject.table.model;


import java.util.ArrayList;
import java.util.List;

public class RichText implements IRichText{
    private String text;
    private List<ITextRun> textRuns;

    @Override
    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence textValue) {
        text = textValue != null ? textValue.toString() : "";
    }


    @Override
    public int getRunCount() {
        return textRuns != null ? textRuns.size() : 0;
    }

    @Override
    public ITextRun getRun(int index) {
        return textRuns.get(index);
    }

    public void addRun(ITextRun tr) {
        if(textRuns == null) {
            textRuns = new ArrayList<>();
        }
        textRuns.add(tr);
    }
}
