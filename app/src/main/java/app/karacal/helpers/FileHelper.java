package app.karacal.helpers;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

public class FileHelper {

    public static String getRealFileName(Context context, Uri uri){
        String result = null;
        try {
            if (uri.getScheme().equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            result = "File name";
        }
        return result;
    }

    public static String getRealAudioPathFromUri(Context context, Uri contentUri) {
        return getRealFilePathFromUri(context, contentUri, MediaStore.Audio.Media.DATA);

    }

    public static String getRealImagePathFromUri(Context context, Uri contentUri) {
        return getRealFilePathFromUri(context, contentUri, MediaStore.Images.Media.DATA);
    }

    private static String getRealFilePathFromUri(Context context, Uri contentUri, String dataType) {
        String result = null;
        Cursor cursor = null;
        try {
            if (contentUri.getScheme().equals("content")) {
                String[] projection = {dataType};
                cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
                int column_index;
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow(dataType);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                }
            }

            if (result == null) {
                result = contentUri.getPath();
            }
            return result;
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public static int getAudioDuration(Context context, Uri uri){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return (int) (Long.parseLong(durationStr) / 1000);
    }
}
