package com.laodev.translate.utils;

import com.theartofdev.edmodo.cropper.CropImage;

public class CropImageRequest {
    public static final int IMAGE_WIDTH = 4000;
    public static final int IMAGE_HEIGHT = 4000;

    //get crop image request when user wants to change his photo
    public static CropImage.ActivityBuilder getCropImageRequest() {
        return CropImage.activity()
                .setMaxCropResultSize(IMAGE_WIDTH, IMAGE_HEIGHT)
                .setAutoZoomEnabled(false);
    }
}
