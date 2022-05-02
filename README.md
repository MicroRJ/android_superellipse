# Android-SuperEllipse

![A set of squircles](https://github.com/MicroRJ/Android-Canvas-Squircle/blob/master/s_sample1.png)
![A set of squircles](https://github.com/MicroRJ/Android-Canvas-Squircle/blob/master/s_sample2.png)

An extremely simple Squircle renderer for the Android canvas.

# Instructions

Step 1. Add Jitpack to your gradle script.

```groovy
      allprojects
      {
        repositories
        {
          maven { url 'https://jitpack.io' }
        }
      }
```
Step 2. Add the dependency

```groovy
    dependencies
    {
      implementation 'com.github.MicroRJ:android_superellipse:3.0.0-alpha'
    }
```

# Example

![Example Image](https://github.com/MicroRJ/Android-Canvas-Squircle/blob/master/s_sample3.png)

```xml
    <com.devrj.superellipse.SuperEllipseImageView
        android:layout_width="256dp"
        android:layout_height="256dp"
        android:id="@+id/superView"
        android:padding="24dp"
        app:shapeBorderWidth="16dp"
        android:src="@drawable/ic_baseline_music_note_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>
```

# Reference Sheet
Attribute                       | Effect
--------------------------------|------------------------
shapeBackgroundColor: Color     | Set the background color of the shape.
shapeForegroundColor: Color     | Set the foreground color of the shape. (The border color)
shapeBorderWidth: Dimension     | Set the border width of the shape.
shapeCurveFactor: Float         | Set the curve factor of the shape.
shapeRadius: Dimension          | Set the radius of the shape.
shapeScale: Float               | Set the scale of the shape.



# Footer
Get yourself some squircles.


