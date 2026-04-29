package com.github.jpeg2000.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import com.github.jpeg2000.J2KWriter;

public class JPEG2000Writer extends ImageWriter
{
    public JPEG2000Writer(ImageWriterImpl imageWriterImpl)
    {
        super(imageWriterImpl);
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param)
    {
        return null;
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param)
    {
        return null;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param)
    {
        return null;
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param)
    {
        return null;
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage iioimage, ImageWriteParam param) throws IOException
    {
        //We can savely cast here, because the setOutput does not accept anything else.
        ImageOutputStream stream = (ImageOutputStream) getOutput();
        RenderedImage image = iioimage.getRenderedImage();
        BufferedImage bimage = null;
        if(image instanceof BufferedImage)
        {
            bimage = (BufferedImage) image;
        }
        else
        {
            bimage = convertToBufferedImage(image);
        }
        J2KWriter writer = new J2KWriter();
        writer.setCompressionRatio(15, false);
        writer.setSource(bimage, 128);
        writer.write(new Output(stream));       
    }

    /**
     * Translate between OutputStream and ImageOutputStream
     */
    class Output extends OutputStream
    {
        private ImageOutputStream stream;
        
        public Output(ImageOutputStream stream)
        {
            this.stream = stream;
        }

        @Override
        public void write(int b) throws IOException
        {
            stream.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            stream.write(b, off, len);
        }
        
        @Override
        public void close() throws IOException
        {
            stream.close();
        }
    }
    
    /**
     * Convert any other RenderedImage to BufferedImage
     * @param img
     * @return
     */
    private BufferedImage convertToBufferedImage(RenderedImage img)
    {
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if(keys != null)
        {
            for(String key : keys)
            {
                properties.put(key, img.getProperty(key));
            }
        }
        BufferedImage bimage = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return bimage;
    }
}
