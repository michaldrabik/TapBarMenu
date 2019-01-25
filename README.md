[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-TapBarMenu-lightgrey.svg?style=flat)](https://android-arsenal.com/details/1/2886)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)


TapBar Menu
===========

Simple library that helps creating a "Tap Bar" menu layout.

![alt text](http://i.giphy.com/ZRCzrySXUaMwM.gif "Demo 1")
![alt text](http://i.giphy.com/zIBFfp4iLlGAo.gif "Demo 2")

Demo 1: https://youtu.be/DjY0cTWWtao

Demo 2: https://youtu.be/dWuPMN6WTOY

## Install
Since version **1.0.6** this library is being distributed via [JitPack](http://jitpack.io/)

**Step 1:**  
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2:**  
Add the dependency:
```groovy
dependencies {
	implementation 'com.github.michaldrabik:TapBarMenu:1.0.6'
}
```
---
For versions **1.0.5** and older:

Add the dependency to your `build.gradle`:

```groovy
dependencies {
    compile 'com.github.michaldrabik:tapbarmenu:1.0.5'
}
```

## Use

Check sample project for a full example.

TapBarMenu is an extension of a LinearLayout so you can simply put it in your XML. For example:
```xml
<com.michaldrabik.tapbarmenulib.TapBarMenu
  android:id="@+id/tapBarMenu"
  android:layout_width="match_parent"
  android:layout_height="56dp"
  android:layout_gravity="bottom"
  android:layout_marginBottom="24dp"
  >
  
    <ImageView
      android:id="@+id/item1"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_weight="1"
      android:paddingTop="10dp"
      android:paddingBottom="10dp"
      android:src="@drawable/ic_thumb_up"
      />
  
    <Space
      android:layout_width="56dp"
      android:layout_height="match_parent"
      />
  
    <ImageView
      android:id="@+id/item2"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_weight="1"
      android:paddingTop="10dp"
      android:paddingBottom="10dp"
      android:src="@drawable/ic_thumb_down"
      />
   
</com.michaldrabik.tapbarmenulib.TapBarMenu>
```
This will create a menu with 2 icons in the bottom of the screen.

Then, in your code, call `open()`, `close()` or `toggle()` to open/close the menu: 
```java
tapBarMenu.setOnClickListener(new View.OnClickListener() {
  @Override public void onClick(View v) {
    tapBarMenu.toggle();
  }
});
```

*Note: TapBarMenu will always expand to its parent width. You can choose between 2 anchors `bottom` or `top` - see attributes below.*

## Customize
All available XML parameters are listed below:

| Attribute     | Format         | Description  |
| ------------- |:-------------:|:-----:|
| app:tbm_showItems|true, false|Use this to show menu's items in a XML preview window.<br>Useful for designing your menu.<br>*Default: false*|
| app:tbm_backgroundColor|color|Menu color.<br>*Default: red*|
| app:tbm_buttonSize|dimension|Diameter of the menu button.<br>*Default: 56dp*|
| app:tbm_buttonPosition|left, right, center|Position of the menu button.<br>*Default: center*|
| app:tbm_buttonMarginRight|dimension|Button position right margin.<br>*Default: 0*|
| app:tbm_buttonMarginLeft|dimension|Button position left margin.<br>*Default: 0*|
| app:tbm_iconOpened|drawable|Custom icon for opened state. Must be an animated vector drawable.<br>*Default: X icon*|
| app:tbm_iconClosed|drawable|Custom icon for closed state. Must be an animated vector drawable.<br>*Default: Menu icon*|
| app:tbm_menuAnchor|top, bottom|Where menu should expand.<br>*Default: bottom*|

## Kudos

I've been inspired by [Aaron Benjamin's](https://dribbble.com/shots/2345329-Tab-Bar-Animation) great design.

## Release Notes

1.0.6 - Update Android dependencies and migrate to JitPack
<br>1.0.5 - Added Regular Drawable support
<br>1.0.4 - Memory optimizations, onClick listener fixes
<br>1.0.2 - You can now add custom icon via XML or code. See attributes.
<br>1.0.1 - Pre 21 SDK bugfixes
<br>1.0.0 - Initial release

## License

    Copyright (C) 2019 Michal Drabik

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
