# HeaderViewPager
###具有共同头部的 ViewPager，支持与ListView，GridView，ScrollView，WebView，RecyclerView 嵌套使用。具有连续的滑动事件 和 滑动监听， 支持下拉刷新。

该项目参考了：[https://github.com/cpoopc/ScrollableLayout](https://github.com/cpoopc/ScrollableLayout) 喜欢原作的可以去使用。相比原项目，代码更简单易懂，扩展性更高，欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 演示
 ![image](https://github.com/jeasonlzy0216/HeaderViewPager/blob/master/screenshots/demo1.png)![image](https://github.com/jeasonlzy0216/HeaderViewPager/blob/master/screenshots/demo2.gif)![image](https://github.com/jeasonlzy0216/HeaderViewPager/blob/master/screenshots/demo3.gif)![image](https://github.com/jeasonlzy0216/HeaderViewPager/blob/master/screenshots/demo4.gif)![image](https://github.com/jeasonlzy0216/HeaderViewPager/blob/master/screenshots/demo5.gif)

## 1.用法
该项目和我github上其他的view相关的项目已经一起打包上传到jCenter仓库中（源码地址 [https://github.com/jeasonlzy0216/ViewCore](https://github.com/jeasonlzy0216/ViewCore) ），使用的时候可以直接使用compile依赖，用法如下
```java
	compile 'com.lzy.widget:view-core:0.1.5'
```
或者使用
```java
    compile project(':header_viewpager')
```

## 2.实现原理
把自定义控件 `HeaderViewPagerLayout` 的 `dispatchTouchEvent` 方法进行重写，根据手势方向决定是否分发事件，同时使用 Scroller 滚动内部视图，达到滑动的连续性。 具体详细代码，实例代码中会有详细注释。

## 3.代码参考
### 1.布局解析
最外层的`PtrClassicFrameLayout`使用的是第三方下拉刷新控件，包裹在自定义控件`HeaderViewPagerLayout`之外，可以让`HeaderViewPagerLayout`拥有下拉刷新的功能，其中，`HeaderViewPagerLayout`有三个子`View`,只有第一个子`View`会被自定义控件按头部解析，所以，如果头部有多个`View`，可以使用`ViewGroup`包裹，例如下面实例使用的是`LinearLayout`包裹（头部也是一个ViewPager），除了第一个`View`会被滑出去外，其余布局均不会被滑出。
```xml
	<?xml version="1.0" encoding="utf-8"?>
	<in.srain.cube.views.ptr.PtrClassicFrameLayout
	    android:id="@+id/ptr"
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    app:ptr_duration_to_close="200"
	    app:ptr_duration_to_close_header="1000"
	    app:ptr_keep_header_when_refresh="true"
	    app:ptr_pull_to_fresh="false"
	    app:ptr_ratio_of_header_height_to_refresh="1.2"
	    app:ptr_resistance="1.7">
	
	    <com.lzy.ui.HeaderViewPagerLayout
	        android:id="@+id/scrollableLayout"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:orientation="vertical">
	
	        <LinearLayout
	            android:layout_width="match_parent"
	            android:layout_height="150dp">
	
	            <android.support.v4.view.ViewPager
	                android:id="@+id/pagerHeader"
	                android:layout_width="match_parent"
	                android:layout_height="match_parent"/>
	        </LinearLayout>
	
	        <com.lzy.headerviewpager.ui.PagerSlidingTabStrip
	            android:id="@+id/tabs"
	            android:layout_width="match_parent"
	            android:layout_height="40dp"
	            android:background="@mipmap/bg_menu_normal"
	            android:textSize="16sp"
	            app:pstsDividerColor="#00000000"
	            app:pstsIndicatorColor="#2DA4F0"
	            app:pstsIndicatorHeight="2dp"
	            app:pstsShouldExpand="false"
	            app:pstsTextAllCaps="false"
	            app:pstsUnderlineHeight="2dp"/>
	
	        <android.support.v4.view.ViewPager
	            android:id="@+id/viewPager"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"/>
	    </com.lzy.ui.HeaderViewPagerLayout>
	</in.srain.cube.views.ptr.PtrClassicFrameLayout>
```
### 2.对于自定义控件，需要做如下初始化
```java
	scrollableLayout.setCurrentScrollableContainer(fragments.get(0));
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            scrollableLayout.setCurrentScrollableContainer(fragments.get(position));
        }
    });
```
### 3.如果需要对滑动过程进行监听，可以使用如下代码,currentY 表示当前滑过的距离，maxY表示当前可以滑动的最大距离，有了这两个参数，就可以对任意布局，做任何动画了。例如如下代码就是实现 视差动画效果的代码。
```xml
	scrollableLayout.setOnScrollListener(new HeaderViewPagerLayout.OnScrollListener() {
        @Override
        public void onScroll(int currentY, int maxY) {
            image.setTranslationY(currentY / 2);
        }
    });
```
