package com.devrj.superellipseexample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.devrj.superellipse.SuperEllipseImageView;

public class MainActivity extends AppCompatActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    SuperEllipseImageView dummy = new SuperEllipseImageView(this);
    
    dummy.setShapeBorderWidth(10.f);
    assert dummy.shapeBorderWidth == 10.f;
    
    dummy.setShapeRadius(10.f);
    assert dummy.shapeRadius == 10.f;
    
    dummy.setShapeCurveFactor(10.f);
    assert dummy.shapeCurveFactor == 10.f;
  
    dummy.setShapeScale(10.f);
    assert dummy.shapeScale == 10.f;
    
    dummy.setShapeForegroundColor(10);
    assert dummy.shapeForegroundColor == 10;
    
    dummy.setShapeBackgroundColor(10);
    assert dummy.shapeBackgroundColor == 10;
  }
}