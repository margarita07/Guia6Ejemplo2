package com.ejemplo2.ejemplo2Margarita;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class SaveImage  extends AsyncTask<Bitmap,Void,Boolean>{
    String path;
    String name;
    Context context;
    public SaveImage(Context context, String path, String name) {
        this.context = context;
        this.path = path;
        this.name = name;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"Guardando Por Favor Espere...",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Toast.makeText(context,"Img :"+name+"Guardada en : "+path,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        Boolean saved = false;
        for(Bitmap img :bitmaps){
            saved = saveToInternalStorage(img,path,name);
        }
        return saved;
    }

    private Boolean saveToInternalStorage(Bitmap img, String path, String name) {
        Boolean saved = false;
        //Se crea La Carpeta Guia6
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath()+"/"+path);
        dir.mkdir();
        FileOutputStream fos1 = null;
        try {
            fos1 = new FileOutputStream(new File(dir,name));
            img.compress(Bitmap.CompressFormat.PNG,100,fos1);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                fos1.close();
                saved=true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return saved;
    }
}
