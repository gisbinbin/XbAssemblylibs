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
