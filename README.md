# **SafeKeyboard**
Android自定义安全软键盘，完全自定义，方便、安全、可靠

下一步计划：改进键盘遮挡解决方案、增加换肤、一键使用、打包发布等功能

详见:  [Android 自定义安全软键盘 SafeKeyboard 开发详细说明 2.0](https://github.com/SValence/SafeKeyboard/wiki/SafeKeyboard-2.0)

预览<br>
![image](explain_files/SafeKeyboard_preview_1.5x.gif)

## **历史更新**

### 三、 2019/09/26
* 1 . 解决 `SafeKeyboard` 在 `PopupWindow` 中显示会崩溃的问题 (关闭按键预览功能即可). 见 `issue#3`
* 2 . 解决 `SafeKeyboard` 显示后遮住目标 `EditText` 问题 (被遮挡时顶起目标 `EditText` 父 `View`). 见 `issue#8`
  <br>暂时只提供这一种解决方案 ( 尚有改进空间 )
* 3 . 适配 `ScrollView` 中的 `EditText` 使用 `SafeKeyboard` 功能. 详见: &nbsp;&nbsp;[注意事项 (SafeKeyboard_Note)](SafeKeyboard_Note.md)
* 4 . 解决 `SafeKeyboard` 在 `AlertDialog` 中显示无法点击的问题. 采用 `DialogFragment` 来实现该功能.
* 5 . 修改多个 `EditText` 共用一个 `SafeKeyboard` 时, `OnTouch` 事件造成 `SafeKeyboard` 显示混乱的 BUG.
* 6 . 简化部分 API 调用代码, 并增加一个有数字的字母键盘.


### 二、 2019/06/22
* 1 . 支持多个 `EditText` 共用一个 `SafeKeyboard`, 各键盘无缝切换
* 2 . 支持根据不同 `EditText` 的 `InputType` 默认使用不同的键盘(目前仅支持数字键盘和身份证键盘)
* 3 . 支持锁定英文大写
* 4 . 增加两种数字键盘、增加一个身份证键盘, 对两种键盘的数字支持随机显示
* 5 . `SafeKeyboard` 提供接口指定显示身份证键盘的 `EditText`
* 6 . 支持记住每个 `EditText` 对应的上次打开的键盘类型, 再次显示 `SafeKeyboard` 时显示该中类型键盘, 此功能可在 `SafeKeyboardView` 的属性中 打开/关闭
* 7 . 项目本身支持 lambda 表达式

### 一、 2018/07/29
* 1 . `AndroidManifest.xml` 文件中添加 `"android:windowSoftInputMode="stateAlwaysHidden">"`, 兼容低版本系统, 重新进入软件界面系统软键盘自动弹出的问题。
* 2 . 解决特殊键盘图标在不同屏幕上显示变形问题, 不需要手动设置图片显示时与按键边界的边距。
* 3 . `SafeKeyboard` 提供接口设置特殊按键的自定义图片
