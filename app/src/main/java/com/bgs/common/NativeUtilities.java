package com.bgs.common;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.bgs.dheket.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;

/**
 * Created by madhur on 3/1/15.
 */
public class NativeUtilities {

    public native static void loadBitmap(String path, Bitmap bitmap, int scale, int width, int height, int stride);


}
