package com.devrj.superellipse;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.devrj.superellipse.SuperEllipse.DEFAULT;
import static com.devrj.superellipse.SuperEllipse.Shape.SQUIRCLE;
import static com.devrj.superellipse.SuperEllipse.debugTimed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/** TODO(RJ):
 *  - Glyph caching!
 */
public class SuperEllipseImageView extends AppCompatImageView
{ public SuperEllipseImageView(Context context)
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
  
  public void setShapeBackgroundColor(int backgroundColor)
  { this.shapeBackgroundColor = backgroundColor;
  }
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
  { this.shapeRadius = shapeRadius;
  }
  public void setShapeScale(float shapeScale)
  { this.shapeScale = shapeScale;
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
  private final Paint debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  
  public void init(AttributeSet attrs)
  {
    debugPaint.setStyle(Paint.Style.STROKE);
    debugPaint.setColor(Color.RED);
    debugPaint.setStrokeWidth(24.f);
    
    resetShape();
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
  { debugTimed("View::draw", () -> super.draw(canvas));
  }
  
  private void drawRectOutline(Canvas canvas, int color, float x, float y, float width, float height)
  { debugPaint.setColor(color);
    canvas.drawRect(x, y, x+width, y+height, debugPaint);
  }
  
  @Override
  protected void onDraw(Canvas canvas)
  { final SuperEllipse e = DEFAULT;
    
    if (isReady)
    { if (shapeRadius == MATCH_PARENT)
      { final int maxWidth = viewWidth-(getPaddingLeft()+getPaddingRight());
        final int maxHeight = viewHeight-(getPaddingTop()+getPaddingBottom());
        shapeRadius = (Math.min(maxWidth, maxHeight) / 2.f) * shapeScale;
        drawRectOutline(canvas, Color.GREEN, 0.f, 0.f, maxWidth, maxHeight);
      }
      
      assert shapeRadius > 0;
  
      drawRectOutline(canvas, Color.RED, 0.f, 0.f, viewWidth, viewHeight);
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
      { canvas.save();
        e.beginRenderingContext(4*shapeRadius, 4*shapeRadius);
        canvas.translate(
            (viewWidth>>1) - (e.renderWidth>>1),
            (viewHeight>>1) - (e.renderHeight>>1));
        e.drawSuperEllipse(shapeCurveFactor, shapeRadius, shapeRadius, shapeBackgroundColor, shapeForegroundColor, shapeBorderWidth);
        canvas.drawRenderNode(e.renderNode);
        e.endRenderingContext();
        canvas.restore();
      }
      if (true)
      { canvas.save();
        canvas.translate(viewWidth>>1, viewHeight>>1);
        e.beginRenderingContext(canvas);
        debugTimed("Canvas::draw", () ->
          e.drawSuperEllipse(shapeCurveFactor, shapeRadius, shapeRadius, shapeBackgroundColor, shapeForegroundColor, shapeBorderWidth));
        e.endRenderingContext();
        canvas.restore();
      }
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
