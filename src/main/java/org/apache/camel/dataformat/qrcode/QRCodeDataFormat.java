/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.camel.dataformat.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author claus.straube
 */
public class QRCodeDataFormat implements DataFormat {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDataFormat.class);

    BarcodeFormat format = BarcodeFormat.QR_CODE;
    ImageType type = ImageType.PNG;
    int width = 100;
    int height = 100;
    String charset = "UTF-8";

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        String payload = ExchangeHelper.convertToMandatoryType(exchange, String.class, graph);
        LOG.debug("Payload --> " + payload);

        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new EnumMap<EncodeHintType, ErrorCorrectionLevel>(EncodeHintType.class);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(payload.getBytes(charset), charset),
                format, width, height, hintMap);
        MatrixToImageWriter.writeToStream(matrix, type.toString(), stream);
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        Map<DecodeHintType, ErrorCorrectionLevel> hintMap = new EnumMap<DecodeHintType, ErrorCorrectionLevel>(DecodeHintType.class);

        BufferedInputStream in = exchange.getContext().getTypeConverter().mandatoryConvertTo(BufferedInputStream.class, stream);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(in))));
        Result result = new MultiFormatReader().decode(bitmap, hintMap);
        return result.getText();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BarcodeFormat getFormat() {
        return format;
    }

    public void setFormat(BarcodeFormat format) {
        this.format = format;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    

}