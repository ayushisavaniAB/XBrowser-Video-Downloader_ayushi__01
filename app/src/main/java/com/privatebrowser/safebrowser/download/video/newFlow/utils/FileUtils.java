package com.privatebrowser.safebrowser.download.video.newFlow.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/* loaded from: classes2.dex */
public class FileUtils {
    public static void copyFile(String str, String str2) {
        try {
            if (new File(str).exists()) {
                FileInputStream fileInputStream = new FileInputStream(str);
                FileOutputStream fileOutputStream = new FileOutputStream(str2);
                byte[] bArr = new byte[1444];
                int i = 0;
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        i += read;
                        System.out.println(i);
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileInputStream.close();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public static boolean isVideo(String str, List<String> list) {
        for (String str2 : list) {
            if (str.matches(str2)) {
                return true;
            }
        }
        return false;
    }
}
