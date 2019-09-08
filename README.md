# Android-Canvas-Squircle


![A set of squircles](https://github.com/MicroRJ/Android-Canvas-Squircle/blob/master/s_sample1.png)
![A set of squircles](https://github.com/MicroRJ/Android-Canvas-Squircle/blob/master/s_sample2.png)

A FULL squircle for the Android Canvas, no tricks. You can customize it and modify it. 

# Instructions

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:


	allprojects {
		repositories {			
			...
			maven { url 'https://jitpack.io' }
		}
	}

  
Step 2. Add the dependency


	dependencies {
		implementation 'com.github.MicroRJ:android_superellipse:2.0'
		//Or latest release version
	}



# Example

	 <com.microdevrj.superellipse.custom_superellipse_views.SuperellipseImageView
		android:layout_width="200dp"
		android:layout_height="200dp"
		android:layout_gravity="center"
		android:padding="64dp"
		app:colorFill="@color/colorFill"
		app:colorStroke="@color/colorStroke"
		app:paintStyle="fillAndStroke"
		app:strokeWidth="6dp" />


# On your activity's onDestroy() method call: 

	SuperellipsePerformanceCalculations.release()
	
	
# Documentation


Property    | Effect
------------| -------------
colorFill   | sets the background color of the squircle, different from background property
colorStroke | sets the color of the stroke
strokeWidth | sets the border stroke width 
paintStyle  | you have three options, fill, stroke, fillAndStroke. 



# How does it work? 
There are two main components, actullay only two, the logic and the view that implements it. 
Every time the SuperEllipseImageView requests a squircle an algorithm will decide whether or not the view needs a NEW bitmap or a CACHED one based on the the view's needs, such as size and color, (for now), this allows for the same bitmap to be reused as many times as seen fit by the algorithm.
If there's a bitmap request and there are no cached bitmaps that fit the view's needs a new bitmap will be created and cached. 

# In what cases is this particularly useful? 
This is very useful especially in cases where you use this custom view several times, like in a recycler view or list view, or anything else that involves repetition. Doing it this way you increase performance drastically. 

# What are the current limitations? 
* No live updates. 
* It uses bitmaps tailored to the view's size, which means that when scaled up quality will not be preserved. 
* Although not a big problem, theoretically you could end up with a high amount of cached bitmaps due to the way the algorithm works, this is something that will be accounted for in a future update, but it's not a problem at all unless you have a LOT of unique squircles.
 
*Note that these limitations will be taken care of in a future update. 

Disclaimer
This was never intended to be a library, but rather a specific need for my app. In the future, more features will be added that will fit your specific needs. 





# Footer 
Squircles are a highly requested feature that haven't been added to Android natively, so here you have it, plain and simple and efficient. 

Make sure to let me know if you used my code, I'd be excited to see what projects you're using it on!
And if you'd like to credit me I would highly appreciate it. 

If you like this project and want to motivate me, give it a star!

# A SQUIRCLE IS NOT A ROUNDED SQUARE
