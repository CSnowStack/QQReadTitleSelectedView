# QQReadTitleSelectedView
仿qq阅读 title 上的选择控件

## 预览
![MSearchView](https://github.com/CSnowStack/QQReadTitleSelectedView/blob/master/imgs/c.gif)

## Attributes
name | format | description
:--------|:--------:|:--------
qrtsv_items | reference | 选项,有且只能有两个
qrtsv_text_size | dimension | 文字大小
qrtsv_selected_color | color | 选中时背景的颜色
qrtsv_normal_color | color | 未选中时背景的颜色
qrtsv_border_width | dimension | 边框宽度

## Add the dependency
```java
dependencies {
    compile 'com.cq.csnowstack:loadingbutton:1.0.0'
}
```

## 简单使用
```java
<com.cq.qqreadtitleselectedview.QQReadTitleSelectedView
    android:layout_width="300dp"
    app:qrtsv_border_width="2dp"
    app:qrtsv_items="@array/default_item"
    app:qrtsv_text_size="18sp"
    android:layout_height="40dp"/>
```
