package com.matemeup.matemeup.entities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapGetter {

    private static final String imageHeader = "data:image/jpeg;base64,";

    public static Boolean isSmallerThan(int bLength, int maxKbLength) {
        return bLength / 1000 <= maxKbLength;
    }

    public static String get(Context ctx, Intent data) {
        return get(ctx, data, -1);
    }

    public static String get(Context ctx, Intent data, int maxKbLength) {
        Uri uri = data.getData();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            try {
                byte[] b = baos.toByteArray();

                if (maxKbLength != -1 && isSmallerThan(b.length, maxKbLength))
                {
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    return imageHeader + encImage;
                }
            } catch (NullPointerException e) {
                System.out.println("bitmaperror1");
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                System.out.println("bitmaperror2");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
