package com.devrj.superellipse;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RecordingCanvas;
import android.graphics.RenderNode;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Hashtable;

// NOTE(RJ):
// ; TODO(RJ): - Glyph caching!
// ;
public final class SuperEllipse
{
  public static final SuperEllipse DEFAULT = new SuperEllipse();
  
  public static final int NO_COLOR = 0x00FF00FF;
  public static final int NO_BORDER = 0;
  
  public interface DebugCall<T>
  {
    T call();
  }
  
  public interface DebugCallNoRet
  {
    void call();
  }
  
  static <T> T debugTimed(String info, DebugCall<T> call)
  {
    long begin = System.nanoTime();
    T result = call.call();
    long end = System.nanoTime();
    System.out.printf("debugTimed(info := %s) (%s)ms\n", info, (end - begin) / 1000000.f);
    return result;
  }
  
  static void debugTimed(String info, DebugCallNoRet call)
  {
    long begin = System.nanoTime();
    call.call();
    long end = System.nanoTime();
    System.out.printf("debugTimed(info := %s) (%s)ms\n", info, (end - begin) / 1000000.f);
  }
  
  /**
   * NOTE(RJ):
   * ; Describes a closed curve in unit space.
   * ; Where :Curve::n represents the curve factor and :Curve::r
   * ; represents the ratio of the semi-diameters [a] and [b].
   * ; Both the :Rune and the :Glyph use a curve to represent the shape they
   * ; contain.
   */
  private static final class Curve
  {
    public static String getHashKey(Curve curve)
    {
      return getHashKey(curve.n, curve.r);
    }
    
    public static String getHashKey(float n, float r)
    {
      return String.format("%s]%s", n, r);
    }
    
    /**
     * NOTE(RJ):
     * ; Curve factor.
     */
    public final float n;
    /**
     * NOTE(RJ):
     * ; Ratio of semi-diameters!
     */
    public final float r;
    
    public Curve(float n, float r)
    {
      this.n = n;
      this.r = r;
    }
    
    public Curve(float n, float a, float b)
    {
      this(n, b / a);
    }
    
    public String getHashKey()
    {
      return getHashKey(this);
    }
    
    public Curve scale(float a, float b)
    {
      return new Curve(n, a, b);
    }
    
    @NonNull
    @Override
    public Curve clone()
    {
      return new Curve(n, r);
    }
  }
  
  public enum Shape
  {
    /**
     * NOTE(RJ):
     * ; if n > 0 && n < 1
     * ; https://www.desmos.com/calculator/hcdh7n1xqr
     */
    ASTROID(0.5f, 1.f, 1.f),
    /**
     * NOTE(RJ):
     * ; if n == 1
     * ; https://www.desmos.com/calculator/spturwwuft
     */
    RHOMBUS(1.f, 1.f, 1.f),
    /**
     * NOTE(RJ):
     * ; if n > 1 && n < 2
     * ; https://www.desmos.com/calculator/qjvqipqpkd
     */
    RHOMBUS_CONVEX(1.5f, 1.f, 1.f),
    /**
     * NOTE(RJ):
     * ; if n == 2
     * ; https://www.desmos.com/calculator/i0uq2uqzpp
     */
    ELLIPSE(2.f, 1.f, 1.f),
    /**
     * NOTE(RJ):
     * ; if n == 2 && a == b
     * ; https://www.desmos.com/calculator/i0uq2uqzpp
     */
    CIRCLE(2.f, 1.f, 1.f),
    /**
     * NOTE(RJ):
     * ; if n > 2
     * ; https://www.desmos.com/calculator/i0uq2uqzpp
     */
    SQUIRCLE(4.f, 1.f, 1.f);
    
    private final Curve curve;
    
    Shape(float n, float a, float b)
    {
      this.curve = new Curve(n, a, b);
    }
    
    public float getCurveFactor()
    {
      return curve.n;
    }
  }
  
  /**
   * NOTE(RJ):
   * ; Create a cacheable Rune! A Rune has a Curve which describes its path.
   * ; Parent runes are typically in unit space, meaning its Curve::r is equivalent to
   * ; [a, b*r] where [a] and [b] are in unit space, 0.f - 1.f.
   * ; This allows for derived runes to preserve identity.
   */
  public static final class Rune
  {
    public static final int SIZE = 360 + 8 * 2;
    
    public static String getHashKey(Curve curve)
    {
      return Curve.getHashKey(curve);
    }
    
    public static String getHashKey(float n, float r)
    {
      return Curve.getHashKey(n, r);
    }
    
    /**
     * NOTE(RJ):
     * ; The path!
     */
    public Path p;
    /**
     * NOTE(RJ):
     * ; The curve!
     */
    public final Curve b;
    
    public Rune(Curve b, Path p)
    {
      this.b = b;
      this.p = p;
    }
    
    public Rune(float n, float r, Path p)
    {
      this(new Curve(n, r), p);
    }
    
    public Rune(float n, float a, float b, Path p)
    {
      this(new Curve(n, a, b), p);
    }
    
    /**
     * NOTE(RJ):
     * ; Create a transformed copy of this rune.
     * ; Where params [a] and [b] modulate the matrix's scale!
     * ; A new path and its descriptive curve are created for the resulting rune!
     */
    public Rune derivative(float a, float b)
    { return debugTimed("derivative", () ->
      { final Rune result = new Rune(this.b.scale(a, b), new Path());
        Matrix transform = new Matrix();
        transform.setScale(a, b);
        p.transform(transform, result.p);
        return result;
      });
    }
  }
  
  // TODO(RJ):
  // ; This is to be implemented but will most likely be dropped!
  // ;
  public static final class Glyph
  {
    private static String getHashKey(Curve curve, float w, float h, int bc, int fc, int bw)
    { return String.format("%s]%s]%s]%s]%s]%s", curve.getHashKey(), w, h, bc, fc, bw);
    }
    
    public final Curve curve;
    public final float width;
    public final float height;
    
    public final int backgroundColor;
    public final int foregroundColor;
    public final int borderWidth;
    public final int backgroundShadow;
    public final int foregroundShadow;
    public final Bitmap texture;
    
    public Glyph(Curve curve, float width, float height, int backgroundColor, int backgroundShadow, int foregroundColor, int foregroundShadow, int borderWidth, Bitmap texture)
    { this.curve = curve;
      this.width = width;
      this.height = height;
      this.backgroundColor = backgroundColor;
      this.backgroundShadow = backgroundShadow;
      this.foregroundColor = foregroundColor;
      this.foregroundShadow = foregroundShadow;
      this.borderWidth = borderWidth;
      this.texture = texture;
    }
    public void release()
    { if (texture != null && !texture.isRecycled())
      { texture.recycle();
      }
    }
  }
  
  /**
   * NOTE(RJ):
   * ; Iterator!
   */
  interface SuperEllipseIterator
  {
    int it(int i, float x, float y);
  }
  
  public static int getSuperEllipseSgn(double i)
  {
    return i < 0 ? -1 : i > 0 ? 1 : 0;
  }
  
  public static float getSuperEllipseX(float t, float n, float s)
  {
    return (float) (pow(abs(cos(t)), 2.f / n) * s * getSuperEllipseSgn(cos(t)));
  }
  
  public static float getSuperEllipseY(float t, float n, float s)
  {
    return (float) (pow(abs(sin(t)), 2.f / n) * s * getSuperEllipseSgn(sin(t)));
  }
  
  /**
   * NOTE(RJ):
   * ; Iterate over a super ellipse defined by a shape!
   */
  public static void superEllipseIterate(Shape shape, SuperEllipseIterator iterator)
  {
    superEllipseIterate(shape.curve, 1.f, iterator);
  }
  
  /**
   * NOTE(RJ):
   * ; Iterate over a super ellipse defined by a shape and a scalar!
   */
  public static void superEllipseIterate(Shape shape, float s, SuperEllipseIterator iterator)
  {
    superEllipseIterate(shape.curve, s, iterator);
  }
  
  /**
   * NOTE(RJ):
   * ; Iterate over a super ellipse defined by a Curve and a scalar!
   */
  public static void superEllipseIterate(Curve curve, float s, SuperEllipseIterator iterator)
  {
    superEllipseIterate(curve.n, s, s * curve.r, iterator);
  }
  
  /**
   * NOTE(RJ):
   * ; Iterate over a super ellipse defined by function of [n] [a] and [b]!
   * ; Where [a] and [b] are the semi-diameters of the curve!
   * ; Where [n] is the curve factor!
   */
  public static void superEllipseIterate(float n, float a, float b, SuperEllipseIterator iterator)
  {
    for (int i = 0; i < 360; )
    { final float t = (float) (i / 180.f * Math.PI);
      i = iterator.it(i,
          getSuperEllipseX(t, n, a),
          getSuperEllipseY(t, n, b));
    }
  }
  
  /**
   * NOTE(RJ):
   * ; Get super ellipse points!
   */
  public static float[] getSuperEllipsePoints(float n, float a, float b)
  {
    final float[] result = new float[360];
    superEllipseIterate(n, a, b, (i, x, y) ->
    {
      result[i++] = x;
      result[i++] = y;
      return i;
    });
    return result;
  }
  
  public static Path getSuperEllipsePath(Curve curve)
  {
    return getSuperEllipsePath(curve.n, 1.f, curve.r);
  }
  
  public static Path getSuperEllipsePath(float n, float a, float b)
  { assert a <= 1.f && a > 0.f;
    assert b <= 1.f && b > 0.f;
    final Path result = new Path();

    debugTimed("getSuperEllipsePath()", () ->
    { result.reset();
      result.moveTo(getSuperEllipseX(0.f, n, a), getSuperEllipseY(0.f, n, b));
      superEllipseIterate(n, a, b, (i, x, y) ->
      { result.lineTo(x, y);
        return ++i;
      });
      result.close();
    });
    return result;
  }
  
  public final Hashtable<String, Rune> runeCache;
  public final Hashtable<String, Rune> glyphCache;
  public final Paint paint;
  public Bitmap renderSurface;
  public RecordingCanvas renderer;
  public RenderNode renderNode;
  public int renderWidth;
  public int renderHeight;
  
  public SuperEllipse()
  { paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    runeCache = new Hashtable<>();
    glyphCache = new Hashtable<>();
  }
  
  public Rune getExactRune(float n, float a, float b)
  {
    final Curve curve = new Curve(n, a, b);
    final String hashKey = Rune.getHashKey(curve);
    final Rune result;
    if (runeCache.containsKey(hashKey))
    { result = runeCache.get(hashKey);
    } else
    { result = new Rune(curve, getSuperEllipsePath(curve));
      runeCache.put(hashKey, result);
    }
    return result;
  }
  
  public Rune drawSuperEllipse(Curve curve, float scalar, int background, int foreground, float borderWidth)
  { return drawSuperEllipse(curve.n, scalar, scalar * curve.r, background, foreground, borderWidth);
  }
  
  public Rune drawSuperEllipse(float n, float a, float b, int background, int foreground, float borderWidth)
  { assert renderer != null;
    
    final Rune original = getExactRune(n, a, b);
    final Rune result = original.derivative(a - borderWidth / 2.f, b - borderWidth / 2.f);
    
    
    debugTimed("SuperEllipse::drawBackground()", () ->
    { if ((background & 0xFF000000) != 0)
      { paint.setStyle(Paint.Style.FILL);
        paint.setColor(background);
        renderer.drawPath(result.p, paint);
      }
    });
  
    paint.setShadowLayer(20.f, 0.f, 0.f, foreground);
  
    debugTimed("SuperEllipse::drawForeground()", () ->
    { if ((foreground & 0xFF000000) != 0 && (borderWidth != 0))
      { paint.setStyle(Paint.Style.STROKE);
        paint.setColor(foreground);
        paint.setStrokeWidth(borderWidth);
        renderer.drawPath(result.p, paint);
      }
    });
  
    return result;
  }
  public void beginRenderingContext(Bitmap bitmap)
  { renderSurface = bitmap;
    // renderList = new Canvas(bitmap);
  }
  public void beginRenderingContext(Canvas canvas)
  { // renderList = canvas;
  }
  public void beginRenderingContext(float width, float height)
  { beginRenderingContext((int)(width+0.5f), (int)(height+0.5f));
  }
  public void save()
  { assert renderer != null;
    renderer.save();
  }
  public void restore()
  { assert renderer != null;
    renderer.restore();
  }
  public void move(int x, int y)
  { assert renderer != null;
    renderer.translate(x, y);
  }
  public void moveCenter()
  { move(renderWidth>>1, renderHeight>>1);
  }
  public void beginRenderingContext(int width, int height)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
      if (renderNode == null)
      { renderNode = new RenderNode("SuperEllipse");
      }
      renderNode.setPosition(0, 0, width, height);
      renderer = renderNode.beginRecording(width, height);
      renderWidth = width;
      renderHeight = height;

      moveCenter();
    } else
    {
      if (renderSurface == null)
      {
        renderSurface = Bitmap.createBitmap(width, height, ARGB_8888);
      }
      if (renderSurface.getWidth() != width || renderSurface.getHeight() != height)
      {
        renderSurface.reconfigure(width, height, renderSurface.getConfig());
      }
      
      // renderList = new Canvas(renderSurface);
    }
  }
  
  public void endRenderingContext()
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
      if (renderNode != null)
      {
        renderNode.endRecording();
      }
    } else
    {
      renderSurface.recycle();
    }
  
    renderNode = null;
    renderSurface = null;
  }
  
}
