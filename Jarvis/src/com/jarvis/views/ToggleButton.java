package com.jarvis.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

public class ToggleButton extends Button {
	
	public boolean isToggled = false;
	
	private final int DEFAULT_BACKGROUND_COLOR = 0x80ffffff;
	private final int DEFAULT_TEXT_COLOR = Color.BLACK;
	
	private final int SELECTED_BACKGROUND_COLOR = 0x80000000;
	private final int SELECTED_TEXT_COLOR = Color.WHITE;
	
    public ToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleButton(Context context) {
        super(context);
    }
    
    @Override
    public boolean performClick() {
    	isToggled = !isToggled;
    	
    	if (isToggled)
    	{
        	this.setBackgroundColor(SELECTED_BACKGROUND_COLOR);
        	this.setTextColor(SELECTED_TEXT_COLOR);
    	}
    	else
    	{
    		this.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
    		this.setTextColor(DEFAULT_TEXT_COLOR);
    	}
    	
    	return super.performClick();
    }
}
