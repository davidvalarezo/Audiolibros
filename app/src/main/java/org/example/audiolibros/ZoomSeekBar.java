package org.example.audiolibros;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomSeekBar extends View {
    // Valor a controlar
    private int val = 0; // valor seleccionado
    private int valMin = 0; // valor mínimo
    private int valMax = 200; // valor máximo
    private int escalaMin = 0; // valor mínimo visualizado
    private int escalaMax = 30; // valor máximo visualizado
    private int escalaIni = 0; // origen de la escala
    private int escalaRaya = 1; // cada cuantas unidades una rayas
    private int escalaRayaLarga = 10; // cada cuantas rayas una larga
    // Dimensiones en pixels
    private int altoNumeros;
    private int altoRegla;
    private int altoBar;
    private int altoPalanca;
    private int anchoPalanca;
    private int altoGuia;
    // Valores que indican donde dibujar
    private int xIni;
    private int yIni;
    private int ancho;
    // Objetos Rect con diferentes regiones
    private Rect escalaRect = new Rect();
    private Rect barRect = new Rect();
    private Rect guiaRect = new Rect();
    private Rect palancaRect = new Rect();
    // Objetos Paint globales para no tener que crearlos cada vez
    private Paint textoPaint = new Paint();
    private Paint reglaPaint = new Paint();
    private Paint guiaPaint = new Paint();
    private Paint palancaPaint = new Paint();

    private OnDesplazarListener escuchador;

    // Variables globales usadas en onTouchEvent()
    enum Estado { SIN_PULSACION, PALANCA_PULSADA, ESCALA_PULSADA, ESCALA_PULSADA_DOBLE };
    Estado estado = Estado.SIN_PULSACION;
    int antVal_0, antVal_1;

    private int centradovertical = 0;

    public ZoomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        float dp = getResources().getDisplayMetrics().density;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZoomSeekBar,
                0, 0);
        try {
            altoNumeros = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_altoNumeros, (int) (30 * dp));
            altoRegla = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_altoRegla, (int) (20 * dp));
            altoBar = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_altoBar, (int) (70 * dp));
            altoPalanca = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_altoPalanca, (int) (40 * dp));
            altoGuia = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_altoGuia, (int) (10 * dp));
            anchoPalanca = a.getDimensionPixelSize( R.styleable.ZoomSeekBar_anchoPalanca, (int) (20 * dp));
            textoPaint.setTextSize(a.getDimension( R.styleable.ZoomSeekBar_altoTexto, 16 * dp));
            textoPaint.setColor(a.getColor( R.styleable.ZoomSeekBar_colorTexto, Color.BLACK));
            reglaPaint.setColor(a.getColor( R.styleable.ZoomSeekBar_colorRegla, Color.BLACK));
            guiaPaint.setColor(a.getColor( R.styleable.ZoomSeekBar_colorGuia, Color.BLUE));
            palancaPaint.setColor(a.getColor( R.styleable.ZoomSeekBar_colorPalanca, 0xFF00007F));

            val = a.getInt(R.styleable.ZoomSeekBar_val, val);
            valMax = a.getInt(R.styleable.ZoomSeekBar_valMax, valMax);
            valMin = a.getInt(R.styleable.ZoomSeekBar_valMin, valMin);
            escalaMin = a.getInt(R.styleable.ZoomSeekBar_escalaMin, escalaMin);
            escalaMax = a.getInt(R.styleable.ZoomSeekBar_escalaMax, escalaMax);
            escalaIni = a.getInt(R.styleable.ZoomSeekBar_escalaIni, escalaIni);
            escalaRaya = a.getInt(R.styleable.ZoomSeekBar_escalaRaya, escalaRaya);
            escalaRayaLarga = a.getInt(R.styleable.ZoomSeekBar_escalaRayaLarga, escalaRayaLarga);
        }finally {
            a.recycle();
        }
        textoPaint.setAntiAlias(true);
        textoPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        xIni = getPaddingLeft();
        yIni = getPaddingTop();
        ancho = getWidth() - getPaddingRight() - getPaddingLeft();
        barRect.set(xIni, yIni, xIni + ancho, yIni + altoBar);
        escalaRect.set(xIni, yIni + altoBar, xIni + ancho,
                yIni + altoBar + altoNumeros + altoRegla);
        int y = yIni + (altoBar - altoGuia) / 2;
        guiaRect.set(xIni, y, xIni + ancho, y + altoGuia);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try{
            // Dibujamos Barra con palanca
            canvas.drawRect(guiaRect, guiaPaint);
            int y = yIni + (altoBar - altoPalanca) / 2 ;
            int x = xIni + ancho * (val - escalaMin) / (escalaMax - escalaMin) - anchoPalanca / 2;
            palancaRect.set(x, y, x + anchoPalanca, y + altoPalanca);
            canvas.drawRect(palancaRect, palancaPaint);
            palancaRect.set(x - anchoPalanca / 2, y, x + 3 * anchoPalanca / 2, y
                    + altoPalanca);

            // Dibujamos Escala
            int v = escalaIni;
            while (v <= escalaMax) {
                if (v >= escalaMin) {
                    x = xIni + ancho * (v - escalaMin) / (escalaMax - escalaMin);
                    if (((v - escalaIni) / escalaRaya) % escalaRayaLarga == 0) {
                        y = yIni + altoBar + altoRegla;
                        canvas.drawText(Integer.toString(v), x, y + altoNumeros, textoPaint);
                    } else {
                        y = yIni + altoBar + altoRegla * 1 / 3;
                    }
                    canvas.drawLine(x, yIni + altoBar, x, y, reglaPaint);
                }
                v += escalaRaya;
            }
        }catch (Exception ex){

        }


    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        int x_0, y_0, x_1, y_1; x_0 = (int) event.getX(0);
        y_0 = (int) event.getY(0);
        int val_0 = escalaMin + (x_0-xIni) * (escalaMax-escalaMin) / ancho;
        if (event.getPointerCount() > 1) {
            x_1 = (int) event.getX(1);
            y_1 = (int) event.getY(1);
        } else {
            x_1 = x_0; y_1 = y_0;
        }
        int val_1 = escalaMin + (x_1 - xIni) * (escalaMax - escalaMin) / ancho;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (palancaRect.contains(x_0, y_0)) {
                    estado = Estado.PALANCA_PULSADA;
                }
                else if (barRect.contains(x_0, y_0)) {
                    if (val_0 > val) val++; else val--; invalidate(barRect);
                    escuchador.onDesplazando(true, val);
                }
                else if (escalaRect.contains(x_0, y_0)) {
                    estado = Estado.ESCALA_PULSADA; antVal_0 = val_0;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                    if (estado == Estado.ESCALA_PULSADA) {
                        if (escalaRect.contains(x_1, y_1)) {
                            antVal_1 = val_1;
                            estado = Estado.ESCALA_PULSADA_DOBLE;
                        }
                    }
                break;
            case MotionEvent.ACTION_UP:
                estado = Estado.SIN_PULSACION;

                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                    estado = Estado.ESCALA_PULSADA;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (estado == Estado.PALANCA_PULSADA) {
                    val = ponDentroRango(val_0, escalaMin, escalaMax);
                    escuchador.onDesplazando(true, val);
                    invalidate(barRect);
                }
                if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                    escalaMin = antVal_0 + (xIni-x_0) * (antVal_0-antVal_1) / (x_0-x_1);
                    escalaMin = ponDentroRango(escalaMin, valMin, val);
                    escalaMax = antVal_0 + (ancho+xIni-x_0) * (antVal_0-antVal_1) / (x_0-x_1);
                    escalaMax = ponDentroRango(escalaMax, val, valMax);
                    invalidate();
                }
                break;
        }
        return true;
    }

    int ponDentroRango(int val, int valMin, int valMax) {
        if (val < valMin) {
            return valMin;
        }
        else if (val > valMax) {
            return valMax;
        } else { return val; }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int altoDeseado = altoNumeros + altoRegla + altoBar + getPaddingBottom() + getPaddingTop() ;
        int alto = obtenDimension(heightMeasureSpec, altoDeseado);
        if(alto > altoDeseado){
            yIni = yIni + (alto - altoDeseado)/2;
        }
        centradovertical = alto> altoDeseado ? (alto - altoDeseado)/2:0;
        int anchoDeseado = 2 * altoDeseado;
        int ancho = obtenDimension(widthMeasureSpec, anchoDeseado);
        setMeasuredDimension(ancho, alto);
    }

    private int obtenDimension(int measureSpec, int deseado) {
        int dimension = MeasureSpec.getSize(measureSpec);
        int modo = MeasureSpec.getMode(measureSpec);
        if (modo == MeasureSpec.EXACTLY) {
            return dimension;
        } else if (modo == MeasureSpec.AT_MOST) {
            return Math.min(dimension, deseado);
        } else { return deseado; }
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        if (valMin <= val && val <= valMax) {
            this.val = val;
            escalaMin = Math.min(escalaMin, val);
            escalaMax = Math.max(escalaMax, val);
            //invalidate();
            postInvalidate();
        }
    }

    public int getValMin() {
        return valMin;
    }

    public void setValMin(int valMin) {
        if(valMin< valMax && valMin >= 0){
            this.valMin = valMin;
           // invalidate();
        }else{
            this.valMin = 0;
           // invalidate();
        }
    }

    public int getValMax() {
        return valMax;
    }

    public void setValMax(int valMax) {
        if(valMin< valMax ){
            this.valMax = valMax;
            //invalidate();
        }else{
            this.valMax = val;
            //invalidate();
        }
    }

    public int getEscalaMin() {
        return escalaMin;
    }

    public void setEscalaMin(int escalaMin) {
        if (valMin <= escalaMin && escalaMin >=0) {
            this.escalaMin = escalaMin;
            //invalidate();
            postInvalidate();
        }else if (escalaMin < valMin){
            this.escalaMin = valMin;
            //invalidate();
            postInvalidate();
        }else {
            this.escalaMin = 0;
            //invalidate();
            postInvalidate();
        }
    }

    public int getEscalaMax() {
        return escalaMax;
    }

    public void setEscalaMax(int escalaMax) {
        if (escalaMax <= valMax && escalaMin < escalaMax) {
            this.escalaMax = escalaMax;
            //invalidate();
            postInvalidate();
        }else if (escalaMax > valMax){
            this.escalaMax = valMax;
            //invalidate();
            postInvalidate();
        }else {
            this.escalaMax = val;
            //invalidate();
            postInvalidate();
        }

    }

    public int getEscalaIni() {
        return escalaIni;
    }

    public void setEscalaIni(int escalaIni) {
        if(escalaIni >= 0 && escalaIni < valMin){
            this.escalaIni = escalaIni;
            invalidate();
        }else{
            escalaIni = 0;
            invalidate();
        }

    }

    public int getEscalaRaya() {
        return escalaRaya;
    }

    public void setEscalaRaya(int escalaRaya) {
        this.escalaRaya = escalaRaya;
    }

    public int getEscalaRayaLarga() {
        return escalaRayaLarga;
    }

    public void setEscalaRayaLarga(int escalaRayaLarga) {
        this.escalaRayaLarga = escalaRayaLarga;
    }

    public void setOnDesplazarListener(OnDesplazarListener escuchador) {
        this.escuchador = escuchador;
    }


    public interface OnDesplazarListener {
        void onDesplazar(boolean movimiento, int val);
        void onDesplazando(boolean movimiento, int val);
        void onError(String mensage);
    }

}
