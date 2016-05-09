# HeaderViewPager
###具有共同头部的 ViewPager，支持与ListView，GridView，ScrollView，WebView，RecyclerView 嵌套使用。具有连续的滑动事件 和 滑动监听， 支持下拉刷新。

该项目参考了：[https://github.com/cpoopc/ScrollableLayout](https://github.com/cpoopc/ScrollableLayout) 喜欢原作的可以去使用。相比原项目，代码更简单易懂，扩展性更高，欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 演示
 ![image](http://7xss53.com2.z0.glb.clouddn.com/headerviewpager/demo1.png) ![image](http://7xss53.com2.z0.glb.clouddn.com/headerviewpager/demo2.gif) ![image](http://7xss53.com2.z0.glb.clouddn.com/headerviewpager/demo3.gif)
## 1.用法
该项目和我github上其他的view相关的项目已经一起打包上传到jCenter仓库中（源码地址 [https://github.com/jeasonlzy0216/ViewCore](https://github.com/jeasonlzy0216/ViewCore) ），使用的时候可以直接使用compile依赖，用法如下
###该项目中使用到的大部分自定义控件，均来源于上述仓库
```java
	compile 'com.lzy.widget:view-core:0.2.1'
```
或者使用
```java
    compile project(':header_viewpager')
```

## 2.实现原理
把自定义控件 `HeaderViewPagerLayout` 的 `dispatchTouchEvent` 方法进行重写，根据手势方向决定是否分发事件，同时使用 Scroller 滚动内部视图，达到滑动的连续性。 具体详细代码，实例代码中会有详细注释。

## 3.代码参考
### 1.布局解析
 * 自定义控件`HeaderViewPagerLayout`作为根布局
 * 无论控件具有多少个子`View`,只有第一个子`View`会被自定义控件按头部解析，所以，如果头部有多个`View`，可以使用`ViewGroup`包裹，例如下面实例使用的是`LinearLayout`包裹（头部是一个ViewPager和Indicator指示器），除了第一个`View`会被滑出去外，其余布局均不会被滑出。
 * 如果滑动时，想让布局滑动到一定距离后停止么可以在xml布局中加入自定义属性`app:hvp_topOffset="50dp"`，值的大小表示距离顶部多少距离停止滑动
 * 在头部滑动的过程中，可以使用`setOnScrollListener`设置滑动监听，动态改变头部据或者其他布局的动画
 
###例如布局中做如下布局：
```xml
	<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:layout_width="match_parent"
             android:layout_height="match_parent">

    <com.lzy.widget.HeaderViewPager
        android:id="@+id/scrollableLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:hvp_topOffset="50dp">

        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="200dp">

            <com.lzy.widget.loop.LoopViewPager
                android:id="@+id/pagerHeader"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:lvp_delayTime="2000"
                app:lvp_isAutoLoop="true"/>

            <com.lzy.widget.tab.CircleIndicator
                android:id="@+id/ci"
                android:layout_width="match_parent"
                android:layout_height="20dp"
                android:layout_alignParentBottom="true"
                android:background="#4000"
                app:ci_normalRadiusColor="#FFF"
                app:ci_selectedRadiusColor="#FFF"/>
        </RelativeLayout>

        <com.lzy.widget.tab.PagerSlidingTabStrip
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
    </com.lzy.widget.HeaderViewPager>

    <include
        android:id="@+id/titleBar"
        layout="@layout/include_titlebar"/>
</FrameLayout>
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
