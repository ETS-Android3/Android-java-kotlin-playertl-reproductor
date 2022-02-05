# PlayerTL

:musical_note::musical_note::musical_note::musical_note:

playerTL es un gestor de listas de canciones para que puedas escuchar tu música ordenada y gestionada por tí mismo.
playerTL is a song list manager so you can listen to your music ordered and managed by yourself.

<!-- ![Imagen programa](images/program.jpg) -->
<img src="docs/program.jpg" width="300" />


<br /><br /><br /><br /><br />
# Desarollo / development

## Tecnologías

### Splash screen

1. Ve a la carperta res/drawablw y haz click con el botón derecho, en el menú contextual elige New drawable file, llamalo splash_background
   
    <?xml version="1.0" encoding="utf-8"?>
    <!-- layer list es una lista de capas-->
    <layer-list android:opacity="opaque"
        xmlns:android="http://schemas.android.com/apk/res/android">
        <item android:drawable="@color/colorPrimaryDarkRetro" />
        <item>
            <bitmap android:src="@drawable/disco"></bitmap>
        </item>
    </layer-list>

2.Ve a res/layout y crea la activity SplashActivity, borrale el ñayout que se ha creado, tambien borrar dentro de spahAvtivity.java el setContent
3.Ve a res/values/Themes y crea un nuevo tema:
    <resources>
        <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
            <item name="colorPrimary">@color/colorPrimaryDefecto</item>
            <item name="colorPrimaryDark">@color/colorPrimaryDarkDefecto</item>
            <item name="colorAccent">@color/colorAccentDefecto</item>
        </style>
        <style name="SplashTheme" parent="AppTheme">
            <item name="android:windowBackground">@drawable/spalsh_background</item>
        </style>
    </resources>
4.Vamos al manifest le quitamos el inter filter de la mainActivity y se lo metemos l splashactivity y dentro de la etiqueta activity del splash le ponemos el theme que hemos creado.
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="es.tipolisto.versionadnroid">
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />

        <application
            android:allowBackup="true"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/AppTheme">
            <activity
                android:name=".SplashActivity"
                android:exported="true"
                android:theme="@style/SplashTheme">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />

                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
            <activity android:name=".Activities.ExplorerFilesActivity" />
            <activity android:name=".Activities.MainActivity">
            </activity>
        </application>
    </manifest>




## Requisitos

### optopenjdk 8 o Java 8 < java 8 u202

A partir de la versión java 8 u202 y java 11, así que o instalas una versión anterior que no te aconsejo a java 8 u202 o instalas [adopopenjdk](https://adoptopenjdk.net/), si estás en windows simplemente instala el programa.

Starting with the java 8 u202 and java 11 version, so either install an earlier version that I do not recommend to java 8 u202 or install [adopopenjdk] (https://adoptopenjdk.net/), if you are on windows simply install the Program.

> Marca la ocpión de establecerla variable JAVA_HOME.

> Check the option to set the JAVA_HOME variable.

### Android studio + SDK android

[Download android studio](https://developer.android.com/studio?hl=es-419)

Another alternative is / Otra alternativa es [IntelliJ IDEA comunity](https://www.jetbrains.com/es-es/idea/download/#section=windows)

### How to open the project / Como abrir el proyecto

El proyecto puede ser abierto pinchando con el botón derecho del ratón en build.gradle (que se encuentra en la raiz del proyecto) y elige tu IDE.

The project can be opened by right-clicking build.gradle (located at the root of the project) and choosing your IDE.


### information / Información

Los proyectos android studio llevan integrados gradle, este proyecto descargará gradle-4.10.1-all automáticamente, descargará las dependencias y configurará su entorno de trabajo, gradle también utiliza tareas, como la de compilar, ejecutar, etc, tan solo tiene que pulsar el botón de compiladción.

The android studio projects have gradle integrated, this project will download gradle-4.10.1-all automatically, it will download the dependencies and configure your work environment, gradle also uses tasks, such as compiling, executing, etc., just press the compilation button.


