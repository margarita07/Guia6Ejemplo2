package com.ejemplo2.ejemplo2Margarita;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText txtArriba,txtAbajo;
    Button btnAplicar;
    ImageView imageView;
    RadioButton rbnBlanco,rbnNegro;

    Resources resources;
    float scale;
    final  int READ_EXTERNAL_STORAGE_PERMISSION_CODE=23;
    final  int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE=23;
    final int CODIGO_IMAGEN =10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtArriba = findViewById(R.id.txtArriba);
        txtAbajo = findViewById(R.id.txtAbajo);
        btnAplicar = findViewById(R.id.btnAplicar);
        imageView = findViewById(R.id.imageView);
        resources = this.getResources();
        scale = resources.getDisplayMetrics().density;
        rbnBlanco       =findViewById(R.id.rbnBlando);
        rbnNegro        = findViewById(R.id.rbnNegro);
        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararIMG();
            }
        });
    }

    private void prepararIMG() {
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Bitmap resultado = aplicarTexto(
                txtArriba.getText().toString(),
                txtAbajo.getText().toString(),
                bitmap,
                scale);
        if (resultado!=null){
            imageView.setImageBitmap(resultado);
        }
    }

    private Bitmap aplicarTexto(String s, String s1, Bitmap bitmap, float scale) {
        try {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        Log.d("Densidad : "," "+bitmap.getDensity());
        Log.d("Heigth : "," "+bitmap.getHeight());
        Log.d("Width : "," "+bitmap.getWidth());

        float factor = (float) (bitmap.getHeight()*0.06);

        if(bitmapConfig ==null){
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }

        bitmap = bitmap.copy(bitmapConfig,true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if(rbnNegro.isChecked()){
            paint.setColor(Color.rgb(35,35,35));
        }else{
            paint.setColor(Color.WHITE);
        }

        paint.setTextSize((int) (factor*scale));
        paint.setShadowLayer(1f,0f,1f,Color.DKGRAY);
        Rect boundsMSG1 = new Rect();
        paint.getTextBounds(s,0,s.length(),boundsMSG1);
        int xMSG1 = (bitmap.getWidth()/2)-(boundsMSG1.width()/2);
        int yMSG1 = (int)(bitmap.getHeight()*0.11);
        canvas.drawText(s,xMSG1,yMSG1,paint);

        Log.d("Ancho  bmp : ",""+bitmap.getWidth());
        Log.d("Canvas     : ",""+canvas.getWidth());
        Log.d("Bounds     : ",""+boundsMSG1.width());
        Log.d("Coord X    : ",""+xMSG1);
        Log.d("xMSG*scale : ",""+(xMSG1*scale));

        int nX = (bitmap.getWidth()/2) - (boundsMSG1.width()/2);
        Log.d("Nueva X    : ",""+nX);

        // poner el msj2
        Rect boundsMSG2 = new Rect();
        paint.getTextBounds(s1, 0, s1.length(), boundsMSG2);
        int xMSG2 = (bitmap.getWidth()/2) - (boundsMSG2.width()/2);//en medio
        int yMSG2 = (int)(bitmap.getHeight()*0.95);
        canvas.drawText(s1, xMSG2, yMSG2, paint);

        return bitmap;
    } catch (Exception e) {
        Log.e("ERROR",e.getMessage());
        Toast.makeText(this,"Se ha producido un error",Toast.LENGTH_SHORT).show();
        return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_opciones,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         switch (item.getItemId()){
             case R.id.menu_abrir:
                 abrirArchivo();
                 break;
             case R.id.menu_guardar:
                 guardarArchivo();
                 break;
             case R.id.menu_cancelar:
                 cancelar();
                 break;
         }
        return true;
    }

    private void cancelar() {
        Toast.makeText(this,"Menu Cancelar",Toast.LENGTH_SHORT).show();
    }

    private void guardarArchivo() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }else{
            //Si ya tengo el Permiso Habilitado
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            String name = "IMG"+String.format("%d.png",System.currentTimeMillis());
            new  SaveImage(this,"Guia6",name).execute(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
            //si el Permiso Fue Concedido
        }
    }

    private void abrirArchivo() {
        Toast.makeText(this,"Menu Abrir",Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,CODIGO_IMAGEN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODIGO_IMAGEN && resultCode== Activity.RESULT_OK){
            Uri SelectedImage = data.getData();
            Bitmap bmp = null;
            try{
                bmp = getBitmapFromUri(SelectedImage);
                imageView.setImageBitmap(bmp);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException{
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri,"r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return  image;
    }
}
