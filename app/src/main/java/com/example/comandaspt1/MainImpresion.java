package com.example.comandaspt1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.print.PrintHelper;

import com.example.comandaspt1.Contract.OnRestListener;
import com.example.comandaspt1.Model.Data.Comanda;
import com.example.comandaspt1.Model.Data.Factura;
import com.example.comandaspt1.Model.Data.Mesa;
import com.example.comandaspt1.Model.Data.Producto;
import com.example.comandaspt1.View.ComandasAdapter;
import com.example.comandaspt1.View.MainViewModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainImpresion extends AppCompatActivity {

    private MainViewModel viewModel;
    private ComandasAdapter adapter;

    private  List<Producto> productoList = new ArrayList<>();
    public Factura factura = new Factura();
    private long idFactura;
    private long idEmpleadoCierre;
    private List<Comanda> comandaList = new ArrayList<>();
    private List<Comanda> comandaAgrupada = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        SharedPreferences sharedPreferences = getSharedPreferences(Conexion.TAG, Context.MODE_PRIVATE);
        idFactura = sharedPreferences.getLong(Facturas.IDFACTURA, 0);
        idEmpleadoCierre = sharedPreferences.getLong(MainActivity.EMPLEADOREMEMBER, 0);


        viewModel = Facturas.viewModel;
        adapter = new ComandasAdapter(this);
        viewModel =  ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLiveDataProductosList().observe(this, new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                productoList = productos;
                cogerComandas();
            }
        });

        cerrarFactura();

    }

    private List<Comanda> agruparComandas(){

        comandaAgrupada = new ArrayList<>();
        boolean añadido;
        Comanda comandaAxu1, comandaAux2;

        for (int i = 0; i < comandaList.size() ; i++) {//comanda principal

            comandaAux2 = comandaList.get(i);
            añadido = false;
            if (comandaAgrupada.size() == 0){
                comandaAgrupada.add(comandaAux2);

            } else {//comandaAgrupada size >0

                for (int j = 0; j < comandaAgrupada.size(); j++) {//comanda agrupada
                    Log.v("xzy", "size: "+comandaAgrupada.size());
                    Log.v("xzy", "indice: "+j);
                    if (comandaAgrupada.get(j).getIdProducto() == comandaAux2.getIdProducto() && comandaAgrupada.get(j).getIdFactura() == idFactura){

                        comandaAxu1 = comandaAgrupada.get(j);
                        comandaAxu1.setUnidades(comandaAxu1.getUnidades()+comandaAux2.getUnidades());
                        comandaAxu1.setPrecio(comandaAxu1.getPrecio()+comandaAux2.getPrecio());
                        comandaAgrupada.set(j, comandaAxu1);
                        añadido = true;
                    }
                }
                if (!añadido){
                    comandaAgrupada.add(comandaAux2);
                }
            }
        }
        for (Comanda comanda : comandaAgrupada ) {
            Log.v("xzy", "comandaAGRUPADA: "+comanda.toString());
        }

        return comandaAgrupada;

    }

    private void cogerComandas() {
        viewModel.getLiveComandaList().observe(this, new Observer<List<Comanda>>() {
            @Override
            public void onChanged(List<Comanda> comandas) {
                comandaList = comandas;
                doPrint();
            }
        });
    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_icon);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

    private void doPrint() {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = this.getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null); //
    }

    private void cerrarFactura(){

        viewModel.getLiveFacturaList().observe(this, new Observer<List<Factura>>() {
            @Override
            public void onChanged(List<Factura> facturas) {
                if (facturas != null) {
                    for (Factura facturaList : facturas) {

                        if (facturaList.getId() == idFactura) {

                            factura = facturaList;
                            factura.setHoraCierra(obtenerHora());
                            factura.setIdEmpleadoCierre(idEmpleadoCierre);

                            viewModel.updateFactura(factura.getId(), factura, new OnRestListener() {
                                @Override
                                public void onSuccess(long id) {
                                }

                                @Override
                                public void onFailure(String message) {
                                }
                            });
                        }
                    }
                }
            }
        });
        //Log.v("xzy", "facturabuena"+factura.toString());

    }

    private String obtenerHora() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        return currentTime;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainImpresion.this, MesasLocal.class);
        startActivity(intent);
    }

    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        private Context context;
        private PrintedPdfDocument myPdfDocument;
        private Float totalFactura;
        private int pageHeight, pageWidth;
        private int totalpages = 1; //Habria que calcularlas

        public MyPrintDocumentAdapter(MainImpresion mainActivity) {
            this.context = mainActivity;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            myPdfDocument = new PrintedPdfDocument(context,
                    newAttributes);
            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72; /* /1000 para pasarlo a pulgadas y  X72 para convertirlo en punto tipografico*/
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new
                        PrintDocumentInfo
                                .Builder("factura"+idFactura+".pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);
                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

            PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

            PdfDocument.Page page = myPdfDocument.startPage(newPage);

            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                myPdfDocument.close();
                myPdfDocument = null;
                return;
            }

            //aqui se escribe
            //doPhotoPrint();
            drawPage(page);
            myPdfDocument.finishPage(page);

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }
            callback.onWriteFinished(pages); // cierra el trabajo e imprime la pagina
        }

        private void drawPage(PdfDocument.Page page) {
            Canvas canvas = page.getCanvas();



            // units are in points (1/72 of an inch)
            int titleBaseLine = 72;//
            int leftMargin = canvas.getWidth()/2;//
            int linea = 0;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.logo_icon);


            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTextAlign(Paint.Align.CENTER);

            paint.setTextSize(40);

            //canvas.drawBitmap(bitmap, leftMargin, 10 ,paint);


            canvas.drawText("Zaidín Gourmet", leftMargin, titleBaseLine+linea, paint);
            linea += 25;

            //CABECERA
            paint.setTextSize(16);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.FILL);
            leftMargin = 60;
            int leftMarginLine = 60;
            int rightMarginLine =  canvas.getWidth();
            linea += 50;

            String fecha = obtenerHora();

            canvas.drawText("fecha: "+fecha, leftMargin, titleBaseLine + linea, paint);
            linea += 25;
            canvas.drawText("Nº factura: "+idFactura, leftMargin, titleBaseLine + linea, paint);

            //cantidad
            linea += 50;
            paint.setTextSize(20);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText("C.", leftMargin, titleBaseLine + linea, paint);

            //concepto
            leftMargin += 40;
            canvas.drawText("CONCEPTO", leftMargin, titleBaseLine + linea, paint);

            //importe
            int rightMargin =  canvas.getWidth()-leftMargin;
            canvas.drawText("IMPORTE", rightMargin, titleBaseLine + linea, paint);

            paint.getTextBounds("IMPORTE", 0, "IMPORTE".length(), bounds);

            //precio
            rightMargin -= bounds.width()+40;
            int marginPrecio = rightMargin;
            canvas.drawText("PRECIO", rightMargin, titleBaseLine + linea, paint);
            canvas.drawLine(leftMarginLine, titleBaseLine + linea+10,rightMarginLine, titleBaseLine + linea+10, paint);
            linea += 15;
            paint.setStyle(Paint.Style.FILL);

            //DATOS
            totalFactura = 0f;

            comandaList = agruparComandas();
            for (Comanda comanda : comandaList) {

                if (comanda.getIdFactura() == idFactura) {

                    for (Producto producto : productoList) {

                        if (comanda.getIdProducto() == producto.getId()) {

                            totalFactura += producto.getPrecio() * comanda.getUnidades();

                            paint.setTextSize(16);
                            leftMargin = 60;
                            linea += 25;

                            //cantidad
                            canvas.drawText(String.valueOf(comanda.getUnidades()), leftMargin, titleBaseLine + linea, paint);

                            //concepto
                            leftMargin += 40;
                            canvas.drawText(producto.getNombre(), leftMargin, titleBaseLine + linea, paint);

                            //importe
                            Float importe = producto.getPrecio() * comanda.getUnidades();
                            rightMargin = canvas.getWidth() - leftMargin;
                            canvas.drawText(importe.toString(), rightMargin, titleBaseLine + linea, paint);

                            paint.getTextBounds(importe.toString(), 0, importe.toString().length(), bounds);

                            //precio
                            rightMargin -= bounds.width() + 40;
                            canvas.drawText(String.valueOf(producto.getPrecio()), marginPrecio, titleBaseLine + linea, paint);

                        }
                    }
                }
            }

            //total
            DecimalFormat df = new DecimalFormat("0.00");

            paint.setStyle(Paint.Style.FILL);
            linea += 25;

            canvas.drawLine(leftMarginLine, titleBaseLine + linea+5, rightMarginLine , titleBaseLine + linea+5, paint);
            linea += 25;

            Float iva = (totalFactura*21)/100;
            Float total = totalFactura+iva;
            canvas.drawText("I.V.A 21,00% ( "+totalFactura+" )  ",canvas.getWidth()/2, titleBaseLine + linea, paint);
            rightMargin = canvas.getWidth() - leftMargin;
            canvas.drawText(df.format(iva) +"",rightMargin, titleBaseLine + linea, paint);
            linea += 10;
            canvas.drawLine(leftMarginLine, titleBaseLine + linea+5, rightMarginLine , titleBaseLine + linea+5, paint);

            linea += 25;
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText("Total: "+df.format(total)+" €",canvas.getWidth()/2, titleBaseLine + linea, paint);
            paint.setStyle(Paint.Style.FILL);

            linea += 75;

            paint.setTextAlign(Paint.Align.CENTER);
            //canvas.drawText("Le antendió: "+"nombre empleado",canvas.getWidth()/2, titleBaseLine + linea, paint);
            linea += 25;
            canvas.drawText("GRACIAS POR SU VISITA",canvas.getWidth()/2, titleBaseLine + linea, paint);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Intent intent = new Intent(MainImpresion.this, MesasLocal.class);
            startActivity(intent);
            finish();
        }
    }//clase impresion
}
