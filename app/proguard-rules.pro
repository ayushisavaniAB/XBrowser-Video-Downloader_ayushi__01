# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class com.google.android.exoplayer.** { *; }

-keep public class com.google.ads.**{
   public *;
}

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.privatebrowser.safebrowser.download.video.download.CompletedVideos.** { <fields>; }
-keep class com.privatebrowser.safebrowser.download.video.download.DownloadQueues.** { <fields>; }
-keep class com.privatebrowser.safebrowser.download.video.download.DownloadVideo.** { <fields>; }
-keep class com.privatebrowser.safebrowser.download.video.download.blockedAds.AdBlocker.** { <fields>; }
-keep class com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel.** { <fields>; }
-keep class com.privatebrowser.safebrowser.download.video.videos.model.VideoModel { <fields>; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
  }


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
