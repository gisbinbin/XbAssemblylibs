# XbAssemblylibs
android自定义组件

Step 1. 将JITPACK存储库添加到build.gradle文件中
将其添加到根目录的build.gradle中

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}

Step 2. 添加依赖项

	dependencies {
		compile 'com.github.gisbinbin:XbAssemblylibs:1.0.4'
	}
	
## 1、组件库包含控件<br>
组件库包含二维码扫描、列表下拉加载更多，下拉刷新及左滑菜单、时间输入及其他常用自定义控件

## 2、组件库引入<br>
Step 1. 将JITPACK存储库添加到build.gradle文件中
将其添加到根目录的build.gradle中

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}

Step 2. 添加依赖项

	dependencies {
		compile 'com.github.gisbinbin:XbAssemblylibs:1.0.4'
	}

## 3、控件使用手册<br> 
### 3.1、二维码扫描组件
![Alt text](https://github.com/gisbinbin/XbAssemblylibs/blob/master/Screenshots/_20180602145824.jpg)
Android二维码扫描需要定义一个有数据回传的Activity,在此我们定义一个QRScanActivity继承组件库中的BaseScanActivity布局页面必须包含SurfaceView、com.xb.xblibrary.view.ViewfinderView两个必备控件、其他控件可按需使用，为支持现有所用扫描功能，布局文件如下：
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent">
    <SurfaceView
        android:id="@+id/preview_view"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:layout_gravity="center" />
    <com.xb.xblibrary.view.ViewfinderView
        android:id="@+id/myqrscan_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_alignParentBottom="true">
        <Button
            android:id="@+id/openlight_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="灯光"/>
        <Button
            android:id="@+id/openxc_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="相册"/>
        <Button
            android:id="@+id/openother_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="其他"/>
    </LinearLayout>
</RelativeLayout>
```

继承BaseScanActivity初始化指定加载布局文件、SurfaceView控件ID、ViewfinderView控件ID、

```java
@Override
public int intiLayout() {
    return R.layout.activity_qrscan;
}
@Override
public int initSurfaceView() {
    return R.id.preview_view;
}

@Override
public int initViewfinderView() {
    return R.id.myqrscan_view;
}
```

到这一步可以开始调用QRScanActivity进行扫描了，调用使用startActivityForResult启动Activity，使用onActivityResult代码如下：<br> 
```
@Override
public void onClick(View v) {
    switch (v.getId()){
        case R.id.scan_btn:
            Intent scanintent = new Intent();
            scanintent.setClass(MainActivity.this, QRScanActivity.class);
            scanintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(scanintent, R.id.scan_btn);
            break;
            }
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
        case R.id.scan_btn:
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                String name = bundle.getString("name");
                Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
            }
            break;
        default:
            break;
    }
}
```

目前还支持扫描灯的开启和关闭、相册二维码图片识别、镜头拉伸、光线感应等功能。<br> 

扫描灯的开启和关闭、扫描相册二维码照片可以直接通过监听按钮OpenAndCloseLight()和ScanPicQR()方法。<br> 

镜头拉伸通过传入int类型的参数zoomto进行控制：-1代表循环来回拉伸、0代表不拉伸、大于0代表拉伸到原来的倍数；scanintent.putExtra("zoomto",1);<br> 

光线感应通过传入boolean类型的参数mylight进行控制：true代表开启光线感应器，在扫描环境较暗时会自动打开扫描灯，false为不开启；scanintent.putExtra("mylight",true);
