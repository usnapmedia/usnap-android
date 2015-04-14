# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# For joda-time library
-dontwarn org.joda.time.**
-dontnote org.joda.time.**

# For Picasso
-dontwarn com.squareup.okhttp.**

# For segment.io SDK
-dontwarn com.actionbarsherlock.app.**
-dontnote com.actionbarsherlock.app.**
-dontwarn com.mixpanel.**
-dontnote com.mixpanel.**
-dontwarn com.kahuna.sdk.**
-keep class com.kahuna.sdk.* { *; }

# For Google Maps
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# For Butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

# For retrofit
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.squareup.**
-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }
-keepclassmembers class * {
  public <init>(android.content.Context);
}

# your package path where your gson models are stored
-keepclassmembers class com.samsao.app.network.entity.** { *; }
-keepclassmembers class com.samsao.app.model.entity.** { *; }

# For Realm
-dontwarn io.realm.**
-keep class io.realm.* { *; }

# For EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# For IcePick
-dontwarn icepick.**
-keep class **$$Icicle { *; }
-keepnames class * { @icepick.Icicle *;}

# For RxJava
-dontwarn rx.internal.util.**

# For Twitter SDK
-include ../proguard-com.twitter.sdk.android.twitter.txt

# For Facebook SDK
-keep class com.facebook.** { *; }
-keepattributes Signature

# For Jackson
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class org.codehaus.jackson.** { *; }
-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**
# If the model classes are obfuscated, reflection does not work
-keep public class com.samsao.snapzi.api.entity.** {
  public void set*(***);
  public *** get*();
}