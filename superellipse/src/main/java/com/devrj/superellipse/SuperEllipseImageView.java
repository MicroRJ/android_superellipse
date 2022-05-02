package com.devrj.superellipse;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.devrj.superellipse.SuperEllipse.Shape.SQUIRCLE;
import static com.devrj.superellipse.SuperEllipse.debugTimed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.devrj.superellipse.SuperEllipse.Rune;

/** TODO(RJ):
 *  - Glyph caching!
 */
public class SuperEllipseImageView extends AppCompatImageView
{ private final static SuperEllipse SUPER_ELLIPSE = new SuperEllipse();

  public SuperEllipseImageView(Context context)
  { super(context);
    init(null);
  }
  public SuperEllipseImageView(Context context, @Nullable AttributeSet attrs)
  { super(context, attrs);
    init(attrs);
  }
  public SuperEllipseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
  { super(context, attrs, defStyleAttr);
    init(attrs);
  }
  
  private int viewWidth;
  private int viewHeight;
  
  private boolean isReady;
 
  public int shapeBackgroundColor;
  public int shapeForegroundColor;
  public float shapeBorderWidth;
  public float shapeCurveFactor;
  public float shapeRadius;
  public float shapeScale;

  public void setShapeForegroundColor(int foregroundColor)
  { this.shapeForegroundColor = foregroundColor;
  }
  public void setShapeBorderWidth(float borderWidth)
  { this.shapeBorderWidth = borderWidth;
  }
  public void setShapeCurveFactor(float curveFactor)
  { this.shapeCurveFactor = curveFactor;
  }
  public void setShapeRadius(float shapeRadius)
  { this.shapeCurveFactor = shapeRadius;
  }
  public void setShapeScale(float shapeScale)
  { this.shapeCurveFactor = shapeScale;
  }
  public void resetShape()
  { shapeBackgroundColor = 0xFFEEEEEE;
    shapeForegroundColor = 0xFFDDDDDD;
    shapeBorderWidth = 64.f;
    shapeCurveFactor = SQUIRCLE.getCurveFactor();
    shapeRadius      = MATCH_PARENT;
    shapeScale       = 1.f;
  }
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  { super.onSizeChanged(w, h, oldw, oldh);
    if (w != 0 && h != 0)
    { viewWidth  = w;
      viewHeight = h;
      isReady    = true;
      invalidate();
    }
  }
  public void init(AttributeSet attrs)
  {  resetShape();
    if (attrs != null)
    { TypedArray styleArray = getContext().obtainStyledAttributes(attrs, R.styleable.SuperEllipseImageView);
      shapeBackgroundColor = styleArray.getColor(R.styleable.SuperEllipseImageView_shapeBackgroundColor, shapeBackgroundColor);
      shapeForegroundColor = styleArray.getColor(R.styleable.SuperEllipseImageView_shapeForegroundColor, shapeForegroundColor);
      shapeBorderWidth = styleArray.getDimension(R.styleable.SuperEllipseImageView_shapeBorderWidth, shapeBorderWidth);
      shapeCurveFactor = styleArray.getFloat(R.styleable.SuperEllipseImageView_shapeCurveFactor, shapeCurveFactor);
      shapeRadius = styleArray.getDimension(R.styleable.SuperEllipseImageView_shapeRadius, shapeRadius);
      shapeScale = styleArray.getFloat(R.styleable.SuperEllipseImageView_shapeScale, shapeScale);
      styleArray.recycle();
    }
  }
  @Override
  public void draw(Canvas canvas)
  { super.draw(canvas);
  }
  @Override
  protected void onDraw(Canvas canvas)
  {
    if (isReady)
    { if (shapeRadius == MATCH_PARENT)
      { final int realViewWidth = viewWidth-(getPaddingLeft()+getPaddingRight());
        final int realViewHeight = viewHeight-(getPaddingTop()-getPaddingBottom());
        shapeRadius = Math.min(realViewWidth, realViewHeight);
        shapeRadius /= 2.f;
        shapeRadius *= shapeScale;
      }
      assert shapeRadius > 0;
      canvas.save();
      canvas.translate(viewWidth/2.f, viewHeight/2.f);
      SUPER_ELLIPSE.drawSuperEllipse(canvas, shapeCurveFactor, shapeRadius, shapeRadius, shapeBackgroundColor, shapeForegroundColor, shapeBorderWidth);
      canvas.restore();
    }
    // NOTE(RJ):
    // ;
    super.onDraw(canvas);
  }
  @Override
  protected void onDetachedFromWindow()
  { super.onDetachedFromWindow();
  }
}
